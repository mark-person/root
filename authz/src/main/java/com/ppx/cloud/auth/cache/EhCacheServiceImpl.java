package com.ppx.cloud.auth.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;



/**
 * 分两部分刷新:一个是刷新所有(菜单修改后)，一个是刷新权限部分(修改权限后)
 * @author mark
 * @date 2018年6月22日
 */
@Service
public class EhCacheServiceImpl extends MyDaoSupport implements EhCacheService {
	
	@Autowired
	@Qualifier(EhCacheConfig.LOCAL_MANAGER)
	private CacheManager cacheManager;
	
	public AuthCache getAuthVersion() {
	    AuthCache authCache = getJdbcTemplate().queryForObject("select all_version, grant_version from auth_cache where cache_type = ?",
                BeanPropertyRowMapper.newInstance(AuthCache.class), AuthCache.AUTH_VERSION);
        return authCache;
	}
	
	public AuthCache initAuthVersion() {
	    int c = getJdbcTemplate().queryForObject("select count(*) from auth_cache where cache_type = ?"
	            , Integer.class, AuthCache.AUTH_VERSION);
	    if (c == 0) {
	        String insertSql = "insert auth_cache(cache_type, all_version, grant_version) values(?, ?, ?)";
	        getJdbcTemplate().update(insertSql, AuthCache.AUTH_VERSION, 1, 1);
	        return new AuthCache(1, 1);
	    }
	    else {
	        return getAuthVersion();
	    } 
    }
	
	public void increaseAllDbVersion() {
        String sql = "update auth_cache set all_version = all_version + 1 where cache_type = ?";
        getJdbcTemplate().update(sql, AuthCache.AUTH_VERSION);
	}
	
	public void increaseGrantDbVersion() {
	    String sql = "update auth_cache set grant_version = grant_version + 1 where cache_type = ?";
        getJdbcTemplate().update(sql, AuthCache.AUTH_VERSION);
	}
	
	public void clearAllLocalCache() {
        clearCache(EhCacheConfig.ACCOUNT_BIT_SET_CACHE);
        clearCache(EhCacheConfig.AUTH_FIX_CACHE);
    }
    
	public void clearAuthLocalCache() {
        clearCache(EhCacheConfig.ACCOUNT_BIT_SET_CACHE);
    }
	
	private void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.clear();
    }
	
	@Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadUriIndex'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<String, Integer> loadUriIndex() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        String sql = "select uri_seq, uri_text from auth_uri_seq";
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        for (Map<String, Object> map : list) {
        	returnMap.put((String)map.get("uri_text"), (Integer)map.get("uri_seq"));
		}
        
        return returnMap;
    }
    
    
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadMenuResourceUri'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<String, List<Map<String, Object>>> loadMenuResourceUri() {
    	
    	Map<String, List<Map<String, Object>>> returnMap = new HashMap<String, List<Map<String, Object>>>();
    	
    	String sql = "select ru.uri_seq, us.uri_text, menu.uri_text menu_uri " + 
    			"from auth_res_uri ru join auth_uri_seq us on ru.uri_seq = us.uri_seq " + 
    			"join auth_res r on r.res_id = ru.res_id join auth_res p on r.parent_id = p.res_id join auth_uri_seq menu on menu.uri_seq = p.uri_seq " + 
    			"where  p.res_type = 1";
    	
    	Set<String> menuUriSet = new HashSet<String>();
    	
    	
    	List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
    	for (Map<String, Object> map : list) {
			String menuUri = (String)map.get("menu_uri");
			menuUriSet.add(menuUri);
		}
    	
    	for (String menuUri : menuUriSet) {
    		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
    		for (Map<String, Object> map : list) {
    			String tmpMenuUri = (String)map.get("menu_uri");
    			if (tmpMenuUri.equals(menuUri)) {
    				resList.add(map);
    			}
    		}
    		returnMap.put(menuUri, resList);
		}
    	
    	return returnMap;
    }
    
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadResouceUri'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<Integer, List<Integer>> loadResouceUri() {
        Map<Integer, List<Integer>> returnMap = new HashMap<Integer, List<Integer>>();
        String sql = "select res_id, group_concat(uri_seq) uri_seqs from auth_res_uri group by res_id";
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        for (Map<String, Object> map : list) {
			Integer resId = (Integer)map.get("res_id");
			String uriSeqs = (String)map.get("uri_seqs");
			String[] uriSeq = uriSeqs.split(",");
			List<Integer> seqList = new ArrayList<Integer>();
			for (int i = 0; i < uriSeq.length; i++) {
				seqList.add(Integer.parseInt(uriSeq[i]));
			}
			returnMap.put(resId, seqList);
		}
        
        return returnMap;
    }
    
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadResource'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public List<Map<String, Object>> loadResource() {

    	var folderList = new ArrayList<Map<String, Object>>();

		String resSql = "select r.res_id id, r.parent_id pId, r.res_name t, r.res_type type, u.uri_text uri" + 
				" from auth_res r left join auth_uri_seq u on r.uri_seq = u.uri_seq order by res_prio";
		List<Map<String, Object>> resList = getJdbcTemplate().queryForList(resSql);
		
		for (Map<String, Object> map : resList) {
			int pId = (int)map.get("pId");
			if (pId == -1) {
				folderList.add(map);
			}
		}
		
		folderList.forEach(f -> {
			var folderId = (int)f.get("id");
			var menuList = getMenuList(folderId, resList);
			if (!menuList.isEmpty()) {
				f.put("n", menuList);	
			}		
		});
			
        return folderList;
    }
    
    private List<Map<String, Object>> getMenuList(int folderId, List<Map<String, Object>> resList) {
		var returnList = new ArrayList<Map<String, Object>>();
		resList.forEach(r -> {
			int pId = (int)r.get("pId");
			
			if (pId == folderId) {
				returnList.add(r);
			}
		});
		return returnList;
	}
}