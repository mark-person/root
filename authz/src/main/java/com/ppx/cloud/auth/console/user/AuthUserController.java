package com.ppx.cloud.auth.console.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.pojo.AuthAccount;
import com.ppx.cloud.auth.pojo.AuthUser;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.page.Page;


/**
 * 商户管理
 * @author mark
 * @date 2018年7月2日
 */
@Controller
public class AuthUserController {

	@Autowired
	private AuthUserServiceImpl impl;
	
    public ModelAndView listAuthUser() {		
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listJson(new Page(), new AuthUser()));
		return mv;
	}
	
	public Map<?, ?> listJson(Page page, AuthUser pojo) {
		var list = impl.listAuthUser(page, pojo);
		return ReturnMap.of(page, list);
	}
	
	public Map<?, ?> insertAuthUser(AuthUser bean) {
		return impl.insertAuthUser(bean);
	}
	
	public Map<?, ?> getAuthUser(@RequestParam Integer id) {
		return ReturnMap.of("pojo", impl.getAuthUser(id));
	}
	
	public Map<?, ?> getAuthAccount(@RequestParam Integer id) {
		return ReturnMap.of("pojo", impl.getAuthAccount(id));
	}
	
	public Map<?, ?> updateAuthUser(AuthUser pojo) {
		return impl.updateAuthUser(pojo);
	}
	
	public Map<?, ?> updateAuthAccount(AuthAccount pojo) {
		return impl.updateAuthAccount(pojo);
	}
	
	public Map<?, ?> updateAuthUserPassword(@RequestParam Integer userId, @RequestParam String userPassword) {
		return impl.updateAuthUserPassword(userId, userPassword);
	}
	
	public Map<?, ?> deleteAuthUser(Integer id) {
		return impl.deleteAuthUser(id);
	}
	
    public Map<?, ?> disable(Integer id) {
        return impl.disable(id);
    }
	
    public Map<?, ?> enable(Integer id) {
        return impl.enable(id);
    }

}