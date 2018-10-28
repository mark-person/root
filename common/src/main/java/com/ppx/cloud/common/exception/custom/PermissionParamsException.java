package com.ppx.cloud.common.exception.custom;



/**
 * 不允许参数值，预防超权
 * @author dengxz
 * @date 2018年6月14日
 */
@SuppressWarnings("serial")
public class PermissionParamsException extends RuntimeException {
		
	public PermissionParamsException(String msg) {
		super("params forbidden:" + msg);
	}
	
	
}
