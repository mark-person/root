package com.ppx.cloud.auth.console.child;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppx.cloud.auth.bean.MerchantAccount;
import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.AuthUtils;
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

	public List<MerchantAccount> listChild(Page page, MerchantAccount child) {
		int merId = AuthContext.getLoginAccount().getMerId();
		
		MyCriteria c = createCriteria("and")
				.addAnd("account_id = ?", child.getAccountId())
				.addAnd("login_account like ?", "%", child.getLoginAccount(), "%");
		
		String sql = "from merchant_account where mer_id = ? and account_status >= ? and mer_id != account_id";
		StringBuilder cSql = new StringBuilder("select count(*) ").append(sql).append(c);
		StringBuilder qSql = new StringBuilder("select * ").append(sql).append(c).append(" order by account_id desc");
		c.addPrePara(merId);
		c.addPrePara(AuthUtils.ACCOUNT_STATUS_EFFECTIVE);
		List<MerchantAccount> list = queryPage(MerchantAccount.class, page, cSql, qSql, c.getParaList());

		return list;
	}

	@Transactional
	public int insertChild(MerchantAccount bean) {
		int mertId = AuthContext.getLoginAccount().getMerId();
		
		MerchantAccount account = new MerchantAccount();
		account.setMerId(mertId);
		account.setLoginAccount(bean.getLoginAccount());
		account.setLoginPassword(AuthUtils.getMD5Password(bean.getLoginPassword()));

		return insertEntity(account, "login_account");
	}

	public MerchantAccount getChild(Integer id) {
		MerchantAccount bean = getJdbcTemplate().queryForObject("select * from merchant_account where account_id = ?",
				BeanPropertyRowMapper.newInstance(MerchantAccount.class), id);
		return bean;
	}
	
	public int updateAccount(MerchantAccount bean) {
		int merId = AuthContext.getLoginAccount().getMerId();
		bean.setModified(new Date());
		// 帐号唯一，只能更新自己子帐号的
		return updateEntity(bean, new LimitRecord("mer_id", merId), "login_account");
	}
	
	public int updatePassword(Integer id, String loginPassword) {
		int merId = AuthContext.getLoginAccount().getMerId();
		// 只能更新自己子帐号的
		String sql = "update merchant_account set login_password = ?, modified = now() where account_id = ? and mer_id = ?";
		return getJdbcTemplate().update(sql, AuthUtils.getMD5Password(loginPassword), id, merId);
	}

	public int deleteChild(Integer id) {
		int mertId = AuthContext.getLoginAccount().getMerId();
		// 只能删除自己子帐号
		return getJdbcTemplate().update("update merchant_account set account_status = ? where account_id = ? and mer_id = ?", 0, id , mertId);
	}
	
	public int disable(Integer id) {
        int mertId = AuthContext.getLoginAccount().getMerId();
        // 只能disable自己子帐号
        return getJdbcTemplate().update("update merchant_account set account_status = ? where account_id = ? and mer_id = ?"
                , AuthUtils.ACCOUNT_STATUS_INEFFECTIVE, id , mertId);
    }
	
	public int enable(Integer id) {
        int mertId = AuthContext.getLoginAccount().getMerId();
        // 只能enable自己子帐号
        return getJdbcTemplate().update("update merchant_account set account_status = ? where account_id = ? and mer_id = ?"
                , AuthUtils.ACCOUNT_STATUS_EFFECTIVE, id , mertId);
    }

}





