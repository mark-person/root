package com.ppx.cloud.common.exception.custom;



/**
 * 初始化配置时异常
 * @author dengxz
 * @date 2018年6月15日
 */
@SuppressWarnings("serial")
public class LoginException extends RuntimeException {
	
    public LoginException(String error) {
        super("Loing Exception:" + error);
    }
}


