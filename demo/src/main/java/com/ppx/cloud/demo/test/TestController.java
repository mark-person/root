package com.ppx.cloud.demo.test;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;

@Controller
public class TestController {

	@Autowired
	private TestServiceImpl impl;

	public ModelAndView test() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list(new Page(), new TestPojo()));

		return mv;
	}
	
	public Map<?, ?> list(Page page, TestPojo pojo) {
		return ControllerReturn.success(page, impl.list(page, pojo));
	}

	public Map<?, ?> testtest() {
		int r = impl.test();
		return ControllerReturn.success(Map.of("valuexx001", r));
	}

	

}
