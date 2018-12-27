package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ppx.cloud.common.contoller.ReturnMap;
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
	
	public Map<?, ?> getResource() {
		Map<String, Object> resMap = impl.getResource();
		if (resMap == null) {
			return ReturnMap.of(4001, "资源为空");
		}
		resMap.putAll(ReturnMap.of());
		return resMap;
	}
	
	public Map<?, ?> getResUri() {
		List<String> menuList = new ArrayList<String>();
		
        RequestMappingHandlerMapping r = ApplicationUtils.context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = r.getHandlerMethods();
        
        List<String> resList = new ArrayList<String>();
        resList.add("/*");
        resList.add("/auto/*");
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
                    resList.add(uri.toString());
                }
            }
        }
        resList.addAll(controllerSet);
        
        return ReturnMap.of("list", resList, "menuList", menuList);
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
    	return ReturnMap.of("resId", impl.insertRes(parentId, resName, resType, menuUri));
    }
    
    public Map<?, ?> updateRes(@RequestParam int id, @RequestParam String resName) {
    	return impl.updateRes(id, resName);
    }
    
    public Map<?, ?> deleteRes(@RequestParam int id) {
    	return impl.deleteRes(id);
    }
    
    public Map<?, ?> updateResPrio(String ids) {
    	return impl.updateResPrio(ids);
    }
    
    public Map<?, ?> insertUri(@RequestParam Integer resId, @RequestParam String uri, Integer menuId) {
    	impl.insertUri(resId, uri, menuId);
		return getUri(resId);
	}	
    
    public Map<?, ?> getUri(@RequestParam Integer resId) {
		List<Map<String, Object>> list = impl.getUri(resId);	
		return ReturnMap.of("list", list);
				
	}
    
    public Map<?, ?> deleteUri(@RequestParam int resId, @RequestParam int uriSeq) {
		return impl.deleteUri(resId, uriSeq);
	}
    
    
    
    
    
    
    
    
    
    
}