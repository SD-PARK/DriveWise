package com.drivewise.smarttraffic.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.api.dto.TrafficInfoDTO;
import com.drivewise.smarttraffic.dto.LinkDTO;
import com.drivewise.smarttraffic.dto.PredictInputDTO;
import com.drivewise.smarttraffic.dto.TotalIndicatorsDTO;
import com.drivewise.smarttraffic.predictor.TravelTimePredictor;
import com.drivewise.smarttraffic.repository.IIndicatorRepository;
import com.drivewise.smarttraffic.repository.ILinkTrafficRepository;
import com.drivewise.smarttraffic.store.LinkStore;

@Component
public class PredictionHandler {
    private final TravelTimePredictor travelTimePredictor;
    private final ILinkTrafficRepository linkTrafficRepository;
    private final IIndicatorRepository indicatorRepository;
    private final LinkStore linkStore;
    
    @Autowired
    public PredictionHandler(
		TravelTimePredictor travelTimePredictor,
		ILinkTrafficRepository linkTrafficRepository,
		IIndicatorRepository indicatorRepository,
		LinkStore linkStore
    ) {
        this.travelTimePredictor = travelTimePredictor;
        this.linkTrafficRepository = linkTrafficRepository;
        this.indicatorRepository = indicatorRepository;
        this.linkStore = linkStore;
    }
    
    public void handling() {
    	List<TotalIndicatorsDTO> indicators = getPredictResults();    	
		indicatorRepository.createIndicators(indicators);
    }
	
	public List<TotalIndicatorsDTO> getPredictResults() {
		List<TotalIndicatorsDTO> result = new ArrayList<>();
		List<LinkDTO> linkList = linkStore.getLinkList();
		List<TrafficInfoDTO> trafficInfoList = linkTrafficRepository.getResentLinkTraffics();
		Map<Long, TrafficInfoDTO> trafficInfoMap = trafficInfoList.stream()
				.collect(Collectors.toMap(TrafficInfoDTO::getLinkId, Function.identity()));
		
		for (LinkDTO linkInfo: linkList) {
			long linkId = linkInfo.getLinkId();
			
			TrafficInfoDTO trafficInfo = trafficInfoMap.get(linkId);
			if (trafficInfo == null) {
				continue;
			}
			
			TotalIndicatorsDTO totalIndicators = new TotalIndicatorsDTO();
			PredictInputDTO input = setPredictInputDTO(linkInfo, trafficInfo);

			totalIndicators.setLinkId(linkId);
			totalIndicators.setDateTime(trafficInfo.getDateTime());
			int travelTime = travelTimePredictor.predict(input);
			totalIndicators.setPtt(travelTime);

			result.add(totalIndicators);
		}
		
		return result;
	}
	
	public PredictInputDTO setPredictInputDTO(LinkDTO linkInfo, TrafficInfoDTO trafficInfo) {
		if (trafficInfo.getDateTime() == null) return null;
		
		PredictInputDTO result = new PredictInputDTO();
		
		int hour = trafficInfo.getDateTime().toLocalDateTime().getHour();
		result.setHour(hour);
		result.setAverageSpeed(trafficInfo.getSpeed());
		result.setLanes(linkInfo.getLanes());
		result.setMaxSpeedLimit(linkInfo.getMaxSpeedLimit());
		result.setLength(linkInfo.getLength());
		result.setVisitorCount(0);
		result.setTci(0.0);
		
		return result;
	}
}
