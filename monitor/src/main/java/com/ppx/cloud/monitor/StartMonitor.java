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

import com.mysql.cj.xdevapi.SqlResult;
import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.monitor.cache.MonitorCache;
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
    	logger.info("StartMonitor----------begin");
    	
    	// 初始化OperatingSystemMXBean对象
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        MonitorUtils.setOperatingSystemMXBean(operatingSystemMXBean);
    	
        Date startTime = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
    	
    	var serviceInfo = getServiceInfo();
    	var startInfo = getStartInfo();
    	try (LogTemplate t = new LogTemplate()) {
    		PersistenceImpl impl = PersistenceImpl.getInstance(t);
    		impl.insertStart(serviceInfo, startInfo, startTime);
    	}
    	

    	
    
    	
    	String cacheSqlMd5 = "select sqlMd5 from stat_sql order by times desc limit 1000";
    	
    	String cacheUrl = "select u.uriSeq, s.uriText,  u.maxTime from stat_uri u join map_uri_seq s on u.uriSeq = s.uriSeq order by u.times desc limit 1000";

    	try (LogTemplate t = new LogTemplate()) {
			String md5Sql = "select sqlMd5 from stat_sql order by times desc limit 1000";
    		SqlResult md5Result = t.sql(md5Sql);
    		md5Result.forEach(r -> {
    			
    			//MonitorCache.addSqlMd5(r.getString("sql_md5"));
    		});
		
    		String uriSql = "select u.uriSeq, s.uriText,from stat_uri u join map_uri_seq s on u.uriSeq = s.uriSeq order by u.times desc limit 1000";
    		SqlResult uriResult = t.sql(uriSql);
    		uriResult.forEach(r -> {
    			MonitorCache.addSeqUri(r.getInt("uriSeq"), r.getString("uriText"));
    		});
    	}
    	
    	
    	
    	
    	
        
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
        machineMap.put("maxActive", Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size")));
        machineMap.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        // java虚拟机可用的处理器个数
        machineMap.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        machineMap.put("modified", new Date());
        machineMap.put("type", "service");
        return machineMap;
    }

    public Map<String, Object> getStartInfo() {
    	// 启动日志     
    	Properties p = System.getProperties();
        Map<String, Object> startMap = new LinkedHashMap<String, Object>();
        startMap.put("profiles", env.getProperty("spring.profiles.active"));
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