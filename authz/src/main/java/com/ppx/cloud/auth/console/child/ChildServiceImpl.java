package com.ppx.cloud.auth.console.child;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.AuthUtils;
import com.ppx.cloud.auth.pojo.AuthAccount;
import com.ppx.cloud.common.jdbc.MyCriteria;
import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.LimitRecord;
import com.ppx.cloud.common.page.Page;

/**
 * 子帐号管理
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class ChildServiceImpl extends MyDaoSupport {

	public List<AuthAccount> listChild(Page page, AuthAccount child) {
		int userId = AuthContext.getLoginAccount().getUserId();
		
		MyCriteria c = createCriteria("and")
				.addAnd("account_id = ?", child.getAccountId())
				.addAnd("login_account like ?", "%", child.getLoginAccount(), "%");
		
		String sql = "from auth_account where user_id = ? and account_status >= ? and user_id != account_id";
		StringBuilder cSql = new StringBuilder("select count(*) ").append(sql).append(c);
		StringBuilder qSql = new StringBuilder("select * ").append(sql).append(c).append(" order by account_id desc");
		c.addPrePara(userId);
		c.addPrePara(AuthUtils.ACCOUNT_STATUS_EFFECTIVE);
		List<AuthAccount> list = queryPage(AuthAccount.class, page, cSql, qSql, c.getParaList());

		return list;
	}

	@Transactional
	public int insertChild(AuthAccount bean) {
		int userId = AuthContext.getLoginAccount().getUserId();
		
		AuthAccount account = new AuthAccount();
		account.setUserId(userId);
		account.setLoginAccount(bean.getLoginAccount());
		account.setLoginPassword(AuthUtils.getMD5Password(bean.getLoginPassword()));

		return insertEntity(account, "login_account");
	}

	public AuthAccount getChild(Integer id) {
		AuthAccount bean = getJdbcTemplate().queryForObject("select * from auth_account where account_id = ?",
				BeanPropertyRowMapper.newInstance(AuthAccount.class), id);
		return bean;
	}
	
	public int updateAccount(AuthAccount bean) {
		int userId = AuthContext.getLoginAccount().getUserId();
		bean.setModified(new Date());
		// 帐号唯一，只能更新自己子帐号的
		return updateEntity(bean, new LimitRecord("user_id", userId), "login_account");
	}
	
	public int updatePassword(Integer id, String loginPassword) {
		int merId = AuthContext.getLoginAccount().getUserId();
		// 只能更新自己子帐号的
		String sql = "update auth_account set login_password = ?, modified = now() where account_id = ? and user_id = ?";
		return getJdbcTemplate().update(sql, AuthUtils.getMD5Password(loginPassword), id, merId);
	}

	public int deleteChild(Integer id) {
		int userId = AuthContext.getLoginAccount().getUserId();
		// 只能删除自己子帐号
		return getJdbcTemplate().update("update auth_account set account_status = ? where account_id = ? and user_id = ?", 0, id , userId);
	}
	
	public int disable(Integer id) {
        int userId = AuthContext.getLoginAccount().getUserId();
        // 只能disable自己子帐号
        return getJdbcTemplate().update("update auth_account set account_status = ? where account_id = ? and user_id = ?"
                , AuthUtils.ACCOUNT_STATUS_INEFFECTIVE, id , userId);
    }
	
	public int enable(Integer id) {
        int userId = AuthContext.getLoginAccount().getUserId();
        // 只能enable自己子帐号
        return getJdbcTemplate().update("update auth_account set account_status = ? where account_id = ? and user_id = ?"
                , AuthUtils.ACCOUNT_STATUS_EFFECTIVE, id , userId);
    }

}





