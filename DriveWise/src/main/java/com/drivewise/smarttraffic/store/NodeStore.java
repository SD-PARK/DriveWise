package com.drivewise.smarttraffic.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivewise.smarttraffic.dto.NodeDTO;
import com.drivewise.smarttraffic.repository.INodeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NodeStore {
	@Autowired
	private INodeRepository nodeRepository;
	private Map<Long, NodeDTO> nodes;

    @PostConstruct
	public void init() {
		loadNodes();
	}
	
	public boolean loadNodes() {
		try {
			nodes = new HashMap<>();
			List<NodeDTO> NodeList = nodeRepository.getAllNode();
			
			for (NodeDTO Node: NodeList)
				nodes.put(Node.getNodeId(), Node);

			log.info("노드 정보를 업데이트했습니다");
			return true;
		} catch (Exception e) {
			log.info("노드 정보 업데이트에 실패했습니다");
			return false;
		}
	}
	
	public NodeDTO getNode(long id) {
		return nodes.get(id);
	}
	
	public List<NodeDTO> getNodeList() {
		List<NodeDTO> NodeList = new ArrayList<>(nodes.values());
		return NodeList;
	}
	
	public Map<Long, NodeDTO> getNodeMap() {
		return nodes;
	}
}