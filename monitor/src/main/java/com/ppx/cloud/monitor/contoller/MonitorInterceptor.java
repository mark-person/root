package com.ppx.cloud.monitor.contoller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.threads.TaskThread;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.monitor.pojo.AccessLog;
import com.ppx.cloud.monitor.queue.AccessQueue;

/**
 * 监控拦截器
 * @author mark
 * @date 2018年7月2日
 */
public class MonitorInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	
    	AccessLog accessLog = AccessLog.getInstance(request);
    	TaskThread.setAccessLog(accessLog);
    	
        return true;
    }
    
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    	AccessLog accessLog = TaskThread.getAccessLog();
        accessLog.setSpendNanoTime((System.nanoTime() - accessLog.getBeginNanoTime()));
        AccessQueue.offer(accessLog);
    }
  
}
