package com.drivewise.smarttraffic.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class TotalIndicatorsDTO {
	long linkId;
	Double tci, tsi;
	Integer ptt, rei;
    private Timestamp dateTime;
}