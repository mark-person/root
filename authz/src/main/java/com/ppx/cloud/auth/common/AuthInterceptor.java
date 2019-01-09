package com.ppx.cloud.auth.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.filter.AuthFilterUtils;
import com.ppx.cloud.common.context.CommonContext;



/**
 * 权限拦截器
 * @author mark
 * @date 2018年12月19日
 */
public class AuthInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String contextPath = request.getContextPath();
		String uri = request.getRequestURI().replace(contextPath, "");
		
		// 不拦截登录和配置页
		if (uri.startsWith("/auto/login/") || uri.startsWith("/auto/config/")) {
			return true;
		}
		LoginAccount account = AuthFilterUtils.getLoginAccout(request, response, uri);
		if (account == null) {
			// 跳转到登录页面(ajax请求也可以)
			response.sendRedirect(contextPath + "/auto/login/login");
			return false;
		} else {
			AuthContext.setLoginAccount(account);
		}

		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}