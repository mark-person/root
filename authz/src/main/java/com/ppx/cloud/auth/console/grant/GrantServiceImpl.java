package com.ppx.cloud.auth.console.grant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			.addAnd("u.user_id = ?", user.getUserId())
			.addAnd("u.user_name like ?", "%", user.getUserName(), "%");
		
		StringBuilder cSql = new StringBuilder("select count(*) from auth_user u where u.user_status = ?").append(c);
		StringBuilder qSql = new StringBuilder("select u.user_id, u.user_name, a.login_account " +
			" from auth_user u left join auth_account a on u.user_id = a.account_id where u.user_status = ?")
			.append(c).append(" order by u.user_id");
		c.addPrePara(1);
		return queryPage(AuthUser.class, page, cSql, qSql, c.getParaList());
	}
	
	@Override
	public Set<Integer> getGrantResIds(Integer accountId) {
	    int c = getJdbcTemplate().queryForObject("select count(*) from auth_grant where account_id = ?", Integer.class, accountId);
	    if (c == 0) {
	        return new HashSet<Integer>(0);
	    }
	    
	    AuthGrant authGrant = getJdbcTemplate().queryForObject("select account_id, group_concat(res_id) res_ids from auth_grant where account_id = ?",
                BeanPropertyRowMapper.newInstance(AuthGrant.class), accountId);
	    
	    return authGrant.getResIds();
	}
	
	@Transactional
	public int saveGrantResIds(Integer accountId, String resIds) {
	    // ehCacheServ.increaseGrantDbVersion();
		String delSql = "delete from auth_grant where account_id = ?";
		getJdbcTemplate().update(delSql, accountId);
		
		if (Strings.isNotEmpty(resIds)) {
			String insertSql = "insert into auth_grant(account_id, res_id) values(?, ?)";
			var paraList = new ArrayList<Object[]>();
			String[] resId = resIds.split(",");
			for (String id : resId) {
				Object[] obj = {accountId, Integer.parseInt(id)};
				paraList.add(obj);
			}
			getJdbcTemplate().batchUpdate(insertSql, paraList);
		}
		
		return 1;
	}
	
	

	
	
}