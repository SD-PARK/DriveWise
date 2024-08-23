package com.drivewise.smarttraffic.api.fetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.drivewise.smarttraffic.api.dto.LinkTrafficDTO;
import com.drivewise.smarttraffic.repository.ILinkTrafficRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LinkTrafficFetcher implements IAPIFetcher<LinkTrafficDTO> {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ILinkTrafficRepository linkTrafficRepository;
	
	@Value("${jeju.its.api.key}")
	private String apiKey;
    private final static String BASE_URL = "http://api.jejuits.go.kr/api/getFrafficInfo";
	
	@Override
	public LinkTrafficDTO fetch() {
		LinkTrafficDTO result = null;
		try {
			String urlWithData = String.format("%s?code=%s", BASE_URL, apiKey);
			result = restTemplate.getForObject(urlWithData, LinkTrafficDTO.class);
        } catch (Exception e) {
            log.error("API 요청 중 오류 발생", e.getMessage());
        }
		return result;
	}
	
	@Override
	public void save(LinkTrafficDTO dto) {
		linkTrafficRepository.createLinkTraffics(dto);
	}
}
