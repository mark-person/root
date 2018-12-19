package com.ppx.cloud.auth.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		

		// 允许的菜单
		LoginAccount account = AuthContext.getLoginAccount();
		int accountId = account.getAccountId();
		Set<Integer> permitResIdSet = grantService.getGrantResIds(accountId);
		
		if (account.isMainAccount()) {
			returnList = filterNode(returnList, permitResIdSet, null);
		}
		else {
			// 不是主帐号则判断是否权限是否主帐号权限	
			Set<Integer> mainPermitResIdSet = grantService.getGrantResIds(account.getUserId());
			returnList = filterNode(returnList, permitResIdSet, mainPermitResIdSet);
		}
		
		return returnList;
	}
	
	/**
	 * 当子账号登录时，显示的菜单必须是主账号拥有的
	 */
	private List<Map<String, Object>> filterNode(List<Map<String, Object>> resList, 
			Set<Integer> permitResIdSet, Set<Integer> mainPermitResIdSet) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> dirMap : resList) {
			Integer resId = (Integer)dirMap.get("id");
			if (mainPermitResIdSet == null) {
				if (permitResIdSet.contains(resId)) {
					returnList.add(dirMap);
				}
			}
			else {
				// 子帐号需要判断主账号权限
				if (permitResIdSet.contains(resId) && mainPermitResIdSet.contains(resId)) {
					returnList.add(dirMap);
				}
			}
			
			var newMenuList = new ArrayList<Map<String, Object>>();
			@SuppressWarnings("unchecked")
			var menuList = (List<Map<String, Object>>)dirMap.get("n");
			if (menuList == null) {
				continue;
			}
			for (Map<String, Object> menuMap : menuList) {
				Integer menuId = (Integer)menuMap.get("id");
				if (mainPermitResIdSet == null) {
					if (permitResIdSet.contains(menuId)) {
						newMenuList.add(menuMap);
					}
				}
				else {
					// 子帐号需要判断主账号权限
					if (permitResIdSet.contains(menuId) && mainPermitResIdSet.contains(menuId)) {
						newMenuList.add(menuMap);
					}
				}
			}
			dirMap.put("n", newMenuList);
		}
		
		return returnList;
	}

}
