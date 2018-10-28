package com.ppx.cloud;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;

@Controller
public class GoTestController {
	
	
	public ModelAndView list() {
		return new ModelAndView();
	}
	
	public Map<Object, Object> helloWorld9() {
		System.out.println(".....................helloWorld");
		return ControllerReturn.SUCCESS;
	}
	
	public Map<?, ?> helloWorld2() {
		System.out.println(".....................helloWorld");
		return ControllerReturn.SUCCESS;
	}
	
	
}
