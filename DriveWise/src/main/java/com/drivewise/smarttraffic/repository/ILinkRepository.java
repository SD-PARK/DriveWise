package com.drivewise.smarttraffic.repository;

import java.util.List;

import com.drivewise.smarttraffic.dto.LinkDTO;

public interface ILinkRepository {
	LinkDTO getLink(long id);
	List<LinkDTO> getAllLink();
}
