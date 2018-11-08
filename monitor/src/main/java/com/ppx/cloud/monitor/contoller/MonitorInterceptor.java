package com.ppx.cloud.monitor.contoller;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.exception.ErrorBean;
import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.exception.custom.NotFoundException;

/**
 * 监控拦截器
 * @author mark
 * @date 2018年7月2日
 */
public class MonitorInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        

        // 判断是否为404或模板错误
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        
        statusCode = statusCode == null ? 0 : statusCode;
        // 不支持uri带.的请求，权限不好控制且不好统计
        statusCode = request.getRequestURI().indexOf(".") > 0 ? 404 : statusCode;
        
        if (statusCode != 200) {
            // 当出现404的，把输入的json写入到日志(判断是json请求才写入)
            if (request.getContentType() != null && request.getContentType().indexOf("application/json") >= 0) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = request.getInputStream().read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();

                }
            }
            
            String requestUri = (String) request.getAttribute("javax.servlet.forward.request_uri");



            String type = "Not Found";
            Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
            if (exception == null) {
                exception = new NotFoundException();
            } else {
                ErrorBean c = ErrorCode.getErroCode(exception);
                type = c.getInfo();
            }
            
            returnWebResponse(request, response, statusCode, type + ":" + requestUri);


            return false;
        }
        
        return true;
    }
    
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
    
    
    private void returnWebResponse(HttpServletRequest request, HttpServletResponse response, 
            Integer statusCode, String msg) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("text/html") >= 0) {
            ControllerReturn.returnErrorHtml(response, statusCode, msg);
        } else {
            ControllerReturn.returnErrorJson(response, statusCode, msg);
        }
    }
}
