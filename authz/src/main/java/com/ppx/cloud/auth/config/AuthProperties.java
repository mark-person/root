/**
 * 
 */
package com.ppx.cloud.auth.config;

/**
 * #由数据库初始化
 * @author mark
 * @date 2018年12月29日
 */
public class AuthProperties {
	
	public static String ADMIN_PASSWORD = AuthUtils.getMD5Password("admin");
	
	public static String JWT_PASSWORD = "JWTPASSWORD";
	
	// # jwt 一天=86400s 说明 1.tocken过期时，将重新验证，合法就产生新的tocken，不合法就跳到login页
	public static int JWT_VALIDATE_SECOND = 86400;
	
	
}
