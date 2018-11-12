package com.ppx.cloud.auth.login;

import java.util.Date;
import java.util.Objects;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.auth.bean.MerchantAccount;
import com.ppx.cloud.auth.common.AuthUtils;

/**
 * 登录
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class LoginServiceImpl extends MyDaoSupport {
	
	public MerchantAccount getLoginAccount(String a, String p) {
	    
		String adminPassword = System.getProperty("admin.password");
		// 超级管理员
		if (AuthUtils.ADMIN_ACCOUNT.equals(a) && Objects.equals(adminPassword, p)) {
		    MerchantAccount account = new MerchantAccount();
		    account.setAccountId(-1);
		    account.setLoginAccount(a);
		    account.setMerId(-1);
		    account.setMerName("超级管理员");
		    account.setModified(new Date());
		    account.setAccountStatus(AuthUtils.ACCOUNT_STATUS_EFFECTIVE);
		    account.setMerAccountStatus(AuthUtils.ACCOUNT_STATUS_EFFECTIVE);
			return account;
		}
		
		String countSql = "select count(*) from merchant_account where login_account = ? and login_password = ?";		
		int c = getJdbcTemplate().queryForObject(countSql, Integer.class, a, AuthUtils.getMD5Password(p));
		if (c == 0) {
		    return null;
		}
		
		String sql = "select a.account_id, a.login_account, a.mer_id, m.mer_name, a.modified, a.account_status, mer_a.account_status mer_account_status "
		        + " from merchant_account a "
		        + " left join merchant m on a.mer_id = m.mer_id "
				+ " left join merchant_account mer_a on mer_a.account_id = a.mer_id"
		        + " where a.login_account = ? and a.login_password = ?";
		MerchantAccount account = getJdbcTemplate().queryForObject(sql, BeanPropertyRowMapper.newInstance(MerchantAccount.class)
		        , a, AuthUtils.getMD5Password(p));
		return account;
	}
	
	public int updateLastLogin(int accountId)  {
	    String sql = "update merchant_account set last_login = now() where account_id = ?";
	    return getJdbcTemplate().update(sql, accountId);
	}
	
}