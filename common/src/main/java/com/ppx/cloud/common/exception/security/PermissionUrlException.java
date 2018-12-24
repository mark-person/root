package com.ppx.cloud.common.exception.security;

import com.ppx.cloud.common.exception.CustomException;

/**
 * 非法URL异常
 * uri长度>64 uri带有. 
 * @author mark
 * @date 2018年12月24日
 */
@SuppressWarnings("serial")
public class PermissionUrlException extends CustomException {
	public PermissionUrlException(int errcode, String errmsg) {
		super(errcode, errmsg);
	}
}


