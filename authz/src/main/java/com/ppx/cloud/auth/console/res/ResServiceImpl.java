package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.LoginAccount;
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
		LoginAccount account = AuthContext.getLoginAccount();
		String resSql = "";
		// 管理员查看所有资源，用户主账号只能查看已经分配的资源(子帐号的userId就是用户主账号)
		Object[] param = {};
		if (account.isAdmin()) {
			resSql = "select res_id id, parent_id pId, res_name text, if (res_type = 0, 'fa fa-folder', if (res_type = 1, 'fa fa-file', 'fa fa-cogs'))"
				+ " icon from auth_res order by res_prio";
		}
		else {
			resSql = "select res_id id, parent_id pId, res_name text, if (res_type = 0, 'fa fa-folder', if (res_type = 1, 'fa fa-file', 'fa fa-cogs'))"
					+ " icon from auth_res r where exists(select 1 from auth_grant g where g.account_id = ? and r.res_id = g.res_id) order by res_prio";
			param = new Object[]{account.getAccountId()};
		}
		List<Map<String, Object>> resList = getJdbcTemplate().queryForList(resSql, param);
		
		
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
			
			for (Map<String, Object> m : menuList) {
				var menuId = (int)m.get("id");
				var actionList = getMenuList(menuId, resList);
				if (!actionList.isEmpty()) {
					m.put("nodes", actionList);	
				}
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
	
	
	
	// >>>>>>>>>>>>>>>>>>>>>>>>.new
	@Transactional
	public int insertRes(int parentId, String resName, int resType, String menuUri) {
		String countSql = "select count(*) from auth_res where parent_id = ?";
		int c = getJdbcTemplate().queryForObject(countSql, Integer.class, parentId);
		
		// 1:菜单 auth_res对应的菜单uri_seq要加上
		if (resType == 1) {
			String insertSeqSql = "insert ignore into auth_uri_seq(uri_text) values(?)";
			getJdbcTemplate().update(insertSeqSql, menuUri);
			
			String seqSql = "select uri_seq from auth_uri_seq where uri_text = ?";
			int uriSeq = getJdbcTemplate().queryForObject(seqSql, Integer.class, menuUri);
			
			String sql = "insert into auth_res(parent_id, res_name, res_prio, res_type, uri_seq)"
					+ " values(?, ?, ?, ?, ?)";
			getJdbcTemplate().update(sql, parentId, resName, c + 1, resType, uriSeq);
			
			String insertResUriSql = "insert into auth_res_uri(res_id, uri_seq) value(LAST_INSERT_ID(), ?)";
			getJdbcTemplate().update(insertResUriSql, uriSeq);
		}
		else {
			String sql = "insert into auth_res(parent_id, res_name, res_prio, res_type)"
					+ " values(?, ?, ?, ?)";
			getJdbcTemplate().update(sql, parentId, resName, c + 1, resType);
		}
		return getLastInsertId();
		
	}
	
	public int updateRes(int id, String resName) {
		String sql = "update auth_res set res_name = ? where res_id = ?";
		return getJdbcTemplate().update(sql, resName, id);
	}
	
	@Transactional
	public int deleteRes(int id) {
		String deleteUriSeq = "delete from auth_res_uri where res_id in (select res_id from auth_res where parent_id = ?) or res_id = ?";
		getJdbcTemplate().update(deleteUriSeq, id, id);
		
		String pSql = "delete from auth_res where parent_id = ?";
		getJdbcTemplate().update(pSql, id);
		String sql = "delete from auth_res where res_id = ?";
		return getJdbcTemplate().update(sql, id);
	}
	
	public int updateResPrio(String ids) {
		String[] id = ids.split(",");
		List<Object[]> paramList = new ArrayList<Object[]>();
		for (int i = 0; i < id.length; i++) {
			Object[] obj = {i, Integer.parseInt(id[i])};
			paramList.add(obj);
		}
		String sql = "update auth_res set res_prio = ? where res_id = ?";
		getJdbcTemplate().batchUpdate(sql, paramList);
		return 1;
	}
	
	@Transactional
	public int insertUri(Integer resId, String uri, Integer menuId) {
		String insertSeqSql = "insert ignore into auth_uri_seq(uri_text) values(?)";
		getJdbcTemplate().update(insertSeqSql, uri);
		
		String seqSql = "select uri_seq from auth_uri_seq where uri_text = ?";
		int uriSeq = getJdbcTemplate().queryForObject(seqSql, Integer.class, uri);
		
		String insertResUriSql = "insert into auth_res_uri(res_id, uri_seq) value(?, ?)";
		return getJdbcTemplate().update(insertResUriSql, resId, uriSeq);
	}
	
	public List<Map<String, Object>> getUri(int resId) {
		String sql = "select s.uri_seq, s.uri_text, if((select uri_seq from auth_res where res_id = ?) = s.uri_seq, 1, 0) is_menu" + 
				" from auth_uri_seq s join auth_res_uri u on s.uri_seq = u.uri_seq where u.res_id = ? order by uri_seq";
		return getJdbcTemplate().queryForList(sql, resId, resId);
	}
	
	public int deleteUri(int resId, int uriSeq) {
		String sql = "delete from auth_res_uri where res_id = ? and uri_seq = ?";
		return getJdbcTemplate().update(sql, resId, uriSeq);
	}
	
	
}
