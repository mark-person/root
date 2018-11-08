package com.ppx.cloud.monitor.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


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
        new AccessQueueThread().start();
    }
    
    class AccessQueueThread extends Thread {
        public void run() {
            try {
                
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

   
    
   
    
}
