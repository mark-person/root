package com.ppx.cloud.auth.config;

import com.ppx.cloud.common.util.MD5Utils;

/**
 * # 权限工具类
 * @author mark
 * @date 2018年7月2日
 */
public class AuthUtils {
	
	public static final String PPXTOKEN = "BASETOKEN";
	
	public static final String ADMIN_ACCOUNT = "admin";
	
	public static final int ACCOUNT_STATUS_EFFECTIVE = 1;
	
	public static final int ACCOUNT_STATUS_INEFFECTIVE = 2;
	
	public static String getJwtPassword() {
		return AuthProperties.JWT_PASSWORD + "PASS";
	}
	
	public static String getMD5Password(String p) {
		return MD5Utils.getMD5(p + "PASS");
	}
}
