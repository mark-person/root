package com.ppx.cloud.common.exception.custom;



/**
 * 配置时异常
 * @author mark
 * @date 2019年1月14日
 */
@SuppressWarnings("serial")
public class ConfigException extends RuntimeException {
	
    public ConfigException(String error) {
        super("Config Exception:" + error);
    }
}


