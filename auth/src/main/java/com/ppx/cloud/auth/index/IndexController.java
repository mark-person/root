package com.ppx.cloud.auth.index;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.common.LoginAccount;
import com.ppx.cloud.common.contoller.ControllerReturn;


/**
 * 首页
 * @author mark
 * @date 2018年7月2日
 */
@Controller
public class IndexController {
    
    @Autowired
    private PasswordServiceImpl passwrodImpl;
    
    @GetMapping
    public ModelAndView adminHome() {    
        ModelAndView mv = new ModelAndView();
        return mv;
    }   
	
	@GetMapping
    public ModelAndView editPassword() {	
		ModelAndView mv = new ModelAndView();
		return mv;
	}	
	
	@PostMapping @ResponseBody
	public Map<?, ?> updatePassword(@RequestParam String oldP, @RequestParam String newP) {
		int r = passwrodImpl.updatePassword(oldP, newP);
		return ControllerReturn.success(r);
	}
	
	
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>菜单>>>>>>>>>>>>>>>>>>>>>>
	@Autowired
	private MenuServiceImpl menuService;
	
	@GetMapping
	public ModelAndView menu(HttpServletRequest request) {		
		ModelAndView mv = new ModelAndView();
	
		LoginAccount account = AuthContext.getLoginAccount();
		mv.addObject("account", account);
		if (account.isAdmin()) {			
			mv.addObject("menu", getAdminResource());
		}
		else {
		    List<Map<String, Object>> menu = menuService.getMenu();
		    if (menu == null) {
		        menu = new ArrayList<Map<String, Object>>();
		    }
		    if (account.isMainAccount()) {
		        menu.addAll(getMainAcountResource());
		    }
			mv.addObject("menu", menu);
		}
		return mv;
	}
	
	private List<Map<String, Object>> getMainAcountResource() {
        // 超级管理员的固定菜单
        List<Map<String, Object>> dirList = new ArrayList<Map<String, Object>>();           
        List<Map<String, Object>> menuList = new ArrayList<Map<String, Object>>();
        
        // 菜单项1
        Map<String, Object> menuMap = new LinkedHashMap<String, Object>();
        menuMap.put("t", "子帐号管理");
        menuMap.put("i", -1);
        menuMap.put("uri", "/child/child");
        menuList.add(menuMap);
        
        menuMap = new LinkedHashMap<String, Object>();
        menuMap.put("t", "子帐号权限");
        menuMap.put("i", -2);
        menuMap.put("uri", "/child/grantToChild");
        menuList.add(menuMap);
        
        // 目录项0
        Map<String, Object> systemMap = new LinkedHashMap<String, Object>();
        systemMap.put("t", "子帐号管理");
        systemMap.put("i", 0);
        systemMap.put("n", menuList);
        
        dirList.add(systemMap);
        return dirList;
    }
	
	private List<Map<String, Object>> getAdminResource() {
		// 超级管理员的固定菜单
		List<Map<String, Object>> dirList = new ArrayList<Map<String, Object>>();			
		List<Map<String, Object>> menuList = new ArrayList<Map<String, Object>>();
		
		// 菜单项1
		Map<String, Object> menuMap = new LinkedHashMap<String, Object>();
		menuMap.put("t", "资源管理");
		menuMap.put("i", 1);
		menuMap.put("uri", "/resource/resource");
		menuList.add(menuMap);
		
		menuMap = new LinkedHashMap<String, Object>();
		menuMap.put("t", "商户管理");
		menuMap.put("i", 1);
		menuMap.put("uri", "/merchant/listMerchant");
		menuList.add(menuMap);
		
		menuMap = new LinkedHashMap<String, Object>();
		menuMap.put("t", "权限管理");
		menuMap.put("i", 1);
		menuMap.put("uri", "/grant/grantToMerchant");
		menuList.add(menuMap);
		
		
		// 目录项0
		Map<String, Object> systemMap = new LinkedHashMap<String, Object>();
		systemMap.put("t", "系统管理");
		systemMap.put("i", 0);
		systemMap.put("n", menuList);
		
		dirList.add(systemMap);
		return dirList;
	}
	
}