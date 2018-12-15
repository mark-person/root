package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppx.cloud.common.jdbc.MyDaoSupport;

/**
 * 资源管理
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class ResServiceImpl extends MyDaoSupport implements ResService {
	
//	@Autowired
//    private EhCacheService ehCacheServ;
	
//	@Autowired
//    private GrantService grantService;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getResource() {
		var returnMap = new HashMap<String, Object>();
//		Query query = Query.query(Criteria.where("_id").is(0));
//		LinkedHashMap<String, Object> resMap = mongoTemplate.findOne(query, LinkedHashMap.class, COL_RESOURCE);	
//		
//		LoginAccount account = AuthContext.getLoginAccount();
//		if (!account.isAdmin()) {
//			// 管理员查看所有资源，商户主账号只能查看已经分配的资源(子帐号的merId就是商户主账号)
//			List<Integer> permitResIdList = grantService.getGrantResIds(account.getMerId());		
//			Map<String, Object> returnMap = new LinkedHashMap<String, Object>();
//			returnMap.put("_id", 0);
//			returnMap.put("tree", filterNode((LinkedHashMap<String, Object>)resMap.get("tree"), permitResIdList));
//			return returnMap;
//		}
//		
//		return resMap;
		
		String resSql = "select res_id id, parent_id pId, res_name text, if (res_type = 0, 'fa fa-folder', 'fa fa-file') icon from auth_res";
		List<Map<String, Object>> resList = getJdbcTemplate().queryForList(resSql);
		
		var folderList = new ArrayList<Map<String, Object>>();
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
				f.put("nodes", menuList);	
			}		
		});
		
		
		var resMap = Map.of("id", -1, "text", "资源", "icon", "fa fa-home");
		var rootMap = new HashMap<String, Object>(resMap);
		
		rootMap.put("nodes", folderList);
		
		
		returnMap.put("tree", rootMap);
		
		
		return returnMap;
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
	
	public void saveResource(String tree, String removeIds) {
	    
//	    ehCacheServ.increaseAllDbVersion();
//				
//		if (!StringUtils.isEmpty(removeIds)) {
//			String[] resId = removeIds.split(",");	
//			Query removeQuery = Query.query(Criteria.where("_id").in(new ArrayList<String>(Arrays.asList(resId))));
//			mongoTemplate.remove(removeQuery, COL_RESOURCE_URI);
//		}
//		
//		Update update = Update.update("tree", BasicDBObject.parse(tree));
//		Query query = Query.query(Criteria.where("_id").is(0));
//		mongoTemplate.upsert(query, update, COL_RESOURCE);
	}
	
	@SuppressWarnings("rawtypes")
	public Map getUri(Integer resId) {
//		Query query = Query.query(Criteria.where("_id").is(resId));
//		return mongoTemplate.findOne(query, Map.class, COL_RESOURCE_URI);
		return null;
	}	
	
	@SuppressWarnings("rawtypes")
	private int saveToUri(String uri) {
//		int index = getUriSeq();
//		Update update = new Update();
//		update.setOnInsert("index", index);
//		
//		Query uriQuery = Query.query(Criteria.where("_id").is(uri));
//		Map uriMap = mongoTemplate.findAndModify(uriQuery, update, FindAndModifyOptions.options().upsert(true).returnNew(true),
//				Map.class, "grant_uri_index");			
//		return (Integer)uriMap.get("index");
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	private int getUriSeq() {
//		Query seqQuery = Query.query(Criteria.where("_id").is("URI_SEQ"));
//		Update update = new Update();
//		update.inc("value", 1);
//		Map seqMap = mongoTemplate.findAndModify(seqQuery, update, FindAndModifyOptions.options().upsert(true).returnNew(true)
//				,Map.class, COL_SEQUENCE);
//		int seq = (Integer)seqMap.get("sequence_value");
//		return seq;
		
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	public Map saveUri(Integer resId, String uri, Integer menuId) {	
		
//	    ehCacheServ.increaseAllDbVersion();
//				
//		Update update = new Update();	
//		String[] uriArray = uri.split(",");
//		List<String> uriList = new ArrayList<String>();
//		List<Integer> indexList = new ArrayList<Integer>();
//		for (String u : uriArray) {
//			uriList.add(u);
//			int index = saveToUri(u);
//			indexList.add(index);
//		}		
//		update.addToSet("uri").each(uriList.toArray());
//		update.addToSet("uriIndex").each(indexList.toArray());
//		if (menuId != null) {
//			update.set("pMenuId", menuId);
//		}
//		
//		Query query = Query.query(Criteria.where("_id").is(resId));
//		Map map = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true),
//				Map.class, COL_RESOURCE_URI);
//		return map;
		
		return null;
	}
	
	
	public void removeUri(Integer resId, String uri, int uriIndex) {
	    
//	    ehCacheServ.increaseAllDbVersion();
//				
//		Query query = Query.query(Criteria.where("_id").is(resId));
//		
//		if ("-1".equals(uri)) {
//			// mongodb数组只有一条数据时，删除整条记录
//			mongoTemplate.remove(query, COL_RESOURCE_URI);
//		}
//		else {
//			Update update = new Update();	
//			update.pull("uri", uri);
//			update.pull("uriIndex", uriIndex);
//			mongoTemplate.updateFirst(query, update, COL_RESOURCE_URI);
//		}
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
	
	
	
	// >>>>>>>>>>>>>>>>.new
	
	public int insertRes(int parentId, String resName, int resType, Integer uriSeq) {
		String sql = "insert into auth_res(parent_id, res_name, res_type, uri_seq) values(?, ?, ?, ?)";
		return getJdbcTemplate().update(sql, parentId, resName, resType, uriSeq);
	}
	
	public int updateRes(int id, String resName) {
		String sql = "update auth_res set res_name = ? where res_id = ?";
		return getJdbcTemplate().update(sql, resName, id);
	}
	
	@Transactional
	public int deleteRes(int id) {
		String pSql = "delete from auth_res where parent_id = ?";
		getJdbcTemplate().update(pSql, id);
		String sql = "delete from auth_res where res_id = ?";
		return getJdbcTemplate().update(sql, id);
	}
	
}
