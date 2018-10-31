package com.ppx.cloud.demo.test;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;

@Controller
public class TestController {

	@Autowired
	private TestServiceImpl impl;

	public ModelAndView list() {
		ModelAndView mv = new ModelAndView();

		return mv;
	}

	public Map<?, ?> test() {
		int r = impl.test();
		return ControllerReturn.success(Map.of("valuexx001", r));
	}

	

}
