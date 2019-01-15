package com.ppx.cloud.config.set;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.config.Config;

@Controller
public class ConfigSetController {

	@Autowired
	private ConfigSetServImpl impl;
	
	
	public ModelAndView configSet(ModelAndView mv) {
		mv.addObject("list", list(new Page(), new Config()));
		return mv;
	}
	
	public Map<?, ?> list(Page page, Config pojo) {
		return ReturnMap.of(page, impl.list(page, pojo));
	}
}
