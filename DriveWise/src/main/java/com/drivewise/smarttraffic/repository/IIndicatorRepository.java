package com.drivewise.smarttraffic.repository;

import java.util.List;

import com.drivewise.smarttraffic.dto.IndicatorDTO;
import com.drivewise.smarttraffic.dto.TotalIndicatorsDTO;

public interface IIndicatorRepository {
	void createIndicators(List<TotalIndicatorsDTO> indicators);
	List<IndicatorDTO> getRecentTCI();
	List<IndicatorDTO> getRecentTSI();
	List<IndicatorDTO> getRecentPTT();
	List<IndicatorDTO> getRecentREI();
	List<TotalIndicatorsDTO> getRecentIndicators();
}