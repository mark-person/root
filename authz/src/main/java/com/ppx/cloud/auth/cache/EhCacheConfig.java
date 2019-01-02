package com.ppx.cloud.auth.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;



/**
 * ehcache
 * @author mark
 * @date 2019年1月2日
 */
@Configuration
@EnableCaching
public class EhCacheConfig {
	
	public static final String LOCAL_MANAGER = "LOCAL_MANAGER";
	
	// 账号对应的位图权限
	public static final String ACCOUNT_BIT_SET_CACHE = "ACCOUNT_BIT_SET_CACHE";
	
	// 对应的固定数据，如资源
    public static final String AUTH_FIX_CACHE = "AUTH_FIX_CACHE";
	
	
	@Bean(name=LOCAL_MANAGER)
    public CacheManager cacheManager() {		
		
		net.sf.ehcache.config.Configuration conf = new net.sf.ehcache.config.Configuration();
		CacheConfiguration accountBitSetCache = new CacheConfiguration().name(ACCOUNT_BIT_SET_CACHE);
		accountBitSetCache.maxBytesLocalHeap(10, MemoryUnit.MEGABYTES);
		conf.addCache(accountBitSetCache);
		
		CacheConfiguration authFixCache = new CacheConfiguration().name(AUTH_FIX_CACHE);
		authFixCache.maxEntriesLocalHeap(4);
        conf.addCache(authFixCache);
		
		return new EhCacheCacheManager(new net.sf.ehcache.CacheManager(conf));
    }	
	

	
}
