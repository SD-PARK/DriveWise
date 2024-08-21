package com.drivewise.smarttraffic.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.api.fetcher.IAPIFetcher;
import com.drivewise.smarttraffic.dto.LinkTrafficDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class APIFetcherInvoker {
    private final IAPIFetcher<LinkTrafficDTO> linkTrafficFetcher;
    
    @Autowired
    public APIFetcherInvoker(
        IAPIFetcher<LinkTrafficDTO> linkTrafficFetcher
    ) {
        this.linkTrafficFetcher = linkTrafficFetcher;
    }
	
	public void invoke() {
		LinkTrafficDTO linkTraffics = linkTrafficFetcher.fetch();
		log.info("실시간 교통정보 수집 완료");
		linkTrafficFetcher.save(linkTraffics);
		log.info("실시간 교통정보 저장 완료");
	}
}
