package com.ppx.cloud.common.exception.security;

import com.ppx.cloud.common.exception.CustomException;
import com.ppx.cloud.common.exception.ErrorCode;


/**
 * # 二次提交异常
 * @author mark
 * @date 2018年12月25日
 */
@SuppressWarnings("serial")
public class PermissionResubmitException extends CustomException {

	public PermissionResubmitException(String errmsg) {
		super(ErrorCode.PERMISSION_RESUBMIT, "resubmit forbidden");
	}
	
	
}
