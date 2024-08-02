package com.drivewise.smarttraffic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {
	
	@GetMapping("/route")
	public void getRoute() {}
}