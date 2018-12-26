package com.ppx.cloud.common.contoller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.exception.ErrorPojo;
import com.ppx.cloud.common.exception.ErrorUtils;
import com.ppx.cloud.common.exception.custom.IllegalRequestException;


/**
 * #监控拦截器
 * 
 * @author mark
 * @date 2018年11月12日
 */
public class CommonInterceptor implements HandlerInterceptor {
	
	

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	String uri = request.getRequestURI();
    	if (uri.length() > 64) {
    		throw new IllegalRequestException(ErrorCode.URI_OUT_OF_LENGTH, "uri length must less than 64");
    	}
    	// 不支持uri带.的请求，权限不好控制且不好统计
        if (uri.indexOf(".") > 0) {
        	throw new IllegalRequestException(ErrorCode.URI_EXISTS_DOT, "uri not supppot .");
        }
        
    	// org.thymeleaf.exceptions.TemplateInputException
    	Exception errorException = (Exception) request.getAttribute("javax.servlet.error.exception");
    	if (errorException != null && errorException.getMessage().indexOf("org.thymeleaf.exceptions") > -1) {
    		ReturnMap.thymeleafError(response, errorException);
    		return false;
    	}
    	
        // 判断是否为404
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        
        
        
        statusCode = statusCode == null ? 200 : statusCode;
       
        
        if (errorException != null || statusCode != 200) {
            // 当出现404的，把输入的json写入到日志(判断是json请求才写入)
//            if (request.getContentType() != null && request.getContentType().indexOf("application/json") >= 0) {
//                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//                    byte[] buffer = new byte[1024];
//                    int len;
//                    while ((len = request.getInputStream().read(buffer)) > -1) {
//                        baos.write(buffer, 0, len);
//                    }
//                    baos.flush();
//                    System.out.println("..........json:" + baos);
//                }
//            }
            
            String requestUri = (String) request.getAttribute("javax.servlet.forward.request_uri");

            String type = "Not Found";
            Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
            if (exception == null) {
                exception = new IllegalRequestException(ErrorCode.URI_NOT_FOUND, "not found");
            }
            ErrorPojo c = ErrorUtils.getErroCode(exception);
            returnResponse(request, response, c.getErrcode(), c.getErrlevel(), c.getErrmsg());
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
    
    
    private void returnResponse(HttpServletRequest request, HttpServletResponse response, 
    		int errcode, int errlevel, String errmsg) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("text/html") >= 0) {
            ReturnMap.errorHtml(response, errcode, errlevel, errmsg);
            
        } else {
        	ReturnMap.errorJson(response, errcode, errlevel, errmsg);
        }
    }
}
