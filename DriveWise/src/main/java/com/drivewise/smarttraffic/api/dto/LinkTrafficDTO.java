package com.drivewise.smarttraffic.api.dto;


import java.util.Date;

import lombok.Data;

@Data
public class LinkTrafficDTO {
	private long linkId;
	private Date prcn_dt;
	private int tfvl;
	private int averageSpeed;
	private int sped;
	
}
