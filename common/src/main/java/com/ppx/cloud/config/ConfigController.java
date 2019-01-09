package com.ppx.cloud.config;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ReturnMap;

@Controller
public class ConfigController {

	@Autowired
	private ConfigServiceImpl impl;
	
	public Map<?, ?> test() {
		
		return ReturnMap.of();
	}
	
	// 接收来自操作服务的请求
	public Map<?, ?> sync(@RequestParam String configParam, String configValue) {
		ConfigExec configRun = ConfigUtils.getConfigExec(configParam);
		if (configRun == null) {
			return ReturnMap.of(4001, "configParam:" + configParam + "没有绑定ConfigRun");
		}
		else {
			boolean r = configRun.run(configValue);
			if (r == false) {
				return ReturnMap.of(4002, "configParam:" + configParam + "执行失败");
			}
			impl.updateConfigValue(configParam);
		}
		return ReturnMap.of();
	}
	
}
