package com.ppx.cloud.demo.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

	

	@RequestMapping(path = "/test")
	@ResponseBody
	public Map<String, Object> test() {
		var map = new HashMap<String, Object>();
		
		
		
		
		
		map.put("result", 4);
		return map;
	}
}
