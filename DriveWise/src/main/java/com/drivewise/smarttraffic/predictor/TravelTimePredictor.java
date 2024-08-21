package com.drivewise.smarttraffic.predictor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.dto.PredictInputDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TravelTimePredictor {
    private RConnection rConn = null;
	private static final String rScriptPath = "C:/Users/bit/git/DriveWise/02-Data/02-R/travel_time_prediction.R";
    
	@PostConstruct
	public void init() {
		try {
			rConn = new RConnection();
            rConn.eval("source('" + rScriptPath + "')");
		} catch (RserveException e) {
			log.error("R Connection을 연결하지 못했습니다: " + e.getMessage());
		}
	}
	
	@PreDestroy
	public void destroy() {
        try {
        	if (rConn != null) rConn.close();
        } catch (Exception e) {
        	log.error("R Connection을 종료하지 못했습니다: " + e.getMessage());
        }
	}
	
	public int predict(PredictInputDTO dto) {
		int result = 100;
		
		try {
	        REXP prediction = rConn.eval(String.format(
        	    "predictTravelTime(%d, %d, %d, %d, %f, %f)",
        	    dto.getHour(),
        	    dto.getAverageSpeed(),
        	    dto.getLanes(),
        	    dto.getMaxSpeedLimit(),
        	    dto.getLength(),
        	    dto.getTci()
        	));
	            
            result = prediction.asInteger();
            
            if (result > 100) result = 100;
            else if (result < 0) result = 0;
		} catch (Exception e) {
			log.error("도로 통행시간 예측 실패: ", e.getMessage());
		}

        return result;
	}
}