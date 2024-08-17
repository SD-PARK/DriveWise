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

import com.drivewise.smarttraffic.dto.LinkInfoDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class LinkRepository implements ILinkRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private WKTReader reader = new WKTReader();
	
	private RowMapper<LinkInfoDTO> linkInfoRowMapper() {
		return ((ResultSet rs, int rowNum) -> {
			LinkInfoDTO linkInfo = new LinkInfoDTO();
			linkInfo.setLinkId(rs.getLong("id"));
			linkInfo.setStartNodeId(rs.getLong("start_node_id"));
			linkInfo.setEndNodeId(rs.getLong("end_node_id"));
			linkInfo.setLanes(rs.getInt("lanes"));
			linkInfo.setName(rs.getString("name"));
			linkInfo.setRoadRankCode(rs.getInt("road_rank_code"));
			linkInfo.setRoadTypeCode(rs.getInt("road_type_code"));
			linkInfo.setRoadUse(rs.getBoolean("road_use"));
			linkInfo.setMaxSpeedLimit(rs.getInt("max_speed_limit"));
			linkInfo.setLength(rs.getDouble("length"));
			linkInfo.setAwsId(rs.getInt("aws_id"));
			linkInfo.setCoordinatesCount(rs.getInt("component_coordinates_count"));
			MultiLineString geometry = null;
			try {
				String str = rs.getString("geometry");
				geometry = (MultiLineString) reader.read(str);
			} catch (ParseException e) {}
			linkInfo.setGeometry(geometry);
			
			return linkInfo;
		});
	};

	@Override
	public LinkInfoDTO getLinkInfo(long id) {
		String sql = "SELECT * FROM links WHERE id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, linkInfoRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			log.warn("ID가 {}인 링크를 찾을 수 없습니다.", id);
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			log.warn("ID가 {}인 링크가 여러개 존재합니다.", id);
			return null;
		}
	}

	@Override
	public List<LinkInfoDTO> getAllLinkInfo() {
		String sql = "SELECT * FROM links";
		return jdbcTemplate.query(sql, linkInfoRowMapper());
	}
}
