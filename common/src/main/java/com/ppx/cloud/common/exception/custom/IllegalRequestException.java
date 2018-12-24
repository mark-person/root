package com.ppx.cloud.common.exception.custom;

import com.ppx.cloud.common.exception.CustomException;

/**
 * 404异常
 * @author mark
 * @date 2018年11月8日
 */
@SuppressWarnings("serial")
public class IllegalRequestException extends CustomException {
	
	public IllegalRequestException(int errcode, String errmsg) {
		super(errcode, errmsg);
	}
	
}


