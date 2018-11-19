package com.ppx.cloud.monitor.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ppx.cloud.monitor.config.MonitorConfig;
import com.ppx.cloud.monitor.output.ConsoleImpl;
import com.ppx.cloud.monitor.pojo.AccessLog;


/**
 * 队列消费者,用于日志输出
 * @author mark
 * @date 2018年6月16日
 */
@Component
public class AccessQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AccessQueueConsumer.class);
  
    
    public void start() {
        // 启动后调用
        logger.info("AccessQueueConsumer.start()>>>>>>>>>>>>>>>>");
        
        new Thread(() ->  {
        	// 加上try内防止线程死掉
        	try {
        		while (true) {
        			consumeAccessLog();
        			Thread.sleep(20);
        		}
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void consumeAccessLog() {
        AccessLog a;
        while ((a = AccessQueue.getQueue().poll()) != null) {
            if (MonitorConfig.IS_DEV) {
                ConsoleImpl.print(a);
            }
            
            // 监控页面的查看不输出(异常时输出)
            if (a.getUri().indexOf("/monitorView/") < 0 || a.getThrowable() != null) {
                try {
                    //logToMongodb(a);
                } catch (Throwable e) {
                    e.printStackTrace();
                    // 输出mongodb异常，则打印到控制台
                    System.err.println("Error(logToMongodb):" + e.getMessage());
                    if (!MonitorConfig.IS_DEV) {
                        //ConsoleServiceImpl.print(a);
                    }
                }
            }
        }
    }
    

   
    
   
    
}
