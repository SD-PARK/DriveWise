package com.drivewise.smarttraffic.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.dto.LinkDTO;
import com.drivewise.smarttraffic.repository.ILinkRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LinkStore {
	@Autowired
	private ILinkRepository linkRepository;
	private Map<Long, LinkDTO> links;

    @PostConstruct
	public void init() {
		loadLinks();
	}
	
	public boolean loadLinks() {
		try {
			links = new HashMap<>();
			List<LinkDTO> linkList = linkRepository.getAllLink();
			
			for (LinkDTO link: linkList)
				links.put(link.getLinkId(), link);
			
			log.info("링크 정보를 업데이트했습니다");
			return true;
		} catch (Exception e) {
			log.info("링크 정보 업데이트에 실패했습니다");
			return false;
		}
	}
	
	public LinkDTO getLink(long id) {
		return links.get(id);
	}
	
	public List<LinkDTO> getLinkList() {
		List<LinkDTO> linkList = new ArrayList<>(links.values());
		return linkList;
	}
	
	public Map<Long, LinkDTO> getLinkMap() {
		return links;
	}
}