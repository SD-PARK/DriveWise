package com.drivewise.smarttraffic.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.api.APIFetcherInvoker;
import com.drivewise.smarttraffic.api.PredictionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataFetchingScheduler {
	@Autowired
	private APIFetcherInvoker apiFetcherInvoker;
	@Autowired
	private PredictionHandler predictionHandler;

	@Scheduled(cron = "0 0/1 * * * ?")
	public void runTasks() {
		log.info("작업 중");
		apiFetcherInvoker.invoke();
		predictionHandler.handling();
		log.info("작업 완료");
	}
}
