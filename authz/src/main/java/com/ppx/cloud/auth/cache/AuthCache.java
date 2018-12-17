package com.ppx.cloud.auth.cache;

public class AuthCache {

    public static String AUTH_VERSION = "version";

    private String cacheType;
    private Integer allVersion;
    private Integer grantVersion;
    
    public AuthCache() {}
    
    public AuthCache(Integer allVersion, Integer grantVersion) {
        this.allVersion = allVersion;
        this.grantVersion = grantVersion;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public Integer getAllVersion() {
        return allVersion;
    }

    public void setAllVersion(Integer allVersion) {
        this.allVersion = allVersion;
    }

    public Integer getGrantVersion() {
        return grantVersion;
    }

    public void setGrantVersion(Integer grantVersion) {
        this.grantVersion = grantVersion;
    }

}
