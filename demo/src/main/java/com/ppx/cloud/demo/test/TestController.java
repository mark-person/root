package com.ppx.cloud.demo.test;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.page.Page;

@Controller
public class TestController {

	@Autowired
	private TestServiceImpl impl;

	public ModelAndView test(ModelAndView mv) {
		mv.addObject("list", list(new Page(), new Test()));
		return mv;
	}
	
	public Map<?, ?> list(Page page, Test pojo) {
		return ReturnMap.of(page, impl.list(page, pojo));
	}
	 
    public Map<?, ?> insert(Test pojo) {
        return impl.insert(pojo);
    }
    
    
    public Map<?, ?> get(@RequestParam Integer id) {
        return ReturnMap.of("pojo", impl.get(id));
    }
    
    public Map<?, ?> update(Test pojo) {
        return impl.update(pojo);
    }
    
    public Map<?, ?> delete(@RequestParam Integer id) {
        return impl.delete(id);
    }

	
}
