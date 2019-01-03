package com.ppx.cloud.monitor.view;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.ppx.cloud.monitor.output.PersistenceSupport;

/**
 * 监控设置
 * @author mark
 * @date 2018年7月2日
 */
@Service
public class MonitorConfServiceImpl extends PersistenceSupport {

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
