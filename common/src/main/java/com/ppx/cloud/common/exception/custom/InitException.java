package com.ppx.cloud.common.exception.custom;



/**
 * 初始化配置时异常
 * @author dengxz
 * @date 2018年6月15日
 */
@SuppressWarnings("serial")
public class InitException extends RuntimeException {
	
    public InitException(String error) {
        super("Init Exception:" + error);
    }
}


