package com.ppx.cloud.auth.filter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.ppx.cloud.auth.cache.EhCacheService;
import com.ppx.cloud.auth.console.grant.GrantService;
import com.ppx.cloud.auth.pojo.AuthAccount;
import com.ppx.cloud.common.jdbc.MyDaoSupport;


/**
 * 权限过滤实现类
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class AuthFilterServiceImpl extends MyDaoSupport {

	@Autowired
	private GrantService grantService;
	
	@Autowired
    private EhCacheService ehCacheServ;
	
	// @Cacheable(value=EhCacheConfig.ACCOUNT_BIT_SET_CACHE, cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public BitSet getAccountResBitSet(Integer accountId) {
        BitSet grantBitset = new BitSet();      
        Set<Integer> resIds = grantService.getGrantResIds(accountId);
        List<Integer> uriIndexes = getUriIndexes(resIds);
        for (Integer index : uriIndexes) {
            grantBitset.set(index);
        }
        return grantBitset;
    }
	
	public Integer getIndexFromUri(String uri) {
		Map<String, Integer> map = ehCacheServ.loadUriIndex();
		return map.get(uri);
	}
	
    private List<Integer> getUriIndexes(Set<Integer> resIds) {		
		List<Integer> returnList = new ArrayList<Integer>();
		Map<Integer, List<Integer>> map = ehCacheServ.loadResouceUri();
		for (Integer resId : resIds) {
			if (map.get(resId) != null) {
				returnList.addAll(map.get(resId));
			}
        }
		return returnList;
	}
	
	@SuppressWarnings("rawtypes")
    public List<Map> getOpUri(String menuUri) {
		Map<String, List<Map>> map = ehCacheServ.loadMenuResourceUri();
		return map.get(menuUri);
	}
	
	public AuthAccount getAccountFromDb(int accountId) {
	    String sql = "select account_status, modified from auth_account where account_id = ?";
	    AuthAccount account = getJdbcTemplate().queryForObject(sql,
                BeanPropertyRowMapper.newInstance(AuthAccount.class), accountId);
	    return account;
	}
	
}