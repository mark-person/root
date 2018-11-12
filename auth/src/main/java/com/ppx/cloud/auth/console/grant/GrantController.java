package com.ppx.cloud.auth.console.grant;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.bean.Merchant;
import com.ppx.cloud.auth.console.res.ResourceService;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;


/**
 * 分配权限
 * @author mark
 * @date 2018年7月2日
 */
@Controller
public class GrantController {
	
	@Autowired
	private GrantServiceImpl impl;
	
	@Autowired
	private ResourceService resourceServ;
	
	@GetMapping
    public ModelAndView grantToMerchant() {			
		ModelAndView mv = new ModelAndView();
		mv.addObject("listJson", listMerchant(new Page(), new Merchant()));
		return mv;
	}
	
	@PostMapping @ResponseBody
	public Map<Object, Object> listMerchant(Page page, Merchant mer) {
		List<Merchant> list = impl.listMerchant(page, mer);
		return ControllerReturn.success(list, page);
	}
	
	@PostMapping @ResponseBody
	public Map<Object, Object> getAuthorize(@RequestParam Integer accountId) {
	    Map<?, ?> resMap = resourceServ.getResource();
        if (resMap == null) {
            return ControllerReturn.success(-1);
        }
        Map<Object, Object> returnMap = ControllerReturn.success(resMap);
        
		returnMap.put("resIds", impl.getGrantResIds(accountId));
		return returnMap;
	}
	
	@PostMapping @ResponseBody
	public Map<Object, Object> saveAuthorize(@RequestParam Integer accountId, @RequestParam String resIds) {
		long r = impl.saveGrantResIds(accountId, resIds);
		return ControllerReturn.success(r);
	}

}