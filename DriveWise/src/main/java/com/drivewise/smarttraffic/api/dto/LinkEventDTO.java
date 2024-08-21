package com.drivewise.smarttraffic.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LinkEventDTO {
	@JsonProperty("link_id")
	private long linkId;
	@JsonProperty("code")
	private int eventCode;
	
	private LocalDateTime dateTime;
}
