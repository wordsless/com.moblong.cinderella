package com.moblong.prophet.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

public final class MaterialsDTO {

	public final void save(final ApplicationContext context, final String pid, final String aid, final String type, final String desc) {
		DataSource ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstat = null;
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("INSERT INTO t_materials_base(aid, typed, desc, pid) VALUES(?, ?, ?, ?)");
			pstat.setString(1, aid);
			pstat.setString(2, type);
			pstat.setString(3, desc);
			pstat.setString(4, pid);
			pstat.execute();
			pstat.close();
			pstat = null;
			con.commit();
			con.close();
			con = null;
		} catch(SQLException e) {
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
