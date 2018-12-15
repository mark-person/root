package com.ppx.cloud.auth;



import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

// @Service
public class StartAuth implements ApplicationListener<ContextRefreshedEvent> {
    
//    @Autowired
//    private EhCacheService ehCacheServ;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)  {
//        AuthCache authCache = ehCacheServ.initAuthVersion();
//        AuthFilterUtils.localAuthAllVersion = authCache.getAllVersion();
//        AuthFilterUtils.localAuthGrantVersion = authCache.getGrantVersion();
    }
}
