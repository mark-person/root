package com.ppx.cloud.monitor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;




/**
 * 开机启动记录日志, 内存和硬盘大小单位默认M, 时间默认ms
 * @author mark
 * @date 2018年6月16日
 */
@Service
public class StartMonitor implements ApplicationListener<ContextRefreshedEvent> {
	
	private static Logger logger = LoggerFactory.getLogger(StartMonitor.class);
    
    //@Autowired
    //private AccessQueueConsumer accessQueueConsumer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)  {
    	logger.info("StartMonitor----------begin");
    	
    	
    	
        
        // 启动日志处理队列
    	// accessQueueConsumer.start();
    }
}
