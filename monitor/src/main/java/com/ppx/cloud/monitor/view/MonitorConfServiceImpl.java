package com.ppx.cloud.monitor.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.monitor.output.PersistenceSupport;

/**
 * 监控设置
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class MonitorConfServiceImpl extends PersistenceSupport {
	
	public List<Map<String, Object>> listConfig() {
		List<Map<String, Object>> returnList  = new ArrayList<Map<String, Object>>();
		try (LogTemplate t = new LogTemplate()) {
			String sql = "select * from conf";
			returnList = queryTable(t, sql, Arrays.asList());
		}
		return returnList;
	}

	public Date setAccessDebug(String serviceId, boolean debug) {
		Date now = new Date();
		return now;
	}

	public Date setAccessWarning(String serviceId, boolean warning) {
		Date now = new Date();
		
		return now;
	}

	
	public void cleanError() {
	}

	public void cleanGather() {
	}

	public void cleanDebug() {
	}

	public void cleanWarning() {
		
	}


	public void orderService(String serviceIds) {
		
	}

	public void display(String serviceId, int display) {
		
	}
}
