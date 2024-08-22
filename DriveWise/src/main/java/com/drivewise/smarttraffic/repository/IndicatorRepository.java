package com.drivewise.smarttraffic.repository;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.drivewise.smarttraffic.dto.IndicatorDTO;
import com.drivewise.smarttraffic.dto.TotalIndicatorsDTO;

@Repository
public class IndicatorRepository implements IIndicatorRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void createIndicators(List<TotalIndicatorsDTO> indicators) {
	    String sql = "MERGE INTO link_indicators i " +
	                "USING (SELECT ? AS date_time, ? AS link_id, ? AS tci, ? AS tsi, ? AS predicted_travel_time, ? AS rei FROM dual) s " +
	                "ON (i.date_time = s.date_time AND i.link_id = s.link_id) " +
	                "WHEN MATCHED THEN " +
	                "    UPDATE SET i.tci = s.tci, i.tsi = s.tsi, i.predicted_travel_time = s.predicted_travel_time, i.rei = s.rei " +
	                "WHEN NOT MATCHED THEN " +
	                "    INSERT (date_time, link_id, tci, tsi, predicted_travel_time, rei) " +
	                "    VALUES (s.date_time, s.link_id, s.tci, s.tsi, s.predicted_travel_time, s.rei)";

	   jdbcTemplate.batchUpdate(sql, indicators, indicators.size(), (ps, indicator) -> {
	       Double tci = indicator.getTci();
	       Double tsi = indicator.getTsi();
	       Integer ptt = indicator.getPtt();
	       Integer rei = indicator.getRei();
	       
	       ps.setObject(1, indicator.getDateTime());
	       ps.setLong(2, indicator.getLinkId());
	       
	       if (tci != null) ps.setDouble(3, tci);
	       else ps.setNull(3, java.sql.Types.DOUBLE);
	       if (tsi != null) ps.setDouble(4, tsi);
	       else ps.setNull(4, java.sql.Types.DOUBLE);
	       if (ptt != null) ps.setInt(5, ptt);
	       else ps.setNull(5, java.sql.Types.INTEGER);
	       if (rei != null) ps.setInt(6, rei);
	       else ps.setNull(6, java.sql.Types.INTEGER);
	   });
	}

	@Override
	public List<IndicatorDTO> getRecentTCI() {
		return null;
	}

	@Override
	public List<IndicatorDTO> getRecentTSI() {
		return null;
	}

	@Override
	public List<IndicatorDTO> getRecentPTT() {
		List<IndicatorDTO> result;
		String sql = "SELECT date_time, link_id, predicted_travel_time " +
					 "FROM (SELECT date_time, link_id, predicted_travel_time, " +
					 "		ROW_NUMBER() OVER (PARTITION BY link_id ORDER BY date_time DESC) AS rn " +
					 "		FROM link_indicators)" +
					 "WHERE rn = 1";
		
		result = jdbcTemplate.query(sql, ((ResultSet rs, int rowNum) -> {
			IndicatorDTO indicator = new IndicatorDTO();
			indicator.setLinkId(rs.getLong("link_id"));
			indicator.setIndicator(rs.getInt("predicted_travel_time"));
			return indicator;
		}));
		
		return result;
	}

	@Override
	public List<IndicatorDTO> getRecentREI() {
		return null;
	}

	@Override
	public List<TotalIndicatorsDTO> getRecentIndicators() {
		List<TotalIndicatorsDTO> result;
		String sql = "SELECT date_time, link_id, tci, tsi, predicted_travel_time, rei " +
					 "FROM (SELECT date_time, link_id, tci, tsi, predicted_travel_time, rei, " +
					 "		ROW_NUMBER() OVER (PARTITION BY link_id ORDER BY date_time DESC) AS rn " +
					 "		FROM link_indicators)" +
					 "WHERE rn = 1";
		
		result = jdbcTemplate.query(sql, ((ResultSet rs, int rowNum) -> {
			TotalIndicatorsDTO total = new TotalIndicatorsDTO();
			total.setLinkId(rs.getLong("link_id"));
			total.setTci(rs.getDouble("tci"));
			total.setTsi(rs.getDouble("tsi"));
			total.setPtt(rs.getInt("predicted_travel_time"));
			total.setRei(rs.getInt("rei"));
			return total;
		}));
		
		return result;
	}

}
