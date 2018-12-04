package com.ppx.cloud.monitor.view;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.util.ApplicationUtils;


@Controller
public class MonitorViewController {
	
	@Autowired
	private MonitorViewServiceImpl impl;
	
	private final String TITLE = "实时多机监控平台V0.1.0";
	
	
    public ModelAndView index(ModelAndView mv) {
		
		List<Map> serviceList = impl.listDisplayService();
		for (Map map : serviceList) {
			
		}
		mv.addObject("listMachine", serviceList);	
		mv.addObject("currentServiceId", ApplicationUtils.getServiceId());
		mv.addObject("title", TITLE);
		return mv;
	}
	
    public ModelAndView service() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("listJson", listAllService());
		mv.addObject("title", TITLE);
		return mv;
	}
	
	public Map<?, ?> listAllService() {	
		List<Map> list = impl.listAllService();
		return ControllerReturn.success(list);
	}
	
	
}