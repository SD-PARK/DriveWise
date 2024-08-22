package com.drivewise.smarttraffic.dto;

import org.locationtech.jts.geom.MultiLineString;

import lombok.Data;

@Data
public class LinkDTO {
	private long linkId, startNodeId, endNodeId;
	private int lanes, roadRankCode, roadTypeCode, maxSpeedLimit, awsId, coordinatesCount;
	private String name;
	private boolean roadUse;
	private double length;
	private MultiLineString geometry;
}
