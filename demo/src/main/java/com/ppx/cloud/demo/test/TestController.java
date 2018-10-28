package com.ppx.cloud.demo.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

	@Autowired
	private TestServiceImpl impl;
	
	public ModelAndView list() {
		ModelAndView mv = new ModelAndView();
		
		return mv;
	}

	public Map<?, ?> test() {
		var map = new HashMap<String, Object>();
		impl.test();
		
		
		map.put("result", 4);
		return map;
	}
}
