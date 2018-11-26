package com.ppx.cloud.monitor;


import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.config.MonitorConfig;
import com.ppx.cloud.monitor.output.PersistenceImpl;
import com.ppx.cloud.monitor.queue.AccessQueueConsumer;
import com.ppx.cloud.monitor.util.MonitorUtils;



/**
 * 开机启动记录日志, 内存和硬盘大小单位默认M, 时间默认ms
 * @author mark
 * @date 2018年6月16日
 */
@Service
public class StartMonitor implements ApplicationListener<ContextRefreshedEvent> {
	
	private static Logger logger = LoggerFactory.getLogger(StartMonitor.class);
	
	@Autowired
    private Environment env;
	
	@Autowired
    private WebApplicationContext context;
    
    @Autowired
    private AccessQueueConsumer accessQueueConsumer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)  {
    	logger.info("StartMonitor----------begin1");
    	
    	
    	
//    	var serviceInfo = getServiceInfo();
//    	var config = getConfig();
//    	var startInfo = getStartInfo();
//    	PersistenceImpl.insertStart(serviceInfo, config, startInfo);

    	PersistenceImpl.createFixedIndex();
    	
/**
TODO 直接使用统计表，并建索引，不需要下面两张表
CREATE TABLE `map_sql_md5` (
  `sql_md5` varchar(32) NOT NULL,
  `sql_text` varchar(2048) DEFAULT NULL,
  `sql_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`sql_md5`),
  KEY `idx_sql_count` (`sql_count`)
)

加上索引 

CREATE TABLE `map_uri_seq` (
  `uri_seq` int(11) NOT NULL,
  `uri_text` varchar(250) NOT NULL,
  `uri_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`uri_seq`)
)
*/
    	
    	
//    	try (LogTemplate t = new LogTemplate()) {
//    		
//    		if (t.existsTable("map_sql_md5")) {
//    			String md5Sql = "select sql_md5 from (select sql_md5 from map_sql_md5 order by sql_count desc) t limit 2";
//        		SqlResult md5Result = t.sql(md5Sql);
//        		md5Result.forEach(r -> {
//        			MonitorCache.addSqlMd5(r.getString("sql_md5"));
//        		});
//    		}
//    		
//    		if (t.existsTable("map_uri_seq")) {
//	    		String uriSql = "select uri_seq, uri_text from (select uri_seq, uri_text from map_uri_seq order by uri_count desc) t limit 2";
//	    		SqlResult uriResult = t.sql(uriSql);
//	    		uriResult.forEach(r -> {
//	    			MonitorCache .addUriSeq(r.getString("uri_text"), r.getInt("uri_seq"));
//	    		});
//    		}
//    	}
//    	
    	
    	
    	
    	
        
        // 启动日志处理队列
    	accessQueueConsumer.start();
    	
    	
    	logger.info("StartMonitor----------end");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private Map<String, Object> getServiceInfo() {
    	// 初始化对象
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        MonitorUtils.setOperatingSystemMXBean(operatingSystemMXBean);
        Properties p = System.getProperties();
        
        // 创建服务信息,服务ID(由机器IP和端口组成) artifactId version osName 物理内存 硬盘大小 
        var machineMap = new LinkedHashMap<String, Object>();
        
        machineMap.put("artifactId", env.getProperty("info.app.artifactId"));
        machineMap.put("version", env.getProperty("info.app.version"));
        machineMap.put("osName", p.getProperty("os.name"));
        machineMap.put("totalPhysicalMemory", MonitorUtils.getTotalPhysicalMemorySize());
        machineMap.put("freePhysicalMemory", MonitorUtils.getFreePhysicalMemorySize());
        machineMap.put("totalSpace", MonitorUtils.getTotalSpace());
        machineMap.put("maxActive", env.getProperty("spring.datasource.hikari.maximum-pool-size"));
        machineMap.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        // java虚拟机可用的处理器个数
        machineMap.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        machineMap.put("modified", new Date());
        machineMap.put("order", -1); // 排序
        machineMap.put("display", 1); // 显示/隐藏
        machineMap.put("type", "service");
        return machineMap;
    }
    
    public Map<String, Object> getConfig() {
    	var confiMap = new LinkedHashMap<String, Object>();
        if (MonitorConfig.IS_DEV) {
        	confiMap.put("isDebug", true);
        	confiMap.put("isWarning", true);
        }
        else {
        	confiMap.put("isDebug", false);
            confiMap.put("isWarning", false);
        }
        confiMap.put("gatherInterval", MonitorConfig.GATHER_INTERVAL);
        confiMap.put("dumpThreadMaxTime", MonitorConfig.DUMP_THREAD_MAX_TIME);
        confiMap.put("created", new Date());
        
        return confiMap;
    }
    
    
    public Map<String, Object> getStartInfo() {
    	// 启动日志     
    	Properties p = System.getProperties();
        Map<String, Object> startMap = new LinkedHashMap<String, Object>();
        startMap.put("sid", ApplicationUtils.getServiceId());
        startMap.put("profiles", env.getProperty("spring.profiles.active"));
        startMap.put("startTime", new Date(ManagementFactory.getRuntimeMXBean().getStartTime()));
        startMap.put("artifactId", env.getProperty("info.app.artifactId"));
        startMap.put("version", env.getProperty("info.app.version"));
        startMap.put("springDatasourceUrl", env.getProperty("spring.datasource.url"));
        startMap.put("maxActive", env.getProperty("spring.datasource.hikari.maximum-pool-size"));
        
        startMap.put("javaHome", p.getProperty("java.home"));
        startMap.put("javaRuntimeVersion", p.getProperty("java.runtime.version"));
        startMap.put("PID", p.getProperty("PID"));      
        startMap.put("beanDefinitionCount", context.getBeanDefinitionCount());
        startMap.put("contextSpendTime", System.currentTimeMillis() - context.getStartupDate());
        startMap.put("jvmSpendTime", System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime());
        
        startMap.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        startMap.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        startMap.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        
        // 服务个数
        RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        startMap.put("handlerMethodsSize", requestMappingHandlerMapping.getHandlerMethods().size());
        return startMap;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}