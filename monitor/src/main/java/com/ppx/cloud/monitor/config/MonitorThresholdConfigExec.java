/**
 * 
 */
package com.ppx.cloud.monitor.config;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppx.cloud.config.ConfigExec;

/**
 * @author mark
 * @date 2019年1月14日
 */
public class MonitorThresholdConfigExec implements ConfigExec {
	
	@SuppressWarnings("unchecked")
	@Override
	public String run(String configValue) {
		
		try {
			Map<String, Object> valMap = new ObjectMapper().readValue(configValue, Map.class);
			Integer DUMP_MAX_TIME = (Integer)valMap.get("DUMP_MAX_TIME");
			if (DUMP_MAX_TIME == null) {
				return "DUMP_MAX_TIME couldn't be null";
			}
			Integer GATHER_INTERVAL = (Integer)valMap.get("GATHER_INTERVAL");
			if (GATHER_INTERVAL == null) {
				return "GATHER_INTERVAL couldn't be null";
			}
			Double MAX_CPU_DUMP = (Double)valMap.get("MAX_CPU_DUMP");
			if (MAX_CPU_DUMP == null) {
				return "MAX_CPU_DUMP couldn't be null";
			}
			Double MAX_MEMORY_DUMP = (Double)valMap.get("MAX_MEMORY_DUMP");
			if (MAX_MEMORY_DUMP == null) {
				return "MAX_MEMORY_DUMP couldn't be null";
			}
			
			MonitorThresholdProperties.setMonitorProperties(DUMP_MAX_TIME, GATHER_INTERVAL, MAX_CPU_DUMP, MAX_MEMORY_DUMP);
		} catch (Exception e) {
			return "exception:" + e.getMessage();
		}
		
		
		return "";
	}
}
