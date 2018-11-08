package com.ppx.cloud.common.exception.custom;



/**
 * 404异常
 * @author mark
 * @date 2018年11月8日
 */
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {
	
	public NotFoundException() {
		super("Not Found");
	}
}


