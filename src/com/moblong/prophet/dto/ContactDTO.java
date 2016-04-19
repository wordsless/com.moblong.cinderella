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

public final class ContactDTO {

	private static final int NUMBER_OF_PER_PAGE = 20;

	public void save(final ApplicationContext context, final String uid, final String pwd, final Contact account) {
		Connection con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("INSERT INTO t_account_base(uid, aid, alias, pwd, signature, telephone, latitude, longitude, registered, lastest) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstat.setString(1, uid.trim());
			
			String aid = account.getId();
			if(aid != null)
				pstat.setString(2, aid.trim());
			else
				pstat.setString(2, null);
			
			String alias = account.getAlias();
			if(alias != null) {
				pstat.setString(3, alias.trim());	
			} else {
				pstat.setString(3, null);
			}
			
			if(pwd != null) {
				pstat.setString(4, pwd.trim());
			} else {
				pstat.setString(4, null);
			}
			
			String signature = account.getSignature();
			if(signature != null) {
				pstat.setString(5, account.getSignature().trim());	
			} else {
				pstat.setString(5, null);
			}
			
			String telephone = account.getTelephone();
			if(telephone != null) {
				pstat.setString(6, account.getTelephone().trim());	
			} else {
				pstat.setString(6, null);
			}
			
			pstat.setDouble(7, account.getLatitude());
			pstat.setDouble(8, account.getLongitude());
			pstat.setDate(9,   new java.sql.Date(account.getRegistered().getTime()));
			pstat.setDate(10,  new java.sql.Date(account.getLatest().getTime()));
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
	
	public Contact reload(final ApplicationContext context, final String aid) {
		Contact		    account = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		ResultSet			 rs = null;
		DataSource 			 ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT * FROM t_account_base WHERE aid = ?");
			pstat.setString(1, aid);
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				account = new Contact();
				account.setId(rs.getString("aid").trim());
				account.setAlias(rs.getString("alias").trim());
				account.setTelephone(rs.getString("telphone").trim());
				account.setRegistered(new java.util.Date(rs.getDate("registered").getTime()));
				account.setLatest(new java.util.Date(rs.getDate("lastest").getTime()));
				account.setSignature(rs.getString("signature").trim());
				account.setAvatar(rs.getString("ppid").trim());
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
		return account;
	}
	
	public Contact signIn(final ApplicationContext context, final String telephone, final String password) {
		Contact		    account = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		ResultSet			 rs = null;
		DataSource 			 ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT * FROM t_account_base WHERE telephone = ? AND pwd = ?");
			pstat.setString(1, telephone);
			pstat.setString(2, password);
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				account = new Contact();
				account.setId(rs.getString("aid").trim());
				account.setAlias(rs.getString("alias").trim());
				account.setTelephone(rs.getString("telephone").trim());
				account.setRegistered(new java.util.Date(rs.getDate("registered").getTime()));
				account.setLatest(new java.util.Date(rs.getDate("lastest").getTime()));
				account.setSignature(rs.getString("signature").trim());
				account.setAvatar(rs.getString("ppid").trim());
				account.setType(rs.getString("type"));
				account.setUid(rs.getString("uid"));
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
		return account;
	}
	
	public String lookforUserId(final ApplicationContext context, final String aid) {
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
	
	public List<Contact> candidate(final ApplicationContext context, final String aid, final int page) {
		List<Contact> candidates = new ArrayList<Contact>();
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		int index = page * NUMBER_OF_PER_PAGE;
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT * FROM t_account_base WHERE aid <> ? AND LENGTH(alias) > 0 AND page >= ? AND page < ?");//匿名注册时也会有account id，但是alias为NULL
			pstat.setString(1, aid);
			pstat.setInt(2, index);
			pstat.setInt(3, index + NUMBER_OF_PER_PAGE);
			pstat.execute();
			rs = pstat.getResultSet();
			while(rs.next()) {
				Contact candidate = new Contact();
				candidate.setId(rs.getString("aid").trim());
				candidate.setAlias(rs.getString("alias").trim());
				candidate.setRegistered(new java.util.Date(rs.getDate("registered").getTime()));
				candidate.setLatest(new java.util.Date(rs.getDate("lastest").getTime()));
				candidate.setSignature(rs.getString("signature").trim());
				candidate.setAvatar(rs.getString("ppid").trim());
				candidates.add(candidate);
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
		return candidates;
	}
//4008109956
	public void update(ApplicationContext context, Contact account, final String password) {
		Connection			con = null;
		PreparedStatement pstat = null;
		DataSource			 ds = context.getBean("ds", DataSource.class);
		try {
			con   = ds.getConnection();
			if(account.getAvatar() == null) {
				pstat = con.prepareStatement("UPDATE t_account_base SET alias = ?, signature = ?, telephone = ?, pwd = ? WHERE aid = ?");
				
				String alias = account.getAlias();
				if(alias != null) {
					pstat.setString(1, account.getAlias().trim());	
				} else {
					pstat.setString(1, null);
				}
				
				String signature = account.getSignature();
				if(signature != null) {
					pstat.setString(2, account.getSignature().trim());	
				} else {
					pstat.setString(2, null);
				}
				
				String telephone = account.getTelephone();
				if(telephone != null) {
					pstat.setString(3, account.getTelephone().trim());
				} else {
					pstat.setString(3, null);
				}
				
				if(password != null) {
					pstat.setString(4, password.trim());	
				} else {
					pstat.setString(4, null);
				}
				
				String aid = account.getId();
				if(aid != null) {
					pstat.setString(5, account.getId().trim());	
				} else {
					pstat.setString(5, null);
				}
				
			} else {
				pstat = con.prepareStatement("UPDATE t_account_base SET alias = ?, signature = ?, telephone = ?, pwd = ?, ppid = ? WHERE aid = ?");
				
				String alias = account.getAlias();
				if(alias != null) {
					pstat.setString(1, account.getAlias().trim());	
				} else {
					pstat.setString(1, null);
				}
				
				String signature = account.getSignature();
				if(signature != null) {
					pstat.setString(2, account.getSignature().trim());	
				} else {
					pstat.setString(2, null);
				}
				
				String telephone = account.getTelephone();
				if(telephone != null) {
					pstat.setString(3, account.getTelephone().trim());
				} else {
					pstat.setString(3, null);
				}
				
				if(password != null) {
					pstat.setString(4, password.trim());	
				} else {
					pstat.setString(4, null);
				}
				
				String ppid = account.getAvatar();
				if(ppid != null) {
					pstat.setString(5, ppid.trim());
				} else {
					pstat.setString(5, null);
				}
				
				String aid = account.getId();
				if(aid != null) {
					pstat.setString(6, account.getId().trim());	
				} else {
					pstat.setString(6, null);
				}
			}
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

	public void delete(ApplicationContext context, String aid) {
		Connection			con = null;
		PreparedStatement pstat = null;
		DataSource			 ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			PreparedStatement pstate = con.prepareStatement("DELETE FROM t_account_base WHERE aid = ?");
			pstate.setString(1, aid);
			pstate.execute();
			pstate.close();
			pstate = null;
			con.close();
			con = null;
		} catch(Exception ex) {
			ex.printStackTrace();
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
