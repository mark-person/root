package com.ppx.cloud.common.exception.custom;


/**
 * 二次提交异常
 * @author dengxz
 * @date 2018年6月14日
 */
@SuppressWarnings("serial")
public class PermissionResubmitException extends RuntimeException {

	public PermissionResubmitException() {
		super("resubmit forbidden");
	}
	
	
}
