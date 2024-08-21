package com.drivewise.smarttraffic.repository;

import java.sql.ResultSet;
import java.util.List;

import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.drivewise.smarttraffic.dto.LinkDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class LinkRepository implements ILinkRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private WKTReader reader = new WKTReader();
	
	private RowMapper<LinkDTO> LinkRowMapper() {
		return ((ResultSet rs, int rowNum) -> {
			LinkDTO link = new LinkDTO();
			link.setLinkId(rs.getLong("id"));
			link.setStartNodeId(rs.getLong("start_node_id"));
			link.setEndNodeId(rs.getLong("end_node_id"));
			link.setLanes(rs.getInt("lanes"));
			link.setName(rs.getString("name"));
			link.setRoadRankCode(rs.getInt("road_rank_code"));
			link.setRoadTypeCode(rs.getInt("road_type_code"));
			link.setRoadUse(rs.getBoolean("road_use"));
			link.setMaxSpeedLimit(rs.getInt("max_speed_limit"));
			link.setLength(rs.getDouble("length"));
			link.setAwsId(rs.getInt("aws_id"));
			link.setCoordinatesCount(rs.getInt("component_coordinates_count"));
			MultiLineString geometry = null;
			try {
				String str = rs.getString("geometry");
				geometry = (MultiLineString) reader.read(str);
			} catch (ParseException e) {}
			link.setGeometry(geometry);
			
			return link;
		});
	};

	@Override
	public LinkDTO getLink(long id) {
		String sql = "SELECT * FROM links l WHERE l.id NOT IN (SELECT id FROM unsupported_links) AND l.id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, LinkRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			log.warn("ID가 {}인 링크를 찾을 수 없습니다.", id);
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			log.warn("ID가 {}인 링크가 여러개 존재합니다.", id);
			return null;
		}
	}

	@Override
	public List<LinkDTO> getAllLink() {
		String sql = "SELECT * FROM links l WHERE l.id NOT IN (SELECT id FROM unsupported_links)";
		return jdbcTemplate.query(sql, LinkRowMapper());
	}
}
