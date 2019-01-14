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
public class MonitorThresholdConfigExec implements ConfigExec {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean run(String configValue) {
		
		try {
			Map<String, Object> valMap = new ObjectMapper().readValue(configValue, Map.class);
			Integer DUMP_MAX_TIME = (Integer)valMap.get("DUMP_MAX_TIME");
			if (DUMP_MAX_TIME == null) {
				throw new ConfigException("DUMP_MAX_TIME couldn't be null");
			}
			Integer GATHER_INTERVAL = (Integer)valMap.get("GATHER_INTERVAL");
			if (GATHER_INTERVAL == null) {
				throw new ConfigException("GATHER_INTERVAL couldn't be null");
			}
			Double MAX_CPU_DUMP = (Double)valMap.get("MAX_CPU_DUMP");
			if (MAX_CPU_DUMP == null) {
				throw new ConfigException("MAX_CPU_DUMP couldn't be null");
			}
			Double MAX_MEMORY_DUMP = (Double)valMap.get("MAX_MEMORY_DUMP");
			if (MAX_MEMORY_DUMP == null) {
				throw new ConfigException("MAX_MEMORY_DUMP couldn't be null");
			}
			
			MonitorThresholdProperties.setMonitorProperties(DUMP_MAX_TIME, GATHER_INTERVAL, MAX_CPU_DUMP, MAX_MEMORY_DUMP);
		} catch (IOException e) {
			throw new ConfigException(e.getMessage());
		}
		
		
		return true;
	}
}
