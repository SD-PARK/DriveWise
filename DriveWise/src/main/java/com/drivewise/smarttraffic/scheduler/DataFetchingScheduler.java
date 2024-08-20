package com.drivewise.smarttraffic.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.invoker.APIFetcherInvoker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataFetchingScheduler {
	@Autowired
	APIFetcherInvoker apiFetcherInvoker;
	
	public static final long SCHEDULE_INTERVAL_MS = 300000;
	
	@Scheduled(fixedRate = SCHEDULE_INTERVAL_MS)
	public void runTasks() {
		log.info("작업 실행 중");
		apiFetcherInvoker.invoke();
	}
}
