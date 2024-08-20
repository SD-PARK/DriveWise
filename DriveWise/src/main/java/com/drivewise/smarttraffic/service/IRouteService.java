package com.drivewise.smarttraffic.service;

import java.util.List;

import com.drivewise.smarttraffic.dto.CoordinatesDTO;
import com.drivewise.smarttraffic.dto.RouteDTO;

public interface IRouteService {
	void getRecentREIs();
	long findNearestNode(double lng, double lat);
	List<RouteDTO> findOptimalPath(CoordinatesDTO coor);
}