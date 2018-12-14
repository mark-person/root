package com.ppx.cloud.auth.console.mer;

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
import com.ppx.cloud.auth.bean.MerchantAccount;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;


/**
 * 商户管理
 * @author mark
 * @date 2018年7月2日
 */
@Controller
public class MerchantController {

	@Autowired
	private MerchantServiceImpl impl;
	
    public ModelAndView listMerchant() {			
		ModelAndView mv = new ModelAndView();
		mv.addObject("listJson", listJson(new Page(), new Merchant()));
		return mv;
	}
	
	public Map<Object, Object> listJson(Page page, Merchant mer) {
		List<Merchant> list = impl.listMerchant(page, mer);
		return ControllerReturn.success(list);
	}
	
	public Map<Object, Object> insertMerchant(Merchant bean) {
		int r = impl.insertMerchant(bean);
		return ControllerReturn.success(r);
	}
	
	public Map<Object, Object> getMerchant(@RequestParam Integer id) {
		Merchant bean = impl.getMerchant(id);
		return ControllerReturn.success(bean);
	}
	
	public Map<Object, Object> getMerchantAccount(@RequestParam Integer id) {
		MerchantAccount bean = impl.getMerchantAccount(id);
		return ControllerReturn.success(bean);
	}
	
	public Map<Object, Object> updateMerchant(Merchant bean) {
		int r = impl.updateMerchant(bean);
		return ControllerReturn.success(r);
	}
	
	@PostMapping @ResponseBody
	public Map<Object, Object> updateMerchantAccount(MerchantAccount bean) {
		int r = impl.updateMerchantAccount(bean);
		return ControllerReturn.success(r);
	}
	
	public Map<Object, Object> updateMerchantPassword(@RequestParam Integer merId, @RequestParam String merPassword) {
		int r = impl.updateMerchantPassword(merId, merPassword);
		return ControllerReturn.success(r);
	}
	
	public Map<Object, Object> deleteMerchant(Integer id) {
		int r = impl.deleteMerchant(id);
		return ControllerReturn.success(r);
	}
	
    public Map<Object, Object> disable(Integer id) {
        int r = impl.disable(id);
        return ControllerReturn.success(r);
    }
	
    public Map<Object, Object> enable(Integer id) {
        int r = impl.enable(id);
        return ControllerReturn.success(r);
    }

}