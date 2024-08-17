package com.drivewise.smarttraffic.repository;

import java.util.List;

import com.drivewise.smarttraffic.dto.LinkInfoDTO;

public interface ILinkRepository {
	LinkInfoDTO getLinkInfo(long id);
	List<LinkInfoDTO> getAllLinkInfo();
}
