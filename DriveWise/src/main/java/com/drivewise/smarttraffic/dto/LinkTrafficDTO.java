package com.drivewise.smarttraffic.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkTrafficDTO {
    private String result;
    
    @JsonProperty("info_cnt")
    private int infoCnt;
    
    private List<TrafficInfo> info;

	@Getter
	@Setter
    public static class TrafficInfo {
        @JsonProperty("link_id")
        private String linkId;

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
}
