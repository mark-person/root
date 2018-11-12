package com.ppx.cloud.auth.console.res;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class ResourceController {
	
	@Autowired
	private ResourceServiceImpl impl;
	
	
	@GetMapping
    public ModelAndView resource() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("res", getResource());
		return mv;
	}
	
	@PostMapping @ResponseBody
	public Map<Object, Object> getResource() {
		@SuppressWarnings("rawtypes")
		Map map = impl.getResource();
		if (map == null) {
			return ControllerReturn.success(-1);
		}
		return ControllerReturn.success(map);
	}
	
	@PostMapping @ResponseBody
	public Map<Object, Object> saveResource(@RequestParam String tree, String removeIds) {
		impl.saveResource(tree, removeIds);
		return ControllerReturn.success();
	}
		
	@PostMapping @ResponseBody
	public Map<Object, Object> getUri(@RequestParam Integer resId) {
		@SuppressWarnings("rawtypes")
		Map map = impl.getUri(resId);		
		if (map == null) {
			return ControllerReturn.success(-1);
		}		
		return ControllerReturn.success(map);
	}
	
	@PostMapping @ResponseBody
	public Map<Object, Object> saveUri(@RequestParam Integer resId, @RequestParam String uri, Integer menuId) {
		Map<?, ?> map = impl.saveUri(resId, uri, menuId);
		return ControllerReturn.success(map);
	}	
	
	@RequestMapping
	@ResponseBody
	public Map<Object, Object> removeUri(@RequestParam Integer resId, @RequestParam String uri, @RequestParam int uriIndex) {
		impl.removeUri(resId, uri, uriIndex);
		return ControllerReturn.success();
	}
	
	@RequestMapping @ResponseBody
    public Map<Object, Object> getResourceUri() {
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
                if (uri.toString().startsWith("/monitorConf/")) continue;
                if (uri.toString().startsWith("/monitorView/")) continue;
                    
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
}