/**
 * 
 */
package com.ppx.cloud.common.contoller;

import java.util.List;
import java.util.Map;

import com.ppx.cloud.common.page.Page;

/**
 * errcode errmsg
 * 0:成功 -1:系统忙(500错误)
 * 4000:存在 400x其它业务逻辑
 * 403?:权限
 * 404?: 4040 no found 参数 uri长度、不合法等
 * 
 * 
 * RetureMap.of(...)
 * errcode不等于0时, errmsg必须有值
 * 
 * 程序尽量不要只返回int，会产生魔法数字
 * 
 * @author mark
 * @date 2018年12月24日
 */
public class ReturnMap {
	
	// 4000存在,400?业务逻辑异常
	private static final int EXISTS_CODE = 4000;
		
	private static final String ERRCODE_TITLE = "errcode";
	
	private static final String ERRMSG_TITLE = "errmsg";
	
	// 0:成功 -1:系统忙(500错误) 4000:存在 400x其它业务逻辑；403?:权限；404?: 4040 no found 参数 uri长度、不合法等
	private static final int ERRCODE_SUCCESS = 0;
	
	private static final int ERRCODE_ERROR = -1;
	
	public static final Map<String, Object> SUCCESS = Map.of(ERRMSG_TITLE, ERRCODE_SUCCESS);
	
	public static final Map<String, Object> ERROR = Map.of(ERRMSG_TITLE, ERRCODE_ERROR, ERRMSG_TITLE, "ERROR");
	
	public static Map<String, Object> of() {
		return SUCCESS;
	}
	
	/**
	 * 只收1:成功，0:存在,返回EXISTS_CODE 4000
	 * @param val
	 * @return
	 */
	public static Map<String, Object> of(int val) {
		if (val == 0) {
			return Map.of(ERRCODE_TITLE, EXISTS_CODE, ERRMSG_TITLE, "EXISTS");
		}
		else if (val == 1) {
			return Map.of(ERRCODE_TITLE, 0);
		}
		else {
			throw new RuntimeException("only access value 0 or 1, current value:" + val);
		}
	}
	// 4001~4009
	public static Map<String, Object> of(int errcode, String errmsg) {
		return Map.of(ERRCODE_TITLE, errcode, ERRCODE_TITLE, errmsg);
	}
	
	public static Map<String, Object> of(Page page, List<?> list) {
		return Map.of(ERRCODE_TITLE, ERRCODE_SUCCESS, "page", page, "list", list);
	}
	
	public static Map<String, Object> of(Page page, List<?> list, String k, Object v) {
		return Map.of(ERRCODE_TITLE, ERRCODE_SUCCESS, "page", page, "list", list, k, v);
	}
	
	public static Map<String, Object> of(String k, Object v) {
		return Map.of(ERRCODE_TITLE, ERRCODE_SUCCESS, k, v);
	}
	
	public static Map<String, Object> of(String k1, Object v1, String k2, Object v2) {
		return Map.of(ERRCODE_TITLE, ERRCODE_SUCCESS, k1, v1, k2, v2);
	}
	
	public static Map<String, Object> of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
		return Map.of(ERRCODE_TITLE, ERRCODE_SUCCESS, k1, v1, k2, v2, k3, v3);
	}
	
	
	// >>>>>>>>>>>>>>>>>>>>>>>error
	public static Map<String, Object> error() {
		return ERROR;
	}
	
	public static Map<String, Object> error(int errcode, String errmsg) {
		// 0:成功 -1:系统忙(500错误) 4000:存在 400x业务逻辑；403?:权限；404?: 4040 no found 参数 uri长度、不合法等
		return Map.of(ERRCODE_TITLE, errcode, ERRCODE_TITLE, errmsg);
	}
	
	
		
}
