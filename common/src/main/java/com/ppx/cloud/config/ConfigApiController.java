package com.ppx.cloud.config;

import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import com.ppx.cloud.common.contoller.ReturnMap;

@Controller
public class ConfigApiController {

	@Autowired
	private ConfigApiServImpl impl;
	
	public Map<?, ?> test() {
		
		return ReturnMap.of();
	}
	
	// 接收来自操作服务的请求
	public Map<?, ?> sync(@RequestParam String configName, String configValue) {
		ConfigExec configRun = ConfigUtils.getConfigExec(configName);
		if (configRun == null) {
			return ReturnMap.of(4001, "configParam:" + configName + "没有绑定ConfigRun");
		}
		else {
			String r = configRun.run(configValue);
			if (Strings.isNotEmpty(r)) {
				return ReturnMap.of(4002, "configParam:" + configName + "执行失败，原因:" + r);
			}
			impl.updateConfigValue(configName);
		}
		return ReturnMap.of();
	}
	
}
