package com.ppx.cloud.auth.console.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.pojo.AuthAccount;
import com.ppx.cloud.auth.pojo.AuthUser;
import com.ppx.cloud.common.contoller.ControllerReturn;
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
	
	public Map<Object, Object> listJson(Page page, AuthUser pojo) {
		var list = impl.listAuthUser(page, pojo);
		return ControllerReturn.success(list, page);
	}
	
	public Map<Object, Object> insertAuthUser(AuthUser bean) {
		int r = impl.insertAuthUser(bean);
		return ControllerReturn.success(r);
	}
	
	public Map<Object, Object> getAuthUser(@RequestParam Integer id) {
		return ControllerReturn.success(impl.getAuthUser(id));
	}
	
	public Map<Object, Object> getAuthAccount(@RequestParam Integer id) {
		return ControllerReturn.success(impl.getAuthAccount(id));
	}
	
	public Map<Object, Object> updateAuthUser(AuthUser pojo) {
		return ControllerReturn.success(impl.updateAuthUser(pojo));
	}
	
	public Map<Object, Object> updateAuthAccount(AuthAccount pojo) {
		return ControllerReturn.success(impl.updateAuthAccount(pojo));
	}
	
	public Map<Object, Object> updateAuthUserPassword(@RequestParam Integer userId, @RequestParam String userPassword) {
		int r = impl.updateAuthUserPassword(userId, userPassword);
		return ControllerReturn.success(r);
	}
	
	public Map<Object, Object> deleteAuthUser(Integer id) {
		return ControllerReturn.success(impl.deleteAuthUser(id));
	}
	
    public Map<Object, Object> disable(Integer id) {
        return ControllerReturn.success(impl.disable(id));
    }
	
    public Map<Object, Object> enable(Integer id) {
        return ControllerReturn.success(impl.enable(id));
    }

}