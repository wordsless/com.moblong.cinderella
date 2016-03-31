package com.moblong.amuse.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Constants;
import com.moblong.flipped.model.Indicator;
import com.moblong.flipped.model.VerifiableItem;

public final class DetailsDTO {

	public void save(final ApplicationContext context, final String uid, final List<VerifiableItem> details) {
		boolean			  exist = false;
		ResultSet			 rs = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("SELECT COUNT(1) FROM t_details_base WHERE uid = ?");
			pstat.setString(1, uid);
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				exist = rs.getInt(1) > 0;
			}
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			
			if(exist) {
				pstat = con.prepareStatement("DELETE FROM t_details_base WHERE uid = ?");
				pstat.setString(1, uid);
				pstat.execute();
				pstat.close();
				pstat = null;
			}
			
			pstat = con.prepareStatement("INSERT INTO t_details_base(uid, iid, title, content, condition) VALUES(?, ?, ?, ?, ?)");
			for(VerifiableItem item : details) {
				Indicator<?> indicator = item.getIndicator();
				pstat.setString(1, uid);
				pstat.setInt(2, indicator.getId());
				pstat.setString(3, indicator.getTitle());
				if(indicator.getId() == Constants.INDEX_CITY || indicator.getId() == Constants.INDEX_FULLNAME) {
					pstat.setString(4, (String) indicator.getContent());
				} else if(indicator.getId() == Constants.INDEX_SEX) {
					pstat.setString(4, Boolean.toString((Boolean) indicator.getContent()));
				} else {
					pstat.setString(4, Integer.toString((Integer) indicator.getContent()));
				}
				pstat.setInt(5, indicator.isVisible() ? 1 : 0);
				pstat.setInt(6, item.isVerified() ? 1 : 0);
				pstat.addBatch();
			}
			pstat.executeBatch();
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
	
	public List<VerifiableItem> reload(final ApplicationContext context, final String uid) {
		List<VerifiableItem> user = null;
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			user = new ArrayList<VerifiableItem>(10);
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT uid, iid, title, content, condition FROM t_details_base WHERE uid = ?");
			pstat.setString(1, uid);
			pstat.execute();
			rs = pstat.getResultSet();
			while(rs.next()) {
				int iid = rs.getInt("iid");
				if(iid == Constants.INDEX_CITY || iid == Constants.INDEX_FULLNAME)
					user.add(new VerifiableItem(new Indicator<String>(iid, rs.getString("title"), rs.getString("content")), rs.getInt("condition") > 0));
				else if(iid == Constants.INDEX_SEX)
					user.add(new VerifiableItem(new Indicator<Boolean>(iid, rs.getString("title"), Boolean.parseBoolean(rs.getString("content"))), rs.getInt("condition") > 0));
				else
					user.add(new VerifiableItem(new Indicator<Integer>(iid, rs.getString("title"), Integer.parseInt(rs.getString("content"))), rs.getInt("condition") > 0));
			}
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
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
		return user;
	}
	
	public void delete(final ApplicationContext context, final String uid) {
		Connection con = null;
		PreparedStatement pstat = null;
		try {
			DataSource ds = context.getBean("ds", DataSource.class);
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("DELETE FROM t_user_indicator WHERE uid = ?");
			pstat.setString(1, uid);
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
	
	public String lookfor(final ApplicationContext context, final String aid) {
		String				uid = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		ResultSet			 rs = null;
		DataSource 			 ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT uid FROM t_account_base WHERE aid = ?");
			pstat.setString(1, aid);
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				uid = rs.getString(1);
			}
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
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
		return uid;
	}
}
