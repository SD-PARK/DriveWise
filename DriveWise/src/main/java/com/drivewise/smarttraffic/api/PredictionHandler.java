package com.drivewise.smarttraffic.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.dto.LinkDTO;
import com.drivewise.smarttraffic.dto.PredictInputDTO;
import com.drivewise.smarttraffic.dto.TotalIndicatorsDTO;
import com.drivewise.smarttraffic.predictor.TravelTimePredictor;
import com.drivewise.smarttraffic.store.LinkStore;

@Component
public class PredictionHandler {
    private final TravelTimePredictor travelTimePredictor;
    private final LinkStore linkStore;
    
    @Autowired
    public PredictionHandler(
		TravelTimePredictor travelTimePredictor,
		LinkStore linkStore
    ) {
        this.travelTimePredictor = travelTimePredictor;
        this.linkStore = linkStore;
    }
	
	public List<TotalIndicatorsDTO> runPredict() {
		List<TotalIndicatorsDTO> result = new ArrayList<>();
		List<LinkDTO> linkList = linkStore.getLinkList();
		
		// 실시간 데이터 받아서 PredictInputDTO에 저장해야함
		// Map으로 가져오면 될 듯?
		// View 만들어야 함
		
		for (LinkDTO linkInfo: linkList) {
			TotalIndicatorsDTO totalIndicators = new TotalIndicatorsDTO();
			PredictInputDTO input = setPredictInputDTO(linkInfo);
			
			totalIndicators.setLinkId(linkInfo.getLinkId());
			
			int travelTime = travelTimePredictor.predict(input);
			totalIndicators.setPtt(travelTime);
			
			result.add(totalIndicators);
		}

		// DB에 저장도 해야함;
		// IndicatorRepository 만들어야겠다.
		return result;
	}
	
	public PredictInputDTO setPredictInputDTO(LinkDTO linkInfo) {
		PredictInputDTO result = new PredictInputDTO();
		
		result.setHour(0);
		result.setAverageSpeed(50);
		result.setLanes(linkInfo.getLanes());
		result.setMaxSpeedLimit(linkInfo.getMaxSpeedLimit());
		result.setLength(linkInfo.getLength());
		result.setVisitorCount(0);
		result.setTci(0.0);
		
		return result;
	}
}
