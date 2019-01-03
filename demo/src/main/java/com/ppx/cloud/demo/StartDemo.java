package com.ppx.cloud.demo;



import java.util.Map;

import org.apache.tomcat.util.threads.TaskThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.ppx.cloud.auth.config.AuthProperties;
import com.ppx.cloud.demo.config.start.StartDemoServ;
import com.ppx.cloud.monitor.config.MonitorProperties;
import com.ppx.cloud.monitor.pojo.AccessLog;

/**
 * # 启动设置
 * @author mark
 * @date 2019年1月3日
 */
@Service
public class StartDemo implements ApplicationListener<ContextRefreshedEvent> {
    
    @Autowired
    private StartDemoServ startDemoServ;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)  {
    	
    	
    	
//    	Map<String, Object> configMap = startDemoServ.getConfig();
//    	
//    	// 权限配置参数初始化
//    	String ADMIN_PASSWORD = (String)configMap.get("ADMIN_PASSWORD");
//    	String JWT_PASSWORD = (String)configMap.get("JWT_PASSWORD");
//    	int JWT_VALIDATE_SECOND = (Integer)configMap.get("JWT_VALIDATE_SECOND");
//    	AuthProperties.setAuthPoperties(ADMIN_PASSWORD, JWT_PASSWORD, JWT_VALIDATE_SECOND);
//    	
//    	// 监控配置参数
//    	Integer DUMP_MAX_TIME = (Integer)configMap.get("DUMP_MAX_TIME");
//    	Integer GATHER_INTERVAL = (Integer)configMap.get("GATHER_INTERVAL");
//    	Integer SYNC_CONF_INTERVAL = (Integer)configMap.get("SYNC_CONF_INTERVAL");
//    	Double MAX_CPU_DUMP = (Double)configMap.get("MAX_CPU_DUMP");
//    	Double MAX_MEMORY_DUMP = (Double)configMap.get("MAX_MEMORY_DUMP");
//    	MonitorProperties.setMonitorProperties(DUMP_MAX_TIME, GATHER_INTERVAL, SYNC_CONF_INTERVAL, MAX_CPU_DUMP, MAX_MEMORY_DUMP);
    	
    }
}
