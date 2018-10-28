package com.ppx.cloud.common.exception.custom;


/**
 * 
 * @author Administrator
 *
 */



/**
 * 分页参数超出范围异常
 * @author dengxz
 * @date 2018年6月14日
 */
@SuppressWarnings("serial")
public class PermissionMaxPageSizeException extends RuntimeException {
	
	public PermissionMaxPageSizeException(String error) {
		super("maxPageSize forbidden:" + error);
	}
	
	
}
