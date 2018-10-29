package com.ppx.cloud.demo.product;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.exception.custom.PermissionParamsException;

@Controller
public class ProductController {
	
	Set<Integer> USER_SET = Set.of(0, 1);
	
	public ModelAndView add(@RequestParam Integer u, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		
		if (!USER_SET.contains(u)) {
			throw new PermissionParamsException("用户ID:" + u + "错误");
    	}
		
		return mv;
	}
	
	
	
}
