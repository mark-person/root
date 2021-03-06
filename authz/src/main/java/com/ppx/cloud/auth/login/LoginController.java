package com.ppx.cloud.auth.login;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ppx.cloud.auth.cache.AuthCache;
import com.ppx.cloud.auth.cache.EhCacheService;
import com.ppx.cloud.auth.config.AuthUtils;
import com.ppx.cloud.auth.pojo.AuthAccount;
import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.exception.custom.LoginException;
import com.ppx.cloud.common.util.CookieUtils;

/**
 * 登录
 * 
 * @author mark
 * @date 2018年12月19日
 */
@Controller
public class LoginController {

	@Autowired
	private LoginServiceImpl impl;

	@Autowired
	private EhCacheService ehCacheServ;

	private final static String VALIDATE_TOKEN_PASSWORK = "FSSBBA";
	private final static String VALIDATE_TOKEN_NAME = "FSSBBIl1UgzbN7N443T";
	private final static String VALIDATE_JS_PASSWORK = "DhefwqGPrzGxEp9hPaoag";

	public ModelAndView login(ModelAndView mv, HttpServletResponse response) throws Exception {
		createValidateToken(mv, response);
		// 清缓存，保证validateToken最新
		response.setHeader("Content-type", "text/html; charset=utf-8");
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		
		response.flushBuffer();
		return mv;
	}

	public void loginout(HttpServletResponse response) throws Exception {
		// 清空登录cookie
		CookieUtils.cleanCookie(response, AuthUtils.PPXTOKEN);
		response.sendRedirect("/auto/login/login");
	}

	public Map<?, ?> doLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam String a,
			@RequestParam String p, @RequestParam String v) {

		// 验证"验证token",不合法就返回403,预防容易直接请求爆力破解密码
		DecodedJWT jwt = null;
		try {
			Map<String, String> cookieMap = CookieUtils.getCookieMap(request);
			Algorithm algorithm = Algorithm.HMAC256(VALIDATE_TOKEN_PASSWORK);
			JWTVerifier verifier = JWT.require(algorithm).build();
			jwt = verifier.verify(cookieMap.get(VALIDATE_TOKEN_NAME));
		} catch (Exception e) {
			throw new LoginException("验证token异常" + e.getClass().getSimpleName());
		}
		String cookieV = jwt.getClaim("v").asString();
		cookieV = cookieV.substring(cookieV.length() - 21);
		StringBuilder r = new StringBuilder();
		for (int i = 0; i < cookieV.length(); i++) {
			r.append(cookieV.charAt(i) + VALIDATE_JS_PASSWORK.charAt(i));
		}
		if (!v.equals(r.toString())) {
			response.setStatus(403);
			return null;
		}

		AuthAccount account = impl.getLoginAccount(a, p);
		if (account == null) {
			return ReturnMap.of(4001, "用户名或密码有误");
		} else if (account.getAccountStatus() != AuthUtils.ACCOUNT_STATUS_EFFECTIVE
				|| account.getUserAccountStatus() != AuthUtils.ACCOUNT_STATUS_EFFECTIVE) {
			return ReturnMap.of(4002, "账号异常");
		} else {
			// 帐号和密码正确，则在cookie上生成一个token, grantAll, grantAuth
			String token = "";
			try {
				// 取得权限缓存，支持分布式(allVersion和grantVersion跟服务器版本比较）
				AuthCache authCache = ehCacheServ.getAuthVersion();
					
				// modified用来校验帐号或密码被修改
				Algorithm algorithm = Algorithm.HMAC256(AuthUtils.getJwtPassword());
				token = JWT.create().withIssuedAt(new Date()).withClaim("accountId", account.getAccountId())
						.withClaim("loginAccount", account.getLoginAccount()).withClaim("userId", account.getUserId())
						.withClaim("userName", account.getUserName()).withClaim("modified", account.getModified())
						.withClaim("authAll", authCache.getAllVersion())
						.withClaim("authGrant", authCache.getGrantVersion())
						.sign(algorithm);
				CookieUtils.setCookie(response, AuthUtils.PPXTOKEN, token);

				impl.updateLastLogin(account.getAccountId());
				return ReturnMap.of();
			} catch (Exception e) {
				throw new LoginException("系统登录异常" + e.getClass().getSimpleName());
			}
		}
	}

	private void createValidateToken(ModelAndView mv, HttpServletResponse response) throws Exception {
		// 产生验证token到页面和cookie，以便验证合法性，支持分布式
		String v = UUID.randomUUID().toString();
		mv.addObject("v", v);
		Algorithm algorithm = Algorithm.HMAC256(VALIDATE_TOKEN_PASSWORK);
		String token = JWT.create().withClaim("v", v).sign(algorithm);
		CookieUtils.setCookie(response, VALIDATE_TOKEN_NAME, token);
	}
}
