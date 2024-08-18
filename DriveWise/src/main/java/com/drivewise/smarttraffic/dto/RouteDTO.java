package com.drivewise.smarttraffic.dto;

import org.locationtech.jts.geom.MultiLineString;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize(using = RouteDTOSerializer.class)
public class RouteDTO {
	String roadName;
	int tci, tsi, time, maxSpeed;
	float length;
	MultiLineString geometry;
}
