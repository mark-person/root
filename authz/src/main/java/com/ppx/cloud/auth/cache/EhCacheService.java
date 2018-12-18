package com.ppx.cloud.auth.cache;

import java.util.List;
import java.util.Map;

public interface EhCacheService {
    
    AuthCache getAuthVersion();
    
    AuthCache initAuthVersion();
    
    void increaseAllDbVersion();
    
    void increaseGrantDbVersion();
    
    void clearAllLocalCache();
    
    void clearAuthLocalCache();
    
    
    Map<String, Integer> loadUriIndex();
    
    @SuppressWarnings("rawtypes")
    Map<String, List<Map>> loadMenuResourceUri();
    
    @SuppressWarnings("rawtypes")
    Map<Integer, Map> loadResouceUri();
    
    List<Map<String, Object>> loadResource();
    
}
