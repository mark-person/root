package com.ppx.cloud.auth.console.mer;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppx.cloud.common.jdbc.MyCriteria;
import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.auth.bean.Merchant;
import com.ppx.cloud.auth.bean.MerchantAccount;
import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.AuthUtils;

/**
 * 商户
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class MerchantServiceImpl extends MyDaoSupport {

	public int lockMerchant() {
		int merchantId = AuthContext.getLoginAccount().getMerId();
		String sql = "select 1 from merchant where mer_id = ? for update";
		getJdbcTemplate().queryForMap(sql, merchantId);
		return merchantId;
	}
	
	public List<Merchant> listMerchant(Page page, Merchant mer) {
		MyCriteria c = createCriteria("and").addAnd("m.mer_name like ?", "%", mer.getMerName(), "%");
		
		String sql = "from merchant m left join merchant_account a on m.mer_id = a.account_id where m.mer_status >= ?";
		StringBuilder cSql = new StringBuilder("select count(*) ").append(sql).append(c);
		StringBuilder qSql = new StringBuilder("select m.*, a.login_account, a.account_status ").append(sql).append(c).append(" order by m.mer_id");
		c.addPrePara(AuthUtils.ACCOUNT_STATUS_EFFECTIVE);
		List<Merchant> list = queryPage(Merchant.class, page, cSql, qSql, c.getParaList());

		return list;
	}

	@Transactional
	public int insertMerchant(Merchant bean) {
	    final int ACCOUNT_EXIST = -1;
	    final int MER_NAME_EXIST = -2;
	    
		MerchantAccount account = new MerchantAccount();
		account.setMerId(-1);
		account.setLoginAccount(bean.getLoginAccount());
		String pw = AuthUtils.getMD5Password(bean.getLoginPassword());
		account.setLoginPassword(pw);
		int accountR = insertEntity(account, "login_account");
		if (accountR == 0) {
			return ACCOUNT_EXIST;
		}
		
		int merId = getLastInsertId();
		bean.setMerId(merId);
		int merchantR = insertEntity(bean, "mer_name");
		if (merchantR == 0) {
		    String delSql = "delete from merchant_account where account_id = ?";
		    getJdbcTemplate().update(delSql, merId);
            return MER_NAME_EXIST;
		}
		
		return getJdbcTemplate().update("update merchant_account set mer_id = ? where account_id = ?", merId, merId);
	}

	public Merchant getMerchant(Integer id) {
		Merchant bean = getJdbcTemplate().queryForObject("select * from merchant where mer_id = ?",
				BeanPropertyRowMapper.newInstance(Merchant.class), id);
		return bean;
	}
	
	public MerchantAccount getMerchantAccount(Integer id) {
		MerchantAccount bean = getJdbcTemplate().queryForObject("select * from merchant_account where account_id = ?",
				BeanPropertyRowMapper.newInstance(MerchantAccount.class), id);
		return bean;
	}

	public int updateMerchant(Merchant bean) {
		return updateEntity(bean, "mer_name");
	}
	
	public int updateMerchantAccount(MerchantAccount bean) {
	    bean.setModified(new Date());
		return updateEntity(bean, "login_account");
	}
	
	public int updateMerchantPassword(Integer merId, String merchantPassword) {
		String sql = "update merchant_account set login_password = ?, modified = now() where account_id = ?";
		return getJdbcTemplate().update(sql, AuthUtils.getMD5Password(merchantPassword), merId);
	}

	@Transactional
	public int deleteMerchant(Integer merId) {
    	getJdbcTemplate().update("update merchant set mer_status = ? where mer_id = ?", 0, merId);
    	getJdbcTemplate().update("update merchant_account set account_status = ? where account_id = ?", 0, merId);
		return 1;
	}
	
	public int disable(Integer merId) {
	    getJdbcTemplate().update("update merchant set mer_status = ? where mer_id = ?"
	            , AuthUtils.ACCOUNT_STATUS_INEFFECTIVE, merId);
	    getJdbcTemplate().update("update merchant_account set account_status = ? where account_id = ?"
	            , AuthUtils.ACCOUNT_STATUS_INEFFECTIVE, merId);
	    return 1;
    }
	
	public int enable(Integer merId) {
        getJdbcTemplate().update("update merchant set mer_status = ? where mer_id = ?"
                , AuthUtils.ACCOUNT_STATUS_EFFECTIVE, merId);
        getJdbcTemplate().update("update merchant_account set account_status = ? where account_id = ?"
                , AuthUtils.ACCOUNT_STATUS_EFFECTIVE, merId);
        return 1;
    }
	
}