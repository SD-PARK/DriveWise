package com.drivewise.smarttraffic.service;

import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drivewise.smarttraffic.dto.CoordinatesDTO;
import com.drivewise.smarttraffic.dto.LinkInfoDTO;
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
        
        for (LinkInfoDTO linkInfo : linkStore.getAllLinkInfo()) {
        	if (linkInfo.getGeometry() == null) continue;
        	
            MultiLineString geometry = linkInfo.getGeometry();

            double distance = DistanceOp.distance(geometry, point);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                result = linkInfo.getLinkId();
            }
        }

        return result;
	}

	@Override
	public List<RouteDTO> findOptimalPath(CoordinatesDTO coor) {
		long startLinkId = findNearestRoad(coor.getStartLng(), coor.getStartLat());
		long endLinkId = findNearestRoad(coor.getEndLng(), coor.getEndLat());
		
		System.out.println("Start Link: " + linkStore.getLinkInfo(startLinkId));
		System.out.println("End Link: " + linkStore.getLinkInfo(endLinkId));
		return null;
	}
}
