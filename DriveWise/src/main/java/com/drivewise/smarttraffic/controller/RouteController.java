package com.drivewise.smarttraffic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.drivewise.smarttraffic.dto.CoordinatesDTO;
import com.drivewise.smarttraffic.dto.RouteDTO;
import com.drivewise.smarttraffic.dto.serializer.RouteDTOSerializer;
import com.drivewise.smarttraffic.service.IRouteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Controller
public class RouteController {
	@Autowired
	private IRouteService routeService;
	
	@GetMapping("/route")
	public void getRoutePage() {}
	
	@GetMapping(value="/route/directions", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getRoute(CoordinatesDTO coor) {
        try {
			List<RouteDTO> routeList = routeService.findOptimalPath(coor);
	
			ObjectMapper mapper = new ObjectMapper();
	        SimpleModule module = new SimpleModule();
	        module.addSerializer(RouteDTO.class, new RouteDTOSerializer());
	        mapper.registerModule(module);
	        
	        Map<String, Object> featureCollection = new HashMap<>();
	        featureCollection.put("type", "FeatureCollection");
	        featureCollection.put("features", routeList);

            String result = mapper.writeValueAsString(featureCollection);
            return ResponseEntity.ok()
            		.contentType(MediaType.APPLICATION_JSON_UTF8)
            		.body(result);
        } catch (IllegalArgumentException e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Failed to process JSON\"}");
        }
	}
}