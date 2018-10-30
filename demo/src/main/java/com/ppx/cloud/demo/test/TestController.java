package com.ppx.cloud.demo.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.security.MD5Encoder;
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

	public static void main(String[] args) {
		System.out.println(".....................1");
		
		
		String a = MD5Encoder.encode("xxxxxxxxxxxxxxxxxxxxxxxxxxxx".getBytes());
		System.out.println("a:" + a);
		

	}

}
