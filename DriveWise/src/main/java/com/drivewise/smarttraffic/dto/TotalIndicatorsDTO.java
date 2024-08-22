package com.drivewise.smarttraffic.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class TotalIndicatorsDTO {
	private long linkId;
	private Double tci, tsi;
	private Integer ptt, rei;
    private Timestamp dateTime;
}