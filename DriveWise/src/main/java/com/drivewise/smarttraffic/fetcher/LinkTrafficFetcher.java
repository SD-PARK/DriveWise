package com.drivewise.smarttraffic.fetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.drivewise.smarttraffic.dto.LinkTrafficDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LinkTrafficFetcher implements IAPIFetcher<LinkTrafficDTO> {
	@Autowired
	private RestTemplate restTemplate;
	
    private ObjectMapper objectMapper;
    
    public LinkTrafficFetcher() {
    	this.objectMapper = new ObjectMapper();
    }
	
	@Override
	public LinkTrafficDTO fetch() {
		String url = "http://api.jejuits.go.kr/api/getFrafficInfo?code=860651";
		LinkTrafficDTO result = null;
		try {
            String response = restTemplate.getForObject(url, String.class);
            result = objectMapper.readValue(response, LinkTrafficDTO.class);
        } catch (Exception e) {
            log.error("API 요청 중 오류 발생", e); // 예외 전체를 로그에 기록
        }
		return result;
	}
}
