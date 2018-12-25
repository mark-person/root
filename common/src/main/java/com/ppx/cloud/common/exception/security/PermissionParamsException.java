package com.ppx.cloud.common.exception.security;

import com.ppx.cloud.common.exception.CustomException;

/**
 * 不允许参数值，预防超权
 * @author dengxz
 * @date 2018年6月14日
 */
@SuppressWarnings("serial")
public class PermissionParamsException extends CustomException {
		
	public PermissionParamsException(String errmsg) {
		super(CustomException.PERMISSION_PARAMS, errmsg);
	}
	
}
