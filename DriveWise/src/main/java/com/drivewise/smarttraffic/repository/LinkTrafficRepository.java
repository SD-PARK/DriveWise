package com.drivewise.smarttraffic.repository;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.drivewise.smarttraffic.api.dto.LinkTrafficDTO;
import com.drivewise.smarttraffic.api.dto.TrafficInfoDTO;

@Repository
public class LinkTrafficRepository implements ILinkTrafficRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<TrafficInfoDTO> TrafficInfoMapper() {
		return ((ResultSet rs, int rowNum) -> {
			TrafficInfoDTO trafficInfo = new TrafficInfoDTO();
			trafficInfo.setDateTime(rs.getTimestamp("date_time"));
			trafficInfo.setLinkId(rs.getLong("id"));
			trafficInfo.setTravelTime(rs.getInt("travel_time"));
			trafficInfo.setSpeed(rs.getInt("speed"));
			trafficInfo.setTrafficVolume(rs.getInt("traffic_volume"));
			trafficInfo.setOccupancy(rs.getInt("occupancy"));
			
			return trafficInfo;
		});
	};
	
	@Override
	public void createLinkTraffics(LinkTrafficDTO dto) {
	    List<TrafficInfoDTO> trafficInfoList = dto.getInfo();
	    String sql = "MERGE INTO link_traffic_details t " +
	                 "USING (SELECT ? AS date_time, ? AS id, ? AS travel_time, ? AS speed, ? AS traffic_volume, ? AS occupancy FROM dual) s " +
	                 "ON (t.date_time = s.date_time AND t.id = s.id) " +
	                 "WHEN MATCHED THEN " +
	                 "    UPDATE SET t.travel_time = s.travel_time, t.speed = s.speed, t.traffic_volume = s.traffic_volume, t.occupancy = s.occupancy " +
	                 "WHEN NOT MATCHED THEN " +
	                 "    INSERT (date_time, id, travel_time, speed, traffic_volume, occupancy) " +
	                 "    VALUES (s.date_time, s.id, s.travel_time, s.speed, s.traffic_volume, s.occupancy)";

	    jdbcTemplate.batchUpdate(sql, trafficInfoList, trafficInfoList.size(), (ps, trafficInfo) -> {
	        ps.setObject(1, trafficInfo.getDateTime());
	        ps.setLong(2, trafficInfo.getLinkId());
	        ps.setInt(3, trafficInfo.getTravelTime());
	        ps.setInt(4, trafficInfo.getSpeed());
	        ps.setInt(5, trafficInfo.getTrafficVolume());
	        ps.setInt(6, trafficInfo.getOccupancy());
	    });
	}

	@Override
	public List<TrafficInfoDTO> getResentLinkTraffics() {
		List<TrafficInfoDTO> result;
		String sql = "SELECT date_time, id, travel_time, speed, traffic_volume, occupancy " +
					 "FROM (SELECT date_time, id, travel_time, speed, traffic_volume, occupancy, " +
					 "		ROW_NUMBER() OVER (PARTITION BY id ORDER BY date_time DESC) AS rn " +
					 "		FROM link_traffic_details)" +
					 "WHERE rn = 1";
		
		result = jdbcTemplate.query(sql, TrafficInfoMapper());
		
		return result;
	}
}
