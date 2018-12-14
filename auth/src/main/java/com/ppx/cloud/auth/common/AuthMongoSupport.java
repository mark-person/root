package com.ppx.cloud.auth.common;

import com.ppx.cloud.common.jdbc.MyDaoSupport;

/**
 * 权限mongodb支持
 * @author mark
 * @date 2018年7月2日
 */
public class AuthMongoSupport extends MyDaoSupport {
    
    protected final String COL_URI_INDEX = "grant_uri_index";
    protected final String COL_RESOURCE = "grant_resource";
    protected final String COL_RESOURCE_URI = "grant_resource_uri";
    protected final String COL_SEQUENCE = "grant_sequence";
    
}
