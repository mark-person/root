package com.ppx.cloud.demo.test;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ReturnMap;

@Controller
public class Test2Controller {

	
	@Autowired
	private Test2ServiceImpl impl2;

	
	public ModelAndView testJson(ModelAndView mv) {
		return mv;
	}
	
	
	public Map<?, ?> getJson(@RequestBody Test test) {
		System.out.println("...........a:" + test.getTestId());
		return ReturnMap.of();
	}
    
    public Map<?, ?> test2() {
    	impl2.test();
		return ReturnMap.of();
		
    }
	    
}
