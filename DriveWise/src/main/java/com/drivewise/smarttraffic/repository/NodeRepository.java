package com.drivewise.smarttraffic.repository;

import java.sql.ResultSet;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.drivewise.smarttraffic.dto.NodeDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class NodeRepository implements INodeRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<NodeDTO> nodeRowMapper() {
		return ((ResultSet rs, int rowNum) -> {
			NodeDTO node = new NodeDTO();
			node.setNodeId(rs.getLong("id"));
			node.setDistrictId(rs.getLong("district_id"));
			node.setNodeTypeCode(rs.getInt("node_type_code"));
			node.setName(rs.getString("name"));
			Coordinate coordinate = new Coordinate(
				rs.getDouble("longitude"),
				rs.getDouble("latitude")
			);
			node.setCoordinate(coordinate);
			return node;
		});
	};

	@Override
	public NodeDTO getNode(long id) {
		String sql = "SELECT * FROM nodes WHERE id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, nodeRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			log.warn("ID가 {}인 노드를 찾을 수 없습니다.", id);
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			log.warn("ID가 {}인 노드가 여러개 존재합니다.", id);
			return null;
		}
	}

	@Override
	public List<NodeDTO> getAllNode() {
		String sql = "SELECT * FROM nodes";
		return jdbcTemplate.query(sql, nodeRowMapper());
	}
}
