package com.drivewise.smarttraffic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.drivewise.smrattraffic.pridiction.TravelTimePrediction;

@Controller
public class RouteController {
	
	@GetMapping("/route")
	public void getRoute() {
		int time = new TravelTimePrediction().predict(10, 10.0, 1, 50, 120.0, 40.0);
		System.out.println(time);
	}
}