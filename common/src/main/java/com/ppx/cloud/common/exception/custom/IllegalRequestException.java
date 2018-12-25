package com.ppx.cloud.common.exception.custom;

import com.ppx.cloud.common.exception.CustomException;

/**
 * 请求异常，404等异常
 * @author mark
 * @date 2018年11月8日
 */
@SuppressWarnings("serial")
public class IllegalRequestException extends CustomException {
	
	public IllegalRequestException(int errcode, String errmsg) {
		super(errcode, errmsg);
	}
	
}


