package com.ppx.cloud.auth.filter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.ppx.cloud.auth.bean.MerchantAccount;
import com.ppx.cloud.auth.cache.EhCacheConfig;
import com.ppx.cloud.auth.cache.EhCacheService;
import com.ppx.cloud.auth.common.AuthMongoSupport;
import com.ppx.cloud.auth.console.grant.GrantService;


/**
 * 权限过滤实现类
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class AuthFilterServiceImpl extends AuthMongoSupport {

	@Autowired
	private GrantService grantService;
	
	@Autowired
    private EhCacheService ehCacheServ;
	
	@Cacheable(value=EhCacheConfig.ACCOUNT_BIT_SET_CACHE, cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public BitSet getAccountResBitSet(Integer accountId) {
        BitSet grantBitset = new BitSet();      
        List<Integer> resIds = grantService.getGrantResIds(accountId);
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Integer> getUriIndexes(List<Integer> resIds) {		
		List<Integer> returnList = new ArrayList<Integer>();
		Map<Integer, Map> map = ehCacheServ.loadResouceUri();
		for (Integer resId : resIds) {
		    returnList.addAll((List<Integer>)map.get(resId).get("uriIndex"));
        }
		return returnList;
	}
	
	@SuppressWarnings("rawtypes")
    public List<Map> getOpUri(String menuUri) {
		Map<String, List<Map>> map = ehCacheServ.loadMenuResourceUri();
		return map.get(menuUri);
	}
	
	public MerchantAccount getAccountFromDb(int accountId) {
	    String sql = "select account_status, modified from merchant_account where account_id = ?";
	    MerchantAccount account = getJdbcTemplate().queryForObject(sql,
                BeanPropertyRowMapper.newInstance(MerchantAccount.class), accountId);
	    return account;
	}
	
}