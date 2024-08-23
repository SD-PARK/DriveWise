package com.drivewise.smarttraffic.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.api.dto.LinkTrafficDTO;
import com.drivewise.smarttraffic.api.fetcher.IAPIFetcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class APIFetcherInvoker {
    private List<IAPIFetcher<?>> fetcherList;
    
    @Autowired
    public APIFetcherInvoker(
        IAPIFetcher<LinkTrafficDTO> linkTrafficFetcher
    ) {
    	this.fetcherList = new ArrayList<>();
        this.fetcherList.add(linkTrafficFetcher);
    }
	
	public void invoke() {
		for (IAPIFetcher<?> fetcher: fetcherList) {
			invokeFetcher(fetcher);
		}
		log.info("실시간 데이터 저장 완료");
	}

    private <T> void invokeFetcher(IAPIFetcher<T> fetcher) {
        T data = fetcher.fetch();
        fetcher.save(data);
    }
}
