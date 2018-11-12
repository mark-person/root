package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.ppx.cloud.auth.cache.EhCacheService;
import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.AuthMongoSupport;
import com.ppx.cloud.auth.common.LoginAccount;
import com.ppx.cloud.auth.console.grant.GrantService;

/**
 * 资源管理
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class ResourceServiceImpl extends AuthMongoSupport implements ResourceService {
	
	@Autowired
    private EhCacheService ehCacheServ;
	
	@Autowired
    private GrantService grantService;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getResource() {
		Query query = Query.query(Criteria.where("_id").is(0));
		LinkedHashMap<String, Object> resMap = mongoTemplate.findOne(query, LinkedHashMap.class, COL_RESOURCE);	
		
		LoginAccount account = AuthContext.getLoginAccount();
		if (!account.isAdmin()) {
			// 管理员查看所有资源，商户主账号只能查看已经分配的资源(子帐号的merId就是商户主账号)
			List<Integer> permitResIdList = grantService.getGrantResIds(account.getMerId());		
			Map<String, Object> returnMap = new LinkedHashMap<String, Object>();
			returnMap.put("_id", 0);
			returnMap.put("tree", filterNode((LinkedHashMap<String, Object>)resMap.get("tree"), permitResIdList));
			return returnMap;
		}
		
		return resMap;
	}
	
	public void saveResource(String tree, String removeIds) {
	    
	    ehCacheServ.increaseAllDbVersion();
				
		if (!StringUtils.isEmpty(removeIds)) {
			String[] resId = removeIds.split(",");	
			Query removeQuery = Query.query(Criteria.where("_id").in(new ArrayList<String>(Arrays.asList(resId))));
			mongoTemplate.remove(removeQuery, COL_RESOURCE_URI);
		}
		
		Update update = Update.update("tree", BasicDBObject.parse(tree));
		Query query = Query.query(Criteria.where("_id").is(0));
		mongoTemplate.upsert(query, update, COL_RESOURCE);
	}
	
	@SuppressWarnings("rawtypes")
	public Map getUri(Integer resId) {
		Query query = Query.query(Criteria.where("_id").is(resId));
		return mongoTemplate.findOne(query, Map.class, COL_RESOURCE_URI);
	}	
	
	@SuppressWarnings("rawtypes")
	private int saveToUri(String uri) {
		int index = getUriSeq();
		Update update = new Update();
		update.setOnInsert("index", index);
		
		Query uriQuery = Query.query(Criteria.where("_id").is(uri));
		Map uriMap = mongoTemplate.findAndModify(uriQuery, update, FindAndModifyOptions.options().upsert(true).returnNew(true),
				Map.class, "grant_uri_index");			
		return (Integer)uriMap.get("index");
	}
	
	@SuppressWarnings("rawtypes")
	private int getUriSeq() {
		Query seqQuery = Query.query(Criteria.where("_id").is("URI_SEQ"));
		Update update = new Update();
		update.inc("value", 1);
		Map seqMap = mongoTemplate.findAndModify(seqQuery, update, FindAndModifyOptions.options().upsert(true).returnNew(true)
				,Map.class, COL_SEQUENCE);
		int seq = (Integer)seqMap.get("sequence_value");
		return seq;
	}
	
	@SuppressWarnings("rawtypes")
	public Map saveUri(Integer resId, String uri, Integer menuId) {	
		
	    ehCacheServ.increaseAllDbVersion();
				
		Update update = new Update();	
		String[] uriArray = uri.split(",");
		List<String> uriList = new ArrayList<String>();
		List<Integer> indexList = new ArrayList<Integer>();
		for (String u : uriArray) {
			uriList.add(u);
			int index = saveToUri(u);
			indexList.add(index);
		}		
		update.addToSet("uri").each(uriList.toArray());
		update.addToSet("uriIndex").each(indexList.toArray());
		if (menuId != null) {
			update.set("pMenuId", menuId);
		}
		
		Query query = Query.query(Criteria.where("_id").is(resId));
		Map map = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true),
				Map.class, COL_RESOURCE_URI);
		return map;
	}
	
	
	public void removeUri(Integer resId, String uri, int uriIndex) {
	    
	    ehCacheServ.increaseAllDbVersion();
				
		Query query = Query.query(Criteria.where("_id").is(resId));
		
		if ("-1".equals(uri)) {
			// mongodb数组只有一条数据时，删除整条记录
			mongoTemplate.remove(query, COL_RESOURCE_URI);
		}
		else {
			Update update = new Update();	
			update.pull("uri", uri);
			update.pull("uriIndex", uriIndex);
			mongoTemplate.updateFirst(query, update, COL_RESOURCE_URI);
		}
	}
	
	
	/**
	 * 根据permitResIdList,过滤掉没有分配的资源
	 */
	private Map<String, Object> filterNode(LinkedHashMap<String, Object> treeMap, List<Integer> permitResIdList) {
		
		Map<String, Object> newNode = new LinkedHashMap<String, Object>();
		newNode.put("t", treeMap.get("t"));
		newNode.put("i", treeMap.get("i"));
		newNode.put("id", treeMap.get("id"));
		
		@SuppressWarnings("unchecked")
		List<LinkedHashMap<String, Object>> list = (ArrayList<LinkedHashMap<String, Object>>)treeMap.get("n");
		if (list != null) {
			List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
			for (LinkedHashMap<String, Object> map : list) {
				Map<String, Object> newMap = filterNode(map, permitResIdList);
				Integer tmpId = (Integer)newMap.get("id");
				if (permitResIdList.contains(tmpId)) {					
					newList.add(newMap);
				}
			}
			if (!newList.isEmpty()) {				
				newNode.put("n", newList);
			}
		}
		return newNode;
	}
	
}
