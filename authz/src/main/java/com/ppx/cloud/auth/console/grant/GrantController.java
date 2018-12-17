package com.ppx.cloud.auth.console.grant;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.console.res.ResService;
import com.ppx.cloud.auth.pojo.AuthUser;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;


/**
 * 分配权限
 * @author mark
 * @date 2018年7月2日
 */
@Controller
public class GrantController {
	
	@Autowired
	private GrantServiceImpl impl;
	
	@Autowired
	private ResService resourceServ;
	
    public ModelAndView grantToUser() {			
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listUser(new Page(), new AuthUser()));
		return mv;
	}
	
	public Map<Object, Object> listUser(Page page, AuthUser user) {
		List<AuthUser> list = impl.listUser(page, user);
		return ControllerReturn.success(list, page);
	}
	
	public Map<Object, Object> getAuthsorize(@RequestParam Integer accountId) {
	    Map<?, ?> resMap = resourceServ.getResource();
        if (resMap == null) {
            return ControllerReturn.success(-1);
        }
        Map<Object, Object> returnMap = ControllerReturn.success(resMap);
        
		returnMap.put("resIds", impl.getGrantResIds(accountId));
		return returnMap;
	}
	
	public Map<Object, Object> saveAuthorize(@RequestParam Integer accountId, @RequestParam String resIds) {
		long r = impl.saveGrantResIds(accountId, resIds);
		return ControllerReturn.success(r);
	}

}