package com.drivewise.smarttraffic.dto;

import org.locationtech.jts.geom.MultiLineString;

import com.drivewise.smarttraffic.dto.serializer.RouteDTOSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize(using = RouteDTOSerializer.class)
public class RouteDTO {
	private String roadName;
	private Double tci, tsi;
	private Integer time, maxSpeed;
	private Float length;
	private MultiLineString geometry;
}
