package com.drivewise.smarttraffic.dto;

import org.locationtech.jts.geom.MultiLineString;

import lombok.Data;

@Data
public class RouteDTO {
	String roadName;
	int tci, tsi, time, maxSpeed;
	float length;
	MultiLineString geometry;
}
