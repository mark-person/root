/**
 * 
 */
package com.ppx.cloud.monitor.config;

import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.cloud.config.ConfigExec;

/**
 * @author mark
 * @date 2019年1月14日
 */
public class MonitorSwitchConfigExec implements ConfigExec {
	
	@SuppressWarnings("unchecked")
	@Override
	public String run(String configValue) {
		
		try {
			if (Strings.isEmpty(configValue)) {
				return "configValue couldn't be empty";
			}
			
			Map<String, Object> valMap = new ObjectMapper().readValue(configValue, Map.class);
			Boolean IS_DEBUG = (Boolean)valMap.get("IS_DEBUG");
			if (IS_DEBUG == null) {
				return "IS_DEBUG couldn't be null";
			}
			Boolean IS_WARNING = (Boolean)valMap.get("IS_WARNING");
			if (IS_WARNING == null) {
				return "IS_WARNING couldn't be null";
			}
			
			MonitorSwitchConfig.setMonitorProperties(IS_DEBUG, IS_WARNING);
		} catch (Exception e) {
			return "exception:" + e.getMessage();
		}
		
		return "";
	}
}
