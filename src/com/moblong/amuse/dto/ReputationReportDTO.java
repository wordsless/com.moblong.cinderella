package com.moblong.amuse.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Reputation;

public final class ReputationReportDTO {

	public void save(final ApplicationContext context, final String aid, final Reputation reputation) {

		Connection con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("INSERT INTO t_reputation_base(uid, aid, alias, password, signature, telephone, latitude, longitude, registered, last) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstat.setString(1, aid);
			pstat.setString(2, UUID.randomUUID().toString().replace("-", ""));
			pstat.setString(3, reputation.getName());
			pstat.execute();
			con.commit();
			
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			if(pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstat = null;
			}
			
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
	
	}
	
}
