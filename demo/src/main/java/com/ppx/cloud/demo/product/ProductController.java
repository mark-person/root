package com.ppx.cloud.demo.product;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.context.MyContext;
import com.ppx.cloud.common.context.User;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.exception.custom.PermissionParamsException;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.demo.test.Test;

@Controller
public class ProductController {
	
	@Autowired
	private ProductServiceImpl impl;
	
	private Set<Integer> USER_SET = Set.of(0, 1);

	
	public ModelAndView product() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list(new Page(), new Product()));
		return mv;
	}
	
	public Map<?, ?> list(Page page, Product pojo) {
		return ControllerReturn.success(page, impl.list(page, pojo));
	}
	
	public ModelAndView add(@RequestParam Integer u, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		
		if (!USER_SET.contains(u)) {
			throw new PermissionParamsException("用户ID:" + u + "错误");
    	}
		else {
			var user = new User();
			user.setUserId(u);
			user.setUserName("name" + u);
			MyContext.setUser(user);
		}
		
		return mv;
	}
	
	
	
}
