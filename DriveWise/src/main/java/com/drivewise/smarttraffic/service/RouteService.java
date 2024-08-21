package com.drivewise.smarttraffic.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drivewise.smarttraffic.dto.CoordinatesDTO;
import com.drivewise.smarttraffic.dto.IndicatorDTO;
import com.drivewise.smarttraffic.dto.LinkDTO;
import com.drivewise.smarttraffic.dto.NodeDTO;
import com.drivewise.smarttraffic.dto.RouteDTO;
import com.drivewise.smarttraffic.repository.IIndicatorRepository;
import com.drivewise.smarttraffic.store.LinkStore;
import com.drivewise.smarttraffic.store.NodeStore;

@Service
public class RouteService implements IRouteService {
	@Autowired
	private LinkStore linkStore;
	@Autowired
	private NodeStore nodeStore;
	@Autowired
	private IIndicatorRepository indicatorRepository;
	
	private Map<Long, IndicatorDTO> reiMap;
	private LocalDateTime lastExecutionTime;
	private GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

	@Override
	public void getRecentREIs() {
		LocalDateTime currentExecutionTime = LocalDateTime.now()
				.truncatedTo(ChronoUnit.MINUTES)
				.withMinute((LocalDateTime.now().getMinute() / 5) * 5);
		
		if (
			lastExecutionTime != null &&
			ChronoUnit.MINUTES.between(lastExecutionTime, currentExecutionTime) < 5
		) {
            return;
        }
		
		reiMap.clear();
		List<IndicatorDTO> reiList = indicatorRepository.getRecentPTT(); // REI 도출식 완성 전까지 PTT로 대체
		reiMap = reiList.stream()
				.collect(Collectors.toMap(IndicatorDTO::getLinkId, Function.identity()));
		
		lastExecutionTime = currentExecutionTime;
	}

	@Override
	public List<RouteDTO> findOptimalPath(CoordinatesDTO coor) {
		Map<Long, LinkDTO> linkMap = linkStore.getLinkMap();
		long startNodeId = findNearestNode(coor.getStartLng(), coor.getStartLat());
		long endNodeId = findNearestNode(coor.getEndLng(), coor.getEndLat());
		List<RouteDTO> result = new LinkedList<>();
		
		if (startNodeId == endNodeId) throw new IllegalArgumentException("경로 간 거리가 너무 가깝습니다.");

		getRecentREIs(); // 5분마다 첫 요청하는 사용자는 검색 속도가 느리다고 느낄 듯
		List<Long> path = getRouteLinkIds(startNodeId, endNodeId);
		for (long linkId: path) {
			LinkDTO link = linkMap.get(linkId);
			RouteDTO route = new RouteDTO();

			route.setRoadName(link.getName());
			route.setMaxSpeed(link.getMaxSpeedLimit());
			route.setLength((float) link.getLength());
			route.setGeometry(link.getGeometry());
			result.add(route);
		}
		
        return result;
	}

	@Override
	public long findNearestNode(double lng, double lat) {
        Long result = null;
		Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
        double nearestDistance = Double.MAX_VALUE;
        
        for (NodeDTO node : nodeStore.getNodeList()) {
        	Coordinate nodeCoor = node.getCoordinate();
        	if (nodeCoor == null) continue;
        	
            Point nodePoint = geometryFactory.createPoint(nodeCoor);

            double distance = point.distance(nodePoint);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                result = node.getNodeId();
            }
        }

        return result;
	}
	
	public List<Long> getRouteLinkIds(long startNodeId, long endNodeId) {
		Map<Long, NodeDTO> nodeMap = nodeStore.getNodeMap();
		Map<Long, LinkDTO> linkMap = linkStore.getLinkMap();

        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previousLinks = new HashMap<>();
        Map<Long, Long> previousNodes = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Long nodeId : nodeMap.keySet()) {
            distances.put(nodeId, Double.MAX_VALUE);
        }

        distances.put(startNodeId, 0.0);
        pq.add(startNodeId);

        while (!pq.isEmpty()) {
            long currentNodeId = pq.poll();

            if (currentNodeId == endNodeId) break;
            
            for (LinkDTO neighborLink : linkMap.values()) {
                if (neighborLink.getStartNodeId() == currentNodeId) {
                    long neighborNodeId = neighborLink.getEndNodeId();
                    long neighborLinkId = neighborLink.getLinkId();
                    double newDist = distances.get(currentNodeId) + reiMap.get(neighborLinkId).getIndicator();

                    if (newDist < distances.get(neighborNodeId)) {
                        distances.put(neighborNodeId, newDist);
                        previousLinks.put(neighborNodeId, neighborLinkId);
                        previousNodes.put(neighborNodeId, currentNodeId);
                        pq.add(neighborNodeId);
                    }
                }
            }
        }

        List<Long> path = new LinkedList<>();
        Long currentNode = endNodeId;

        while (currentNode != null && previousNodes.containsKey(currentNode)) {
            Long linkId = previousLinks.get(currentNode);
            if (linkId != null) {
                path.add(0, linkId);
            }
            currentNode = previousNodes.get(currentNode);
        }
        
        return path;
	}
}
