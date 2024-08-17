package com.drivewise.smarttraffic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.drivewise.smarttraffic.dto.CoordinatesDTO;
import com.drivewise.smarttraffic.dto.RouteDTO;
import com.drivewise.smarttraffic.service.IRouteService;

@Controller
public class RouteController {
	@Autowired
	IRouteService routeService;
	
	@GetMapping("/route")
	public void getRoutePage() {}
	
	@GetMapping("/route/directions")
	public ResponseEntity<List<RouteDTO>> getRoute(CoordinatesDTO coor) {
		List<RouteDTO> result = routeService.findOptimalPath(coor);
		return null;
	}
}