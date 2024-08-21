package com.drivewise.smarttraffic.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.drivewise.smarttraffic.dto.LinkTrafficDTO;

@Repository
public class LinkTrafficRepository implements ILinkTrafficRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void createLinkTraffics(LinkTrafficDTO dto) {
		List<LinkTrafficDTO.TrafficInfo> trafficInfoList = dto.getInfo();
		String sql = "INSERT INTO link_traffic_details (date_time, id, travel_time, speed, traffic_volume, occupancy) " +
	            	"VALUES (?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql, trafficInfoList, trafficInfoList.size(), (ps, trafficInfo) -> {
			ps.setObject(1, trafficInfo.getDateTime());
			ps.setString(2, trafficInfo.getLinkId());
			ps.setInt(3, trafficInfo.getTravelTime());
			ps.setInt(4, trafficInfo.getSpeed());
			ps.setInt(5, trafficInfo.getTrafficVolume());
			ps.setInt(6, trafficInfo.getOccupancy());
		});
	}
}
