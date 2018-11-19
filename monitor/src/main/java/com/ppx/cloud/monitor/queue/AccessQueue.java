package com.ppx.cloud.monitor.queue;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppx.cloud.monitor.StartMonitor;
import com.ppx.cloud.monitor.output.ConsoleImpl;
import com.ppx.cloud.monitor.pojo.AccessLog;




/**
 * 日志队列
 * @author mark
 * @date 2018年11月19日
 */
public class AccessQueue {
    
    private final static int QUEUE_MAX_SIZE = 20000;
    
    private static Queue<AccessLog> queue = new LinkedList<AccessLog>();
    
    private static Logger logger = LoggerFactory.getLogger(StartMonitor.class);
    
    public synchronized static boolean offer(AccessLog a) {
        if (queue.size() > QUEUE_MAX_SIZE) {
        	logger.warn("Warning AccessQueue over:" + QUEUE_MAX_SIZE);
            // 进入不了队列，调用ConsoleService输出到控制台
            ConsoleImpl.print(a);
            return false;
        }
        return queue.offer(a);
    }
    
    public static Queue<AccessLog> getQueue() {
        return queue;
    }
    
    public static int getQueueSize() {
        return queue.size();
    }
    
    
    
}