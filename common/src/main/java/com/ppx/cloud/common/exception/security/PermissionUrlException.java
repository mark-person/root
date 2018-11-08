package com.ppx.cloud.common.exception.security;


/**
 * 非法URL异常
 * @author mark
 * @date 2018年11月8日
 */
@SuppressWarnings("serial")
public class PermissionUrlException extends RuntimeException {
	
	public PermissionUrlException() {
		super("url illegal");
	}
	
	public PermissionUrlException(String msg) {
		super(msg);
	}
}


