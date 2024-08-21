package com.drivewise.smarttraffic.dto;

import lombok.Data;

@Data
public class PredictInputDTO {
	// Null 여부를 판별해야 하는 데이터는 참조 타입, 그 외는 기본 타입 지정
	int hour, averageSpeed, lanes, maxSpeedLimit;
	double length;
	Integer visitorCount;
	Double tci;
}
