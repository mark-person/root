package com.ppx.cloud.demo.test;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.threads.TaskThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.pojo.AccessLog;

@Controller
public class ValidateController {
	
    @RequestMapping("/.well-known/pki-validation/fileauth.txt")
    public ModelAndView valiate() {
    	System.out.println("999");
    	
    	ModelAndView mv = new ModelAndView("fileauth.txt");
    	
		return mv;
	}
	    
}
