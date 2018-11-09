package com.ppx.cloud.demo.product;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.context.MyContext;
import com.ppx.cloud.common.context.User;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.exception.custom.PermissionParamsException;
import com.ppx.cloud.common.page.Page;

@Controller
public class ProductController {
	
	@Autowired
	private ProductServiceImpl impl;
	
	private Set<Integer> USER_SET = Set.of(0, 1, 2);

	
	public ModelAndView product() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list(new Page(), new Product()));
		return mv;
	}
	
	public Map<?, ?> list(Page page, Product pojo) {
		return ControllerReturn.success(page, impl.list(page, pojo));
	}
	
	public ModelAndView addProduct(@RequestParam Integer u, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		
		String userAgent = (String)request.getHeader("user-agent");
		System.out.println("00000000userAgent:" + userAgent);
		
		// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36
		
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
