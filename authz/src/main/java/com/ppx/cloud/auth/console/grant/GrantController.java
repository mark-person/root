package com.ppx.cloud.auth.console.grant;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.console.res.ResService;
import com.ppx.cloud.auth.pojo.AuthUser;
import com.ppx.cloud.common.contoller.ReturnMap;
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
	
	public Map<?, ?> listUser(Page page, AuthUser user) {
		List<AuthUser> list = impl.listUser(page, user);
		return ReturnMap.of(page, list);
	}
	
	public Map<?, ?> getAuthorize(@RequestParam Integer accountId) {
	    Map<?, ?> resMap = resourceServ.getResource();
        return ReturnMap.of("resIds", impl.getGrantResIds(accountId), "tree", resMap);
	}
	
	public Map<?, ?> saveAuthorize(@RequestParam Integer accountId, @RequestParam String resIds) {
		return impl.saveGrantResIds(accountId, resIds);
	}

}