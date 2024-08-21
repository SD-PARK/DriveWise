package com.drivewise.smarttraffic.api.fetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.drivewise.smarttraffic.dto.LinkDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LinkEventFetcher {

    private static final Logger logger = LoggerFactory.getLogger(LinkEventFetcher.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public LinkEventFetcher() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @SuppressWarnings("unlikely-arg-type")
	public List<LinkDTO> getValidLinkEvents(String code) {
        String apiUrl = "http://api.jejuits.go.kr/api/infoRoadEventList?code=" + code;
        logger.info("Calling API: {}", apiUrl); // API 호출 전에 로그 출력
        
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        
        logger.info("API Response Status: {}", response.getStatusCodeValue()); // 응답 상태 코드 로그 출력
        logger.info("API Response Body: {}", response.getBody()); // 응답 본문 로그 출력
        
        List<LinkDTO> infoList = null;

        try {
            // JSON 전체를 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            // info 배열을 JsonNode로 추출
            JsonNode infoNode = rootNode.path("info");

            // info 배열을 List<LinkEventDTO>로 변환
            infoList = objectMapper.readValue(
                    infoNode.toString(),
                    new com.fasterxml.jackson.core.type.TypeReference<List<LinkDTO>>() {}
            );

            // link_id가 -1이 아닌 항목만 필터링
            infoList = infoList.stream()
                    .filter(info -> !"-1".equals(info.getLinkId()))
                    .collect(Collectors.toList());

            logger.info("Filtered Link Events Count: {}", infoList.size()); // 필터링된 결과 개수 로그 출력

        } catch (Exception e) {
            logger.error("Error processing API response", e); // 오류 발생 시 로그 출력
        }

        return infoList;
    }
}
