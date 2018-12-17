package com.ppx.cloud.auth.console.grant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppx.cloud.auth.bean.Merchant;
import com.ppx.cloud.auth.cache.EhCacheService;
import com.ppx.cloud.auth.pojo.AuthUser;
import com.ppx.cloud.common.jdbc.MyCriteria;
import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;

/**
 * 分配权限
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class GrantServiceImpl extends MyDaoSupport implements GrantService {
	
	@Autowired
	private EhCacheService ehCacheServ;
	
	public List<AuthUser> listUser(Page page, AuthUser user) {
		MyCriteria c = createCriteria("and")
			.addAnd("m.mer_id = ?", user.getUserId())
			.addAnd("m.mer_name like ?", "%", user.getUserName(), "%");
		
		StringBuilder cSql = new StringBuilder("select count(*) from auth_user u where u.user_status = ?").append(c);
		StringBuilder qSql = new StringBuilder("select u.user_id, u.user_name, a.login_account " +
			" from merchant m left join auth_account a on m.user_id = a.account_id where u.user_status = ?")
			.append(c).append(" order by u.user_id");
		c.addPrePara(1);
		return queryPage(AuthUser.class, page, cSql, qSql, c.getParaList());
	}
	
	@Override
	public List<Integer> getGrantResIds(Integer accountId) {
	    int c = getJdbcTemplate().queryForObject("SELECT count(*) from auth_grant where account_id = ?", Integer.class, accountId);
	    if (c == 0) {
	        return new ArrayList<Integer>(0);
	    } 
	     
	    AuthGrant authGrant = getJdbcTemplate().queryForObject("select * from auth_grant where account_id = ?",
                BeanPropertyRowMapper.newInstance(AuthGrant.class), accountId);
	    return authGrant.getResIds();
	}
	
	@Transactional
	public int saveGrantResIds(Integer accountId, String resIds) {
	    ehCacheServ.increaseGrantDbVersion();

		String sql = "insert into auth_grant(account_id, res_ids) values(?, ?) on duplicate key update res_ids = ?";
		resIds = "[" + resIds + "]";
		getJdbcTemplate().update(sql, accountId, resIds, resIds);
		
		return 1;
	}
	
	

	
	
}