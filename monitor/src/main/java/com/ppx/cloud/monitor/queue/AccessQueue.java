package com.ppx.cloud.monitor.queue;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import com.ppx.cloud.monitor.output.ConsoleImpl;
import com.ppx.cloud.monitor.pojo.AccessLog;



/**
 * 日志队列
 * @author mark
 * @date 2018年7月2日
 */
public class AccessQueue {
    
    private final static int QUEUE_MAX_SIZE = 20000;
    
    private static Queue<AccessLog> queue = new LinkedList<AccessLog>(); 
    
    public synchronized static boolean offer(AccessLog a) {
        if (queue.size() > QUEUE_MAX_SIZE) {
            System.out.println("Warning AccessQueue over:" + QUEUE_MAX_SIZE + ":" + new Date());
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