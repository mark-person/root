package com.ppx.cloud.common.exception.security;

import com.ppx.cloud.common.exception.CustomException;

/**
 * # 权限限制
 * @author mark
 * @date 2018年12月24日
 */
@SuppressWarnings("serial")
public class PermissionUriException extends CustomException {
	
	public PermissionUriException(int errcode, String errmsg) {
		super(errcode, errmsg);
	}
	
}


