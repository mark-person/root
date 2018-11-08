package com.ppx.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

import com.ppx.cloud.common.util.ApplicationUtils;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
    	
    	// spring上下文
        ApplicationUtils.context = SpringApplication.run(DemoApplication.class, args);
        
        // jar路径的上一层
        ApplicationHome home = new ApplicationHome(DemoApplication.class);
        ApplicationUtils.JAR_PARENT_HOME = home.getSource().getParentFile().getParent() + "/";
    }

  
   
}