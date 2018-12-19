package com.ppx.cloud.auth.login;

import java.util.Date;
import java.util.Objects;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.ppx.cloud.auth.common.AuthUtils;
import com.ppx.cloud.auth.pojo.AuthAccount;
import com.ppx.cloud.common.jdbc.MyDaoSupport;

/**
 * 登录
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class LoginServiceImpl extends MyDaoSupport {
	
	public AuthAccount getLoginAccount(String a, String p) {
	    
		String adminPassword = System.getProperty("admin.password");
		// 超级管理员
		if (AuthUtils.ADMIN_ACCOUNT.equals(a) && Objects.equals(adminPassword, p)) {
			AuthAccount account = new AuthAccount();
		    account.setAccountId(-1);
		    account.setLoginAccount(a);
		    account.setUserId(-1);
		    account.setUserName("超级管理员");
		    account.setModified(new Date());
		    account.setAccountStatus(AuthUtils.ACCOUNT_STATUS_EFFECTIVE);
		    account.setUserAccountStatus(AuthUtils.ACCOUNT_STATUS_EFFECTIVE);
			return account;
		}
		
		String countSql = "select count(*) from auth_account where login_account = ? and login_password = ?";		
		int c = getJdbcTemplate().queryForObject(countSql, Integer.class, a, AuthUtils.getMD5Password(p));
		if (c == 0) {
		    return null;
		}
		
		String sql = "select a.account_id, a.login_account, a.user_id, m.user_name, a.modified, a.account_status, u.account_status user_account_status "
		        + " from auth_account a "
		        + " left join auth_user u on a.user_id = u.user_id "
				+ " left join auth_account a on a.account_id = a.user_id"
		        + " where a.login_account = ? and a.login_password = ?";
		AuthAccount account = getJdbcTemplate().queryForObject(sql, BeanPropertyRowMapper.newInstance(AuthAccount.class)
		        , a, AuthUtils.getMD5Password(p));
		return account;
	}
	
	public int updateLastLogin(int accountId)  {
	    String sql = "update auth_account set last_login = now() where account_id = ?";
	    return getJdbcTemplate().update(sql, accountId);
	}
	
}