package com.ppx.cloud.auth.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.cloud.auth.cache.EhCacheService;
import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.LoginAccount;
import com.ppx.cloud.auth.console.grant.GrantService;


/**
 * 菜单
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class MenuServiceImpl  {
	
	@Autowired
    private GrantService grantService;
	
	@Autowired
    private EhCacheService ehCacheServ;
	
	public List<Map<String, Object>> getMenu() {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		
		returnList = ehCacheServ.loadResource();
		
		
		
		
		if (returnList.isEmpty()) return returnList;
		
		
		
		
//		// 读取资源树
//		LinkedHashMap<String, Object> treeMap = (LinkedHashMap<String, Object>)map.get("tree"); 
//		
//		// 允许的菜单
		LoginAccount account = AuthContext.getLoginAccount();
		
		int accountId = account.getAccountId();
		// List<Integer> permitResIdList = grantService.getGrantResIds(accountId);
		
		Map<String, Object> test = null;		
		if (!account.isMainAccount()) {
			// 不是主帐号则判断是否权限是否主帐号权限	
//			List<Integer> mainPermitResIdList = grantService.getGrantResIds(account.getUserId());
//			test = filterNode(treeMap, uriMap, permitResIdList, mainPermitResIdList);
		}
		else {
			// test = filterNode(treeMap, uriMap, permitResIdList, null);
			
//			var menuList = new ArrayList<Map<String, Object>>();
//			
//			// 菜单项1
//	        Map<String, Object> menuMap = new LinkedHashMap<String, Object>();
//	        menuMap.put("t", "菜单001");
//	        // menuMap.put("i", -1);
//	        menuMap.put("uri", "/auto/child/child");
//	        menuList.add(menuMap);
//	        
//	        menuMap = new LinkedHashMap<String, Object>();
//	        menuMap.put("t", "菜单002");
//	        // menuMap.put("i", -2);
//	        menuMap.put("uri", "/auto/child/grantToChild");
//	        menuList.add(menuMap);
//	        
//	        // 目录项0
//	        Map<String, Object> systemMap = new LinkedHashMap<String, Object>();
//	        systemMap.put("t", "目录001");
//	        // systemMap.put("i", 0);
//	        systemMap.put("n", menuList);
//	        
//	        returnList.add(systemMap);
//	        return returnList;
		}
		
		return returnList;
	}
	
	/**
	 * 当子账号登录时，显示的菜单必须是主账号拥有的
	 */
	private Map<String, Object> filterNode(LinkedHashMap<String, Object> treeMap, 
			Map<Integer, String> uriMap, List<Integer> permitResIdList, List<Integer> mainPermitResIdList) {
		
		Map<String, Object> newNode = new LinkedHashMap<String, Object>();
		String t = (String)treeMap.get("t");
		Integer i = (Integer)treeMap.get("i");
		Integer id = (Integer)treeMap.get("id");
		newNode.put("t", t);
		newNode.put("i", i);
		newNode.put("id", id);
		// 1:菜单
		if (i == 1) {
			newNode.put("uri", uriMap.get(id));
		}
		
		List<LinkedHashMap<String, Object>> list = (ArrayList<LinkedHashMap<String, Object>>)treeMap.get("n");
		if (list == null) {
			return newNode;
		}
		
		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
		for (LinkedHashMap<String, Object> map : list) {
			Map<String, Object> newMap = filterNode(map, uriMap, permitResIdList, mainPermitResIdList);
			
			Integer tmpId = (Integer)newMap.get("id");
			if (mainPermitResIdList == null) {
				// 2:操作
				if ((Integer)newMap.get("i") != 2 && permitResIdList.contains(tmpId)) {	
					newList.add(newMap);
				}
			}
			else {
				// 2:操作
				if ((Integer)newMap.get("i") != 2 && permitResIdList.contains(tmpId) && mainPermitResIdList.contains(tmpId)) {	
					newList.add(newMap);
				}
			}
		}
		// 如果空不加
		if (!newList.isEmpty()) {				
			newNode.put("n", newList);
		}
		return newNode;
	}

}
