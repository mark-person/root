package com.ppx.cloud.monitor.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.monitor.config.MonitorConfig;


@Controller
public class MonitorViewController {
	
	@Autowired
	private MonitorViewServiceImpl impl;
	
	private final String TITLE = "实时多机监控平台V0.1.0";
	
	
    public ModelAndView index(ModelAndView mv) {
		mv.addObject("listService", impl.listDisplayService());	
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
		return ReturnMap.of(page, list);
	}
	
	public ModelAndView startup() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listStartup(new Page(), null));
		mv.addObject("listService", impl.listDisplayService());
		return mv;
	}
	public Map<?, ?> listStartup(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listStartup(page, serviceId);
		return ReturnMap.of(page, list);
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
		return ReturnMap.of(page, list);
	}
	public Map<?, ?> getAccess(String accessId) {
		return impl.getAccess(accessId);
	}
	
	public ModelAndView error() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listError(new Page(), null));
		mv.addObject("listService", impl.listDisplayService());
		return mv;
	}
	public Map<?, ?> listError(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listError(page, serviceId);
		return ReturnMap.of(page, list);
	}
	public Map<?, ?> getError(String accessId) {
		return ReturnMap.of("pojo", impl.getDebug(accessId));
	}
	
	public ModelAndView gather() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", listGather(new Page(), null));
		
		return mv;
	}
	public Map<?, ?> listGather(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listGather(page, serviceId);
		return ReturnMap.of(page, list);
	}
	
	
	public ModelAndView statUri(ModelAndView mv) {
		mv.addObject("list", listStatUri(new Page(), null));
		
		return mv;
	}
	public Map<?, ?> listStatUri(Page page, String uri) {
		List<Map<String, Object>> list = impl.listStatUri(page, uri);
		return ReturnMap.of(page, list);
	}

	public ModelAndView statSql(ModelAndView mv) {
		mv.addObject("list", listStatSql(new Page(), null));
		return mv;
	}
	public Map<?, ?> listStatSql(Page page, String sql) {
		List<Map<String, Object>> list = impl.listStatSql(page, sql);
		return ReturnMap.of(page, list);
	}
	
	public ModelAndView statResponse(ModelAndView mv) {
		mv.addObject("list", listStatResponse(new Page(), null));
		return mv;
	}
	public Map<?, ?> listStatResponse(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listStatResponse(page, serviceId);
		return ReturnMap.of(page, list);
	}
	
	public ModelAndView statWarning(ModelAndView mv) {
		mv.addObject("list", listStatWarning(new Page(), null));
		return mv;
	}
	public Map<?, ?> listStatWarning(Page page, String serviceId) {
		List<Map<String, Object>> list = impl.listStatWarning(page, serviceId);
		return ReturnMap.of(page, list);
	}
	
	public ModelAndView debug(ModelAndView mv) {
		mv.addObject("listService", impl.listDisplayService());
		mv.addObject("list", listDebug(new Page(), null, null, null, null, null, null));
		return mv;
	}
	public Map<?, ?> listDebug(Page page, String serviceId, String date, String beginTime, String endTime,
			String uri, String marker) {
		List<Map<String, Object>> list = impl.listDebug(page, serviceId, date, beginTime, endTime, uri, marker);
		return ReturnMap.of(page, list);
	}
	public Map<?, ?> getDebug(String accessId) {
		return ReturnMap.of("pojo", impl.getDebug(accessId));
	}
	
	
	
	
	// >>>>>>>>>>>>>>>>>>>>>设置
	@Autowired
	private MonitorConfServiceImpl confImpl;
	
	private String DELETE_PASSWORD = "ppx";
	
	public ModelAndView set(ModelAndView mv) {
		mv.addObject("isAccessDebug", MonitorConfig.IS_DEBUG);
		mv.addObject("listJson", confImpl.listConfig());		
		return mv;
	}

	public Map<String, Object> setAccessDebug(@RequestParam String serviceId, @RequestParam boolean debug) {
		Date now = confImpl.setAccessDebug(serviceId, debug);
		// 本机立即生效
		if (ApplicationUtils.getServiceId().equals(serviceId)) {
			MonitorConfig.IS_DEBUG = debug;
		}	
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.TIME_PATTERN);
		return ReturnMap.of("now", sdf.format(now));
	}

	public Map<String, Object> setAccessWarning(@RequestParam String serviceId, @RequestParam boolean warning) {
		Date now = confImpl.setAccessWarning(serviceId, warning);
		// 本机立即生效
		if (ApplicationUtils.getServiceId().equals(serviceId)) {
		    MonitorConfig.IS_WARNING = warning;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.TIME_PATTERN);
		return ReturnMap.of("now", sdf.format(now));
	}
	
	public Map<String, Object> orderService(@RequestParam String serviceIds) {
		confImpl.orderService(serviceIds);		
		return ReturnMap.of();
	}	
	
	public Map<String, Object> display(@RequestParam String serviceId, @RequestParam int display) {
		confImpl.display(serviceId, display);	
		return ReturnMap.of("listService", impl.listAllService(new Page()));
	}
	
	
	
}