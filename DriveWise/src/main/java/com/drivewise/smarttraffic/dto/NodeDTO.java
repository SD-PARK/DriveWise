package com.drivewise.smarttraffic.dto;

import org.locationtech.jts.geom.Coordinate;

import lombok.Data;

@Data
public class NodeDTO {
	long nodeId, districtId;
	int nodeTypeCode;
	String name;
	Coordinate coordinate;
}
