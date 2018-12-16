package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
		@SuppressWarnings("rawtypes")
		Map map = impl.getResource();
		if (map == null) {
			return ControllerReturn.success(-1);
		}
		return ControllerReturn.success(map);
	}
	
	public Map<Object, Object> saveResource(@RequestParam String tree, String removeIds) {
		impl.saveResource(tree, removeIds);
		return ControllerReturn.success();
	}
		
	
	
	
	
	
	
    public Map<?, ?> getResUri() {
        RequestMappingHandlerMapping r = ApplicationUtils.context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = r.getHandlerMethods();
        
        // 排除监控部分/monitorConf/ /monitorView/ /error
        List<String> returnList = new ArrayList<String>();
        returnList.add("/*");
        
        Set<String> controllerSet = new HashSet<String>();
        
        Set<RequestMappingInfo> set =  map.keySet();
        for (RequestMappingInfo info : set) {
            Set<String> uriSet = info.getPatternsCondition().getPatterns();
            for (Object uri : uriSet) {     
                // 监控部分
                if (uri.toString().startsWith("/auto/monitorConf/")) continue;
                if (uri.toString().startsWith("/auto/monitorView/")) continue;
                    
                // 权限部分
                if (uri.toString().startsWith("/grant/")) continue;
                if (uri.toString().startsWith("/index/")) continue;
                if (uri.toString().startsWith("/login/")) continue;
                if (uri.toString().startsWith("/resource/")) continue;
                
                if (uri.toString().startsWith("/error")) continue;
                
                String[] u = uri.toString().split("/");
                if (u.length == 3) {
                    controllerSet.add("/" + u[1] + "/*");
                }
                returnList.add(uri.toString());
            }
        }
     
        returnList.addAll(controllerSet);
        return ControllerReturn.success(returnList);
    }
    
    
    
    // >>>>>>>>>>>>>>>....new
    public Map<?, ?> insertRes(@RequestParam int parentId, @RequestParam String resName,
    		@RequestParam int resType, Integer uriSeq) {
    	return ControllerReturn.success(impl.insertRes(parentId, resName, resType, uriSeq));
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