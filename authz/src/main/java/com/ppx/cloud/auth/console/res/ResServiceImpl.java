package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.LoginAccount;
import com.ppx.cloud.common.contoller.ReturnMap;
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
			var menuList = getChildList(folderId, resList);
			if (!menuList.isEmpty()) {
				f.put("nodes", menuList);	
			}		
			
			for (Map<String, Object> m : menuList) {
				var menuId = (int)m.get("id");
				var actionList = getChildList(menuId, resList);
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
	
	private List<Map<String, Object>> getChildList(int parentId, List<Map<String, Object>> resList) {
		var returnList = new ArrayList<Map<String, Object>>();
		resList.forEach(r -> {
			int pId = (int)r.get("pId");
			if (pId == parentId) {
				returnList.add(r);
			}
		});
		return returnList;
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
	
	public Map<String, Object> updateRes(int id, String resName, String menuUri) {
		if (Strings.isEmpty(menuUri)) {
			String sql = "update auth_res set res_name = ? where res_id = ?";
			getJdbcTemplate().update(sql, resName, id);
		}
		else {
			String insertSeqSql = "insert ignore into auth_uri_seq(uri_text) values(?)";
			getJdbcTemplate().update(insertSeqSql, menuUri);
			
			String seqSql = "select uri_seq from auth_uri_seq where uri_text = ?";
			int uriSeq = getJdbcTemplate().queryForObject(seqSql, Integer.class, menuUri);
			
			String sql = "update auth_res set res_name = ?, uri_seq = ? where res_id = ?";
			getJdbcTemplate().update(sql, resName, uriSeq, id);
		}
		return ReturnMap.of();
	}
	
	@Transactional
	public Map<String, Object> deleteRes(int id) {
		String deleteUriSeq = "delete from auth_res_uri where res_id in (select res_id from auth_res where parent_id = ?) or res_id = ?";
		getJdbcTemplate().update(deleteUriSeq, id, id);
		
		String pSql = "delete from auth_res where parent_id = ?";
		getJdbcTemplate().update(pSql, id);
		String sql = "delete from auth_res where res_id = ?";
		getJdbcTemplate().update(sql, id);
		return ReturnMap.of();
	}
	
	public Map<String, Object> updateResPrio(String ids) {
		String[] id = ids.split(",");
		List<Object[]> paramList = new ArrayList<Object[]>();
		for (int i = 0; i < id.length; i++) {
			Object[] obj = {i, Integer.parseInt(id[i])};
			paramList.add(obj);
		}
		String sql = "update auth_res set res_prio = ? where res_id = ?";
		getJdbcTemplate().batchUpdate(sql, paramList);
		return ReturnMap.of();
	}
	
	@Transactional
	public Map<String, Object> insertUri(Integer resId, String uri, Integer menuId) {
		String insertSeqSql = "insert ignore into auth_uri_seq(uri_text) values(?)";
		getJdbcTemplate().update(insertSeqSql, uri);
		
		String seqSql = "select uri_seq from auth_uri_seq where uri_text = ?";
		int uriSeq = getJdbcTemplate().queryForObject(seqSql, Integer.class, uri);
		
		String insertResUriSql = "insert into auth_res_uri(res_id, uri_seq) value(?, ?)";
		getJdbcTemplate().update(insertResUriSql, resId, uriSeq);
		return ReturnMap.of();
	}
	
	public List<Map<String, Object>> getUri(int resId) {
		String sql = "select s.uri_seq, s.uri_text, if((select uri_seq from auth_res where res_id = ?) = s.uri_seq, 1, 0) is_menu" + 
				" from auth_uri_seq s join auth_res_uri u on s.uri_seq = u.uri_seq where u.res_id = ? order by uri_seq";
		return getJdbcTemplate().queryForList(sql, resId, resId);
	}
	
	public Map<String, Object> deleteUri(int resId, int uriSeq) {
		String sql = "delete from auth_res_uri where res_id = ? and uri_seq = ?";
		getJdbcTemplate().update(sql, resId, uriSeq);
		return ReturnMap.of();
	}
	
	
}
