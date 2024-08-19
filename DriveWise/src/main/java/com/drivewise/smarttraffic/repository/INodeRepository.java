package com.drivewise.smarttraffic.repository;

import java.util.List;

import com.drivewise.smarttraffic.dto.NodeDTO;

public interface INodeRepository {
	NodeDTO getNode(long id);
	List<NodeDTO> getAllNode();
}
