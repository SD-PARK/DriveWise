package com.drivewise.smarttraffic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.drivewise.smrattraffic.pridiction.TravelTimePrediction;

@Controller
public class RouteController {
	
	@GetMapping("/route")
	public void getRoute() {}
}