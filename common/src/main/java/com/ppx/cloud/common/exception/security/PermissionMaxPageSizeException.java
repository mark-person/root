package com.ppx.cloud.common.exception.security;


/**
 * 分页参数超出范围异常
 * @author mark
 * @date 2018年11月8日
 */
@SuppressWarnings("serial")
public class PermissionMaxPageSizeException extends RuntimeException {
	
	public PermissionMaxPageSizeException(String error) {
		super("maxPageSize forbidden:" + error);
	}
	
	
}
