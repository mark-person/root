package com.ppx.cloud.auth.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;




/**
 * 权限拦截器
 * @author mark
 * @date 2018年6月19日
 */
public class AuthInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String contextPath = request.getContextPath();
		String uri = request.getRequestURI().replace(contextPath, "");
		
		// 不拦截登录页
		if (uri.startsWith("/login/") || uri.equals("/")) {
			return true;
		}
		
//		LoginAccount account = AuthFilterUtils.getLoginAccout(request, response, uri);
//		if (account == null) {
//			// 跳转到登录页面(ajax请求也可以)
//			response.sendRedirect(contextPath + "/login/login");
//			return false;
//		} else {
//		    // 为每个请求都加上accountId	
//		    // CommonContext.setAccountId(account.getAccountId());
//			AuthContext.setLoginAccount(account);
//		}

		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}