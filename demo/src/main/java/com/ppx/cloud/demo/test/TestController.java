package com.ppx.cloud.demo.test;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;

@Controller
public class TestController {

	@Autowired
	private TestServiceImpl impl;

	public ModelAndView test() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list(new Page(), new Test()));
		return mv;
	}
	
	public Map<?, ?> list(Page page, Test pojo) {
		int i = 1 / 0;
		return ControllerReturn.success(page, impl.list(page, pojo));
	}
	 
    public Map<?, ?> insert(Test pojo) {
        return ControllerReturn.success(impl.insert(pojo));
    }
    
    public Map<?, ?> get(@RequestParam Integer id) {
        return ControllerReturn.success(impl.get(id));
    }
    
    public Map<?, ?> update(Test pojo) {
        return ControllerReturn.success(impl.update(pojo));
    }
    
    public Map<?, ?> delete(@RequestParam Integer id) {
        return ControllerReturn.success(impl.delete(id));
    }
	    
}
