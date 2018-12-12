package com.ppx.cloud.monitor.exception;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.threads.TaskThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.exception.CustomExceptionHandler;
import com.ppx.cloud.monitor.pojo.AccessLog;
import com.ppx.cloud.monitor.util.MonitorUtils;

/**
 * 监控异常处理
 * @author mark
 * @date 2018年6月25日
 */
@ControllerAdvice
public class MonitorExceptionHandler extends CustomExceptionHandler {
	
	private static Logger logger = LoggerFactory.getLogger(MonitorExceptionHandler.class);
    
    private AtomicInteger sequence = new AtomicInteger(0);
    
    @Override
    @ExceptionHandler(value = Throwable.class)
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object,
            Exception exception) {
        // 100秒+系列号，在页面中显示和数据库中存储
        String marker = System.currentTimeMillis() / 100000 + "-" + sequence.getAndIncrement();
        // 传参marker到CustomExceptionHandler
        request.setAttribute("marker", marker);
        super.resolveException(request, response, object, exception);
        
        AccessLog accessLog = TaskThread.getAccessLog();
        if (accessLog == null) {
            return null;
        }
        // accessLog.addMarker(marker);
        logger.error(MarkerFactory.getMarker(marker), "ERROR");
        
        if (StringUtils.isEmpty(accessLog.getParams())) {
            // 所有异常 包括ErrorCode.IGNORE_ERROR，都要打印输入参数
            accessLog.setParams(MonitorUtils.getParams(request));
        }
        
        accessLog.setThrowable(exception);
        return null;
    }

}
