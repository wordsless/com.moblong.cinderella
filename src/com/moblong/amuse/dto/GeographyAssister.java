package com.moblong.amuse.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Account;

public final class GeographyAssister {
	
	public void update(final ApplicationContext context, final String aid, final double latitude, final double longitude) {
		DataSource  ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstate = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			StringBuilder sb = new StringBuilder("UPDATE t_location_realtime SET location = 'POINT(");
			sb.append(latitude);
			sb.append(' ');
			sb.append(longitude);
			sb.append(")':: geometry WHERE aid = \'");
			sb.append(aid);
			sb.append('\'');
			pstate = con.prepareStatement(sb.toString());
			pstate.execute();
			pstate.close();
			pstate = null;
			
			sb = new StringBuilder("INSERT INTO t_location_history(aid, location) VALUES(");
			sb.append('\'');
			sb.append(aid);
			sb.append('\'');
			sb.append(',');
			sb.append("'POINT(");
			sb.append(latitude);
			sb.append(' ');
			sb.append(longitude);
			sb.append(")':: geometry)");
			pstate = con.prepareStatement(sb.toString());
			pstate.execute();
			pstate.close();
			pstate = null;
			
			con.commit();
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstate != null) {
				try {
					pstate.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstate = null;
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
	
	public List<Account> nearby(final ApplicationContext context, final String aid, final double latitude, final double longitude, final int radius) {
		String sql = String.format("SELECT "
				+ "base.aid, "
				+ "base.alias, "
				+ "base.telephone, "
				+ "base.registered, "
				+ "base.lastest, "
				+ "base.signature, "
				+ "base.ppid, "
				+ "base.type, "
				+ "base.uid, "
				+ "nearby.distance "
				+ "FROM "
				+ "t_account_base AS base, "
				+ "(SELECT aid, ST_Distance('POINT(%s %s)', location) AS distance FROM t_location_realtime WHERE aid <> '%s' "
				+ "ORDER BY distance LIMIT 1000) AS nearby "
				+ "WHERE base.aid = nearby.aid AND distance < %s", new Object[]{new Double(latitude), new Double(longitude), aid, new Integer(radius)});
		List<Account> nearby = new ArrayList<Account>();
		DataSource ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			System.out.println(sql);
			pstate = con.prepareStatement(sql);
			pstate.execute();
			rs = pstate.getResultSet();
			while(rs.next()) {
				Account account = new Account();
				account = new Account();
				account.setId(rs.getString(1).trim());
				account.setAlias(rs.getString(2).trim());
				account.setTelephone(rs.getString(3).trim());
				account.setRegistered(new java.util.Date(rs.getDate(4).getTime()));
				account.setLast(new java.util.Date(rs.getDate(5).getTime()));
				account.setSignature(rs.getString(6).trim());
				account.setAvatar(rs.getString(7).trim());
				account.setType(rs.getString(8));
				account.setUid(rs.getString(9));
				account.setDistance(rs.getInt(10));
				nearby.add(account);
			}
			rs.close();
			rs = null;
			pstate.close();
			pstate = null;
			con.close();
			con = null;
		} catch(SQLException ex) {
			ex.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
			if(pstate != null) {
				try {
					pstate.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstate = null;
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
		
		return nearby;
	}
	
}
