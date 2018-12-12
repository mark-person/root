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
		
		List<Map<String, Object>> serviceList = impl.listDisplayService();
		
		mv.addObject("listMachine", serviceList);	
		mv.addObject("currentServiceId", ApplicationUtils.getServiceId());
		mv.addObject("title", TITLE);
		return mv;
	}
	
    public ModelAndView service() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listAllService(new Page()));
		return mv;
	}
	
	public Map<?, ?> listAllService(Page page) {	
		List<Map<String, Object>> list = impl.listAllService(page);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView startup() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listStartup(new Page(), null));
		return mv;
	}
	public Map<?, ?> listStartup(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listStartup(page, serviceId);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView access() {
		ModelAndView mv = new ModelAndView();
		String today = DateUtils.today();
		mv.addObject("today", today);
		mv.addObject("list", listAccess(new Page(), today, null, null, null, null));
		
		mv.addObject("listService", impl.listDisplayService());
		
		return mv;
	}
	public Map<?, ?> listAccess(Page page, String date, String beginTime, String endTime, String serviceId, String uriText) {
		List<Map<String, Object>> list = impl.listAccess(page, date, beginTime, endTime, serviceId, uriText);
		return ControllerReturn.success(list, page);
	}
	public Map<?, ?> getAccess(String accessId) {
		return impl.getAccess(accessId);
	}
	
	public ModelAndView error() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listError(new Page(), null));
		
		return mv;
	}
	public Map<?, ?> listError(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listError(page, serviceId);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView gather() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listGather(new Page(), null));
		
		return mv;
	}
	public Map<?, ?> listGather(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listGather(page, serviceId);
		return ControllerReturn.success(list, page);
	}
	
	
	public ModelAndView statUri(ModelAndView mv) {
		mv.addObject("list", listStatUri(new Page(), null));
		
		return mv;
	}
	public Map<?, ?> listStatUri(Page page, String uri) {
		List<Map<String, Object>> list = impl.listStatUri(page, uri);
		return ControllerReturn.success(list, page);
	}

	public ModelAndView statSql(ModelAndView mv) {
		mv.addObject("list", listStatSql(new Page(), null));
		return mv;
	}
	public Map<?, ?> listStatSql(Page page, String sql) {
		List<Map<String, Object>> list = impl.listStatSql(page, sql);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView statResponse(ModelAndView mv) {
		mv.addObject("list", listStatResponse(new Page(), null));
		return mv;
	}
	public Map<?, ?> listStatResponse(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listStatResponse(page, serviceId);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView statWarning(ModelAndView mv) {
		mv.addObject("list", listStatWarning(new Page(), null));
		return mv;
	}
	public Map<?, ?> listStatWarning(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listStatWarning(page, serviceId);
		return ControllerReturn.success(list, page);
	}
	
	public ModelAndView debug(ModelAndView mv) {
		mv.addObject("list", listDebug(new Page(), null));
		return mv;
	}
	public Map<?, ?> listDebug(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listDebug(page, serviceId);
		return ControllerReturn.success(list, page);
	}
}