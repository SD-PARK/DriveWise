package com.drivewise.smarttraffic.api.dto;

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
    
    private List<TrafficInfoDTO> info;
}
