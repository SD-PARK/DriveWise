package com.drivewise.smarttraffic.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.fetcher.IAPIFetcher;
import com.drivewise.smarttraffic.dto.LinkTrafficDTO;

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
		LinkTrafficDTO linkTraffic = linkTrafficFetcher.fetch();
	}
}
