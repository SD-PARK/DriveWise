package com.drivewise.smarttraffic.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drivewise.smarttraffic.dto.CoordinatesDTO;
import com.drivewise.smarttraffic.dto.LinkDTO;
import com.drivewise.smarttraffic.dto.RouteDTO;
import com.drivewise.smarttraffic.store.LinkStore;

@Service
public class RouteService implements IRouteService {
	@Autowired
	LinkStore linkStore;
    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

	@Override
	public void getRecentREIs() {
	}

	@Override
	public long findNearestRoad(double lng, double lat) {
        Long result = null;
		Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
        double nearestDistance = Double.MAX_VALUE;
        
        for (LinkDTO link : linkStore.getLinkList()) {
        	if (link.getGeometry() == null) continue;
        	
            MultiLineString geometry = link.getGeometry();

            double distance = DistanceOp.distance(geometry, point);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                result = link.getLinkId();
            }
        }

        return result;
	}

	@Override
	public List<RouteDTO> findOptimalPath(CoordinatesDTO coor) {
		Map<Long, LinkDTO> linkMap = linkStore.getLinkMap();
		long startLinkId = findNearestRoad(coor.getStartLng(), coor.getStartLat());
		long endLinkId = findNearestRoad(coor.getEndLng(), coor.getEndLat());
		List<RouteDTO> result = new LinkedList<>();
		
		if (startLinkId == endLinkId) throw new IllegalArgumentException("경로 간 거리가 너무 가깝습니다.");

		List<Long> path = Dajkstra(startLinkId, endLinkId);
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
	
	public List<Long> Dajkstra(long startLinkId, long endLinkId) {
		Map<Long, LinkDTO> linkMap = linkStore.getLinkMap();

        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previousLinks = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Long linkId : linkMap.keySet()) {
            distances.put(linkId, Double.MAX_VALUE);
        }

        distances.put(startLinkId, 0.0);
        pq.add(startLinkId);

        while (!pq.isEmpty()) {
            long currentLinkId = pq.poll();
            LinkDTO currentLink = linkMap.get(currentLinkId);

            if (currentLinkId == endLinkId) {
                break;
            }
            
            for (LinkDTO neighborLink : linkMap.values()) {
                if (neighborLink.getStartNodeId() == currentLink.getEndNodeId()) {
                    long neighborLinkId = neighborLink.getLinkId();
                    double newDist = distances.get(currentLinkId) + neighborLink.getLength();

                    if (newDist < distances.get(neighborLinkId)) {
                        distances.put(neighborLinkId, newDist);
                        previousLinks.put(neighborLinkId, currentLinkId);
                        pq.add(neighborLinkId);
                    }
                }
            }
        }

        // 경로 추적: 종료 링크부터 시작 링크까지 추적하여 경로를 생성
        List<Long> path = new LinkedList<>();
        for (Long at = endLinkId; at != null; at = previousLinks.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        
        return path;
	}
}
