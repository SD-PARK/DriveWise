package com.drivewise.smrattraffic.pridiction;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

public class TravelTimePrediction {
	public int predict(int hour, double averageSpeed, int lanes, int maxSpeedLimit, double length, double tci) {
		// 임시로 넣어둠
        RConnection rConn = null;
        
        try {
            // Rserve 실행
            rConn = new RConnection();

            // 프로젝트 폴더 내의 R 스크립트 상대 경로 설정
            String rScriptPath = "C:/Users/bit/git/DriveWise/02-Data/02-R/travel_time_prediction.R";
            
            // R 스크립트 로드
            rConn.eval("source('" + rScriptPath + "')");

            // R에서 예측 수행
            REXP prediction = rConn.eval(String.format(
        	    "predictTravelTime(%d, %f, %d, %d, %f, %f)",
        	    hour, averageSpeed, lanes, maxSpeedLimit, length, tci
        	));
            
            // 예측 결과 가져오기
            int predictedTravelTime = prediction.asInteger();

            return predictedTravelTime;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (rConn != null) {
                try {
                    rConn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
	}
}