package com.moblong.amuse.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Device;

public final class DeviceDTO {
	
	public void save(final ApplicationContext context, final String aid, final Device device) {
		int				  exist = 0;
		ResultSet            rs = null;
		Connection          con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT COUNT(1) FROM t_device_base WHERE did = ?");
			pstat.setString(1, device.getDeviceID());
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next())
				exist = rs.getInt(1);
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			
			if(exist == 1) {
				pstat = con.prepareStatement("UPDATE t_device_base SET aid = ? WHERE did = ?");
				pstat.setString(1, aid);
				pstat.setString(2, device.getDeviceID());
				pstat.execute();
				con.commit();
				pstat.close();
				pstat = null;
			} else if(exist == 0) {
				pstat = con.prepareStatement("INSERT INTO t_device_base VALUES(?, ?, ?, ?, ?, ?, ?)");
				pstat.setString(1, aid);
				pstat.setString(2, device.getDeviceID());
				pstat.setString(3, device.getManufacture());
				pstat.setString(4, device.getModel());
				pstat.setString(5, device.getPhone());
				pstat.setString(6, device.getPlatform());
				pstat.setString(7, device.getRelease());
				pstat.execute();
				con.commit();
			} else {
				throw new Exception("more than one result.");
			}
			
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
	
	public void setPhone(final ApplicationContext context, final String did, final String phone) {
		Connection          con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("UPDATE t_device_base SET phone = ? WHERE did = ?");
			pstat.setString(1, phone);
			pstat.setString(2, did);
			pstat.execute();
			con.commit();
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch(Exception e) {
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
	
	public final Device reload(final ApplicationContext context, final String aid) {
		Device device = null;
		Connection          con = null;
		ResultSet            rs = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("SELECT aid, did, manufacture, model, phone, platform, release FROM t_device_base WHERE aid = ?");
			pstat.setString(1, aid);
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				device = new Device();
				device.setDeviceID(rs.getString("did"));
				device.setManufacture(rs.getString("manufacture"));
				device.setModel(rs.getString("model"));
				device.setPhone(rs.getString("phone"));
				device.setPlatform(rs.getString("platform"));
				device.setRelease(rs.getString("release"));
			}
			con.commit();
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return device;
	}
}
