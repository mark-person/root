package com.ppx.cloud.demo.config;

import java.util.List;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.ppx.cloud.common.config.CommonMvcConfig;
import com.ppx.cloud.common.util.ApplicationUtils;  



@Configuration
public class DemoMvcConfig extends CommonMvcConfig {
	

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		super.addViewControllers(registry);
	}	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        
        // jar路径的上一层
        ApplicationHome home = new ApplicationHome(DemoMvcConfig.class);
        ApplicationUtils.JAR_PARENT_HOME = home.getSource().getParentFile().getParent() + "/";
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + ApplicationUtils.JAR_PARENT_HOME + "img/")
        	.setCachePeriod(Integer.MAX_VALUE);
    }
	
	
	
	
	
	
	
}  
