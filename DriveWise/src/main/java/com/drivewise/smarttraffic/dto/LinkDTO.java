package com.drivewise.smarttraffic.dto;

import org.locationtech.jts.geom.MultiLineString;

import lombok.Data;

@Data
public class LinkDTO {
	long linkId, startNodeId, endNodeId;
	int lanes, roadRankCode, roadTypeCode, maxSpeedLimit, awsId, coordinatesCount;
	String name;
	boolean roadUse;
	double length;
	MultiLineString geometry;
}
