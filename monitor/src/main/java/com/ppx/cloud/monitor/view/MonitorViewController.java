package com.ppx.cloud.monitor.view;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.common.util.DateUtils;


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
		mv.addObject("list", listAllService(new Page()));
		mv.addObject("title", TITLE);
		return mv;
	}
	
	public Map<?, ?> listAllService(Page page) {	
		List<Map<String, Object>> list = impl.listAllService(page);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView start() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listStart(new Page(), null));
		mv.addObject("title", TITLE);
		return mv;
	}
	public Map<?, ?> listStart(Page page, String sid) {
		List<Map<String, Object>> list = impl.listStart(page, sid);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView access() {
		ModelAndView mv = new ModelAndView();
		String today = DateUtils.shortToday();
		mv.addObject("list", listAccess(new Page(), today, null));
		mv.addObject("title", TITLE);
		
		return mv;
	}
	public Map<?, ?> listAccess(Page page, String date, String sid) {
		List<Map<String, Object>> list = impl.listAccess(page, date, sid);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView error() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listError(new Page(), null));
		mv.addObject("title", TITLE);
		
		return mv;
	}
	public Map<?, ?> listError(Page page, String sid) {
		List<Map<String, Object>> list = impl.listError(page, sid);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView gather() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listGather(new Page(), null));
		mv.addObject("title", TITLE);
		
		return mv;
	}
	public Map<?, ?> listGather(Page page, String sid) {
		List<Map<String, Object>> list = impl.listGather(page, sid);
		return ControllerReturn.success(list, page);
	}
	
	
}