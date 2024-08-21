package com.drivewise.smarttraffic.API.dto;

import java.util.List;

import com.drivewise.smarttraffic.API.Fetcher.LinkEventFetcher;




public class test {
	 public static void main(String[] args) {
	        LinkEventFetcher fetcher = new LinkEventFetcher();

	        // 테스트를 위해 특정 코드로 API 호출
	        String testCode = "860653";
	        List<LinkEventDTO> events = fetcher.getValidLinkEvents(testCode);

	        // 결과 출력
	        events.forEach(event -> {
	            System.out.println("Link ID: " + event.getLinkId());
	            System.out.println("Code: " + event.getEventCode());
	            System.out.println("-----------");
	        });
	    }

}
