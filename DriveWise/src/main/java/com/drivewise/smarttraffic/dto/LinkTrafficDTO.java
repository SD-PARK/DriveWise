package com.drivewise.smarttraffic.dto;

import java.util.List;

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
        private String prcnDt;
        
        private int tfvl;
        private int sped;
        
        @JsonProperty("ocpy_rate")
        private int ocpyRate;
        
        @JsonProperty("trvl_hh")
        private int trvlHh;
    }
}
