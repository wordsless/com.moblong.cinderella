package com.moblong.prophet.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Contact;

public final class GeographyAssister {
	
	public void update(final ApplicationContext context, final String aid, final double latitude, final double longitude) {
		DataSource  ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pstate = null;
		String sql = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			sql = String.format("UPDATE t_location_realtime SET location = 'POINT(%s %s)':: geometry WHERE aid = '%s'", new Object[]{latitude, longitude, aid});
			pstate = con.prepareStatement(sql);
			pstate.execute();
			pstate.close();
			pstate = null;
			
			sql = String.format("INSERT INTO t_location_history(aid, location) VALUES('%s', 'POINT(%s %s)::geometry')", new Object[]{aid, latitude, longitude});
			pstate = con.prepareStatement(sql);
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
	
	public void register(final ApplicationContext context, final String aid, final double latitude, final double longitude) {
		DataSource  ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pstate = null;
		String sql = null;
		try {
			boolean exist = false;
			con = ds.getConnection();
			con.setAutoCommit(false);
			sql = String.format("SELECT COUNT(1) FROM t_location_realtime WHERE aid = '%s'", aid);
			pstate = con.prepareStatement(sql);
			pstate.execute();
			rs = pstate.getResultSet();
			if(rs.next()) {
				exist = rs.getInt(1) > 0;
			}
			pstate.close();
			pstate = null;
			
			if(exist)
				sql = String.format("UPDATE t_location_realtime SET location = 'POINT(%s %s)':: geometry WHERE aid = '%s'", new Object[]{latitude, longitude, aid});
			else
				sql = String.format("INSERT INTO t_location_realtime(aid, location) VALUES('%s', 'POINT(%s %s)::geometry')", new Object[]{aid, latitude, longitude});
			System.out.println(sql);
			pstate = con.prepareStatement(sql);
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
	
	public List<Contact> nearby(final ApplicationContext context, final String aid, final double latitude, final double longitude, final int radius) {
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
				+ "WHERE base.aid = nearby.aid", new Object[]{new Double(longitude), new Double(latitude), aid});
		List<Contact> nearby = new ArrayList<Contact>();
		DataSource ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			//System.out.println(sql);
			pstate = con.prepareStatement(sql);
			pstate.execute();
			rs = pstate.getResultSet();
			while(rs.next()) {
				Contact account = new Contact();
				account = new Contact();
				String id = rs.getString(1);
				if(id != null)
					account.setId(id.trim());
				
				String alias = rs.getString(2);
				if(alias != null)
					account.setAlias(alias.trim());
				
				String telephone = rs.getString(3);
				if(telephone != null)
					account.setTelephone(telephone.trim());
				
				java.sql.Date reg = rs.getDate(4);
				if(reg != null)
					account.setRegistered(new java.util.Date(rs.getDate(4).getTime()));
				
				java.sql.Date last = rs.getDate(4);
				if(last != null)
					account.setLatest(new java.util.Date(last.getTime()));
				
				String signature = rs.getString(6);
				if(signature != null)
					account.setSignature(signature.trim());
				
				String avatar = rs.getString(7);
				if(avatar != null)
					account.setAvatar(avatar.trim());
				
				String type = rs.getString(8);
				if(type != null)
					account.setType(type.trim());
				
				String uid = rs.getString(9);
				if(uid != null)
					account.setUid(uid.trim());
				
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
