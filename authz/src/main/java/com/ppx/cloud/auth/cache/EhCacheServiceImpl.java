package com.ppx.cloud.auth.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
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
	
//	@Autowired
//	@Qualifier(EhCacheConfig.LOCAL_MANAGER)
//	private CacheManager cacheManager;
	
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
//        Cache cache = cacheManager.getCache(cacheName);
//        cache.clear();
    }
	
 //   @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadUriIndex'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<String, Integer> loadUriIndex() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
//        List<Map> list = mongoTemplate.findAll(Map.class, COL_URI_INDEX);
//        if (list == null) {
//            return returnMap;
//        }
//        for (Map map : list) {
//            
//        }
        String sql = "select uri_seq, uri_text from auth_uri_seq";
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        for (Map<String, Object> map : list) {
        	returnMap.put((String)map.get("uri_text"), (Integer)map.get("uri_seq"));
		}
        
        return returnMap;
    }
    
    
    @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadMenuResourceUri'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<String, List<Map>> loadMenuResourceUri() {
        Map<String, List<Map>> returnMap = new HashMap<String, List<Map>>();
        
        Map<Integer, Map> IdMap = new HashMap<Integer, Map>();
        Map<Integer, List<Map>> pMenuIdMap = new HashMap<Integer, List<Map>>();
        
//        List<Map> list = mongoTemplate.findAll(Map.class, COL_RESOURCE_URI);
//        if (list == null) {
//            return returnMap;
//        }
//        for (Map map : list) {
//            IdMap.put((Integer)map.get("_id"), map);
//            
//            Integer pMenuId = (Integer)map.get("pMenuId");
//            if (pMenuId != null) {
//                List<Map> tmpList = pMenuIdMap.get(pMenuId);
//                tmpList = tmpList == null ? new ArrayList<Map>() : tmpList;
//                tmpList.add(map);
//                pMenuIdMap.put(pMenuId, tmpList);
//            }
//        }
//        
//        pMenuIdMap.forEach((pMenuId, value) -> {
//            Map map = IdMap.get(pMenuId);
//            List<String> uriList = (List<String>)map.get("uri");
//            returnMap.put((String)uriList.get(0), value);
//        });
        return returnMap;
    }
    
   // @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadResouceUri'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
    public Map<Integer, List<Integer>> loadResouceUri() {
        Map<Integer, List<Integer>> returnMap = new HashMap<Integer, List<Integer>>();
        
//        List<Map> list = mongoTemplate.findAll(Map.class, COL_RESOURCE_URI);
//        if (list == null) {
//            return returnMap;
//        }
//        for (Map map : list) {
//            returnMap.put((Integer)map.get("_id"), map);
//        }
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
    
    // @Cacheable(value=EhCacheConfig.AUTH_FIX_CACHE, key="'loadResource'", cacheManager=EhCacheConfig.LOCAL_MANAGER)
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