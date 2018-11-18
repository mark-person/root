package com.ppx.cloud.demo.test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.DocResult;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.jdbc.nosql.LogSessionPool;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.common.util.ApplicationUtils;

@Controller
public class TestController {

	@Autowired
	private TestServiceImpl impl;
	
	@Autowired
	private Test2ServiceImpl impl2;

	public ModelAndView test() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list(new Page(), new Test()));
		return mv;
	}
	
	public Map<?, ?> list(Page page, Test pojo) {
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
    
    public Map<?, ?> action() {
    	
    	// String[] n = ApplicationUtils.context.getBeanDefinitionNames();
    	String jarParentHome = ApplicationUtils.getServiceId();
    	
    	return ControllerReturn.success(jarParentHome);
    }
    
    
    
    public void test2(HttpServletResponse response) {
    	
    	
    	System.out.println("---------------------begin------------------");
    	
    	
    	
    	
    	
    	impl2.test();
    
    	
    	
    	
    	
    	
    	
    	
    	
//    	Session mySession = new SessionFactory().getSession("mysqlx://localhost:33060?user=root&password=@Dengppx123456");
//    	long t1 = System.nanoTime();
//    	for (int i = 0; i < 1; i++) {
//    		
//        	
//        	
//    		// Connect to server on localhost
//    		
//        	
//        	
//    		Schema myDb = mySession.getSchema("world_x");
//    		
//    		// Use the collection 'my_collection'
//    		Collection myColl = myDb.getCollection("test");
//    		
//    		
//    		// Specify which document to find with Collection.find() and
//    		// fetch it from the database with .execute()
//    		DocResult myDocs = myColl.find("name = ?").limit(1).bind("abcSabc").execute();
//    		
//    		
//    		
//    		DbDoc doc = myDocs.fetchOne();
//    		
//    		
//    		String out = doc.toString();
//    		System.out.println("out.........:" + out);
//    		
//		}
//    	mySession.close();
//    	System.out.println("..........spendTime:" + (System.nanoTime() - t1)/1000000);
    	
		
		ControllerReturn.returnJson(response, "sss");
    }
	    
}
