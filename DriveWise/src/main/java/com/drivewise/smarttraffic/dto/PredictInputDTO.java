package com.drivewise.smarttraffic.dto;

import lombok.Data;

@Data
public class PredictInputDTO {
	int hour, averageSpeed, lanes, maxSpeedLimit, visitorCount;
	float length;
	double tci;
}
