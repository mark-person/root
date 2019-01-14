/**
 * 
 */
package com.ppx.cloud.monitor.config;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.cloud.common.exception.custom.ConfigException;
import com.ppx.cloud.config.ConfigExec;

/**
 * @author mark
 * @date 2019年1月14日
 */
public class MonitorSwitchConfigExec implements ConfigExec {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean run(String configValue) {
		
		try {
			if (Strings.isEmpty(configValue)) {
				throw new ConfigException("configValue couldn't be empty");
			}
			
			Map<String, Object> valMap = new ObjectMapper().readValue(configValue, Map.class);
			Boolean IS_DEBUG = (Boolean)valMap.get("IS_DEBUG");
			if (IS_DEBUG == null) {
				throw new ConfigException("IS_DEBUG couldn't be null");
			}
			Boolean IS_WARNING = (Boolean)valMap.get("IS_WARNING");
			if (IS_WARNING == null) {
				throw new ConfigException("IS_WARNING couldn't be null");
			}
			
			MonitorSwitchConfig.setMonitorProperties(IS_DEBUG, IS_WARNING);
		} catch (IOException e) {
			throw new ConfigException(e.getMessage());
		}
		
		
		return true;
	}
}
