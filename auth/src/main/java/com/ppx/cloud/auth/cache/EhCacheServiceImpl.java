package com.ppx.cloud.auth.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.ppx.cloud.auth.common.AuthMongoSupport;


/**
 * 分两部分刷新:一个是刷新所有(菜单修改后)，一个是刷新权限部分(修改权限后)
 * @author mark
 * @date 2018年6月22日
 */
@Service
public class EhCacheServiceImpl extends AuthMongoSupport implements EhCacheService {
	
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
	
	@SuppressWarnings("rawtypes")
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadUriIndex'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<String, Integer> loadUriIndex() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        List<Map> list = mongoTemplate.findAll(Map.class, COL_URI_INDEX);
        if (list == null) {
            return returnMap;
        }
        for (Map map : list) {
            returnMap.put((String)map.get("_id"), (Integer)map.get("index"));
        }
        return returnMap;
    }
    
    
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadMenuResourceUri'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, List<Map>> loadMenuResourceUri() {
        Map<String, List<Map>> returnMap = new HashMap<String, List<Map>>();
        
        Map<Integer, Map> IdMap = new HashMap<Integer, Map>();
        Map<Integer, List<Map>> pMenuIdMap = new HashMap<Integer, List<Map>>();
        
        List<Map> list = mongoTemplate.findAll(Map.class, COL_RESOURCE_URI);
        if (list == null) {
            return returnMap;
        }
        for (Map map : list) {
            IdMap.put((Integer)map.get("_id"), map);
            
            Integer pMenuId = (Integer)map.get("pMenuId");
            if (pMenuId != null) {
                List<Map> tmpList = pMenuIdMap.get(pMenuId);
                tmpList = tmpList == null ? new ArrayList<Map>() : tmpList;
                tmpList.add(map);
                pMenuIdMap.put(pMenuId, tmpList);
            }
        }
        
        pMenuIdMap.forEach((pMenuId, value) -> {
            Map map = IdMap.get(pMenuId);
            List<String> uriList = (List<String>)map.get("uri");
            returnMap.put((String)uriList.get(0), value);
        });
        return returnMap;
    }
    
    @SuppressWarnings("rawtypes")
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadResouceUri'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<Integer, Map> loadResouceUri() {
        Map<Integer, Map> returnMap = new HashMap<Integer, Map>();
        
        List<Map> list = mongoTemplate.findAll(Map.class, COL_RESOURCE_URI);
        if (list == null) {
            return returnMap;
        }
        for (Map map : list) {
            returnMap.put((Integer)map.get("_id"), map);
        }
        return returnMap;
    }
    
    @SuppressWarnings("rawtypes")
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadResource'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map loadResource() {
        Query query = Query.query(Criteria.where("_id").is(0));
        Map map =  mongoTemplate.findOne(query, Map.class, COL_RESOURCE);
        return map;
    }
}