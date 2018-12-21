package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.util.ApplicationUtils;


/**
 * 资源管理
 * @author mark
 * @date 2018年7月2日
 */
@Controller
public class ResController {
	
	@Autowired
	private ResServiceImpl impl;
	
	
    public ModelAndView res(ModelAndView mv) {
		mv.addObject("res", getResource());
		return mv;
	}
	
	public Map<Object, Object> getResource() {
		Map<String, Object> map = impl.getResource();
		if (map == null) {
			return ControllerReturn.success(-1);
		}
		return ControllerReturn.success(map);
	}
	
	public Map<?, ?> getResUri() {
		List<String> menuList = new ArrayList<String>();
		
        RequestMappingHandlerMapping r = ApplicationUtils.context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = r.getHandlerMethods();
        
        List<String> returnList = new ArrayList<String>();
        returnList.add("/*");
        returnList.add("/auto/*");
        Set<String> controllerSet = new HashSet<String>();
        
        Set<RequestMappingInfo> set =  map.keySet();
        for (RequestMappingInfo info : set) {
            Set<String> uriSet = info.getPatternsCondition().getPatterns(); 
            for (String uri : uriSet) {  
                if (!filterUri(uri)) {
                	if (isMenu(info) ) {
                		menuList.add(uri);
                	}
                	if (uri.startsWith("/auto/")) {
                    	String[] u = uri.split("/");
                        controllerSet.add("/auto/" + u[2] + "/*");
                    }
                    returnList.add(uri.toString());
                }
            }
        }
        returnList.addAll(controllerSet);
        return ControllerReturn.success(returnList, Map.of("menuList", menuList));
    }
	
	private boolean filterUri(String uri) {
		 // 排除监控部分/monitorConf/ /monitorView/ /error
		 // 监控部分
        if (uri.startsWith("/auto/monitorConf/")) return true;
        if (uri.startsWith("/auto/monitorView/")) return true;
            
        // 权限部分
        if (uri.startsWith("/auto/grant/")) return true;
        if (uri.startsWith("/auto/index/")) return true;
        if (uri.startsWith("/auto/login/")) return true;
        if (uri.startsWith("/auto/res/")) return true;
        
        if (uri.startsWith("/error")) return true;
        
        return false;
	}
	
	private boolean isMenu(RequestMappingInfo info) {
		Set<RequestMethod> methodSet = info.getMethodsCondition().getMethods();
    	if (methodSet.contains(RequestMethod.GET)) {
    		return true;
    	}
    	return false;
	}
	
    // >>>>>>>>>>>>>>>....new
    public Map<?, ?> insertRes(@RequestParam int parentId, @RequestParam String resName,
    		@RequestParam int resType, String menuUri) {
    	return ControllerReturn.success(impl.insertRes(parentId, resName, resType, menuUri));
    }
    
    public Map<?, ?> updateRes(@RequestParam int id, @RequestParam String resName) {
    	return ControllerReturn.success(impl.updateRes(id, resName));
    }
    
    public Map<?, ?> deleteRes(@RequestParam int id) {
    	return ControllerReturn.success(impl.deleteRes(id));
    }
    
    public Map<?, ?> updateResPrio(String ids) {
    	return ControllerReturn.success(impl.updateResPrio(ids));
    }
    
    public Map<?, ?> insertUri(@RequestParam Integer resId, @RequestParam String uri, Integer menuId) {
    	impl.insertUri(resId, uri, menuId);
		return ControllerReturn.success(getUri(resId));
	}	
    
    public Map<?, ?> getUri(@RequestParam Integer resId) {
		List<Map<String, Object>> list = impl.getUri(resId);	
		return ControllerReturn.success(list);
	}
    
    public Map<Object, Object> deleteUri(@RequestParam int resId, @RequestParam int uriSeq) {
		impl.deleteUri(resId, uriSeq);
		return ControllerReturn.success();
	}
    
    
    
    
    
    
    
    
    
    
}