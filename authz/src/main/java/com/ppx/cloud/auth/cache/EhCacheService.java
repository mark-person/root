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
    
    Map<String, List<Map<String, Object>>> loadMenuResourceUri();
    
    Map<Integer, List<Integer>> loadResouceUri();
    
    List<Map<String, Object>> loadResource();
    
}
