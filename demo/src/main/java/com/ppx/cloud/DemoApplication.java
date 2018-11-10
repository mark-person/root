package com.ppx.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ppx.cloud.common.util.ApplicationUtils;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
    	
    	// spring上下文
        ApplicationUtils.context = SpringApplication.run(DemoApplication.class, args);
        
    }

  
   
}