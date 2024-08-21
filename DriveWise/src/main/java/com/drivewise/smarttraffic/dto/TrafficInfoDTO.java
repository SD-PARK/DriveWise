package com.drivewise.smarttraffic.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficInfoDTO {
    @JsonProperty("link_id")
    private long linkId;

    @JsonProperty("prcn_dt")
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private Timestamp dateTime;

    @JsonProperty("tfvl")
    private int trafficVolume;
    
    @JsonProperty("sped")
    private int speed;
    
    @JsonProperty("ocpy_rate")
    private int occupancy;
    
    @JsonProperty("trvl_hh")
    private int travelTime;
}