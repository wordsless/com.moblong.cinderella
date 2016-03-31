package com.moblong.amuse.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

public final class MaterialDTO {

	public void save(ApplicationContext context, String aid, String typed, String pid) {
		DataSource ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstate = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstate = con.prepareStatement("INSERT INTO t_materials_base(aid, pid, typed) VALUES(?, ?, ?)");
			pstate.execute();
			con.commit();
			pstate.close();
			pstate = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstate != null)
				try {
					pstate.close();
					pstate = null;
				} catch(SQLException e) {
					e.printStackTrace();
				}
			
			if(con != null)
				try {
					con.close();
					con = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
}
