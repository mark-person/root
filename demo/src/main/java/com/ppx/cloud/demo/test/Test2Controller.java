package com.ppx.cloud.demo.test;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.threads.TaskThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.monitor.pojo.AccessLog;

@Controller
public class Test2Controller {

	
	@Autowired
	private Test2ServiceImpl impl2;

	
	public ModelAndView testJson(ModelAndView mv) {
		return mv;
	}
	
	
	public Map<?, ?> getJson(@RequestBody Test test) {
		System.out.println("...........a:" + test.getTestId());
		return ControllerReturn.SUCCESS;
	}
    
    public Map<?, ?> test2() {
    	impl2.test();
		return ControllerReturn.SUCCESS;
		
    }
	    
}
