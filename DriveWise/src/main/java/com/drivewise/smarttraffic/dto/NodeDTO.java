package com.drivewise.smarttraffic.dto;

import org.locationtech.jts.geom.Coordinate;

import lombok.Data;

@Data
public class NodeDTO {
	private long nodeId, districtId;
	private int nodeTypeCode;
	private String name;
	private Coordinate coordinate;
}
