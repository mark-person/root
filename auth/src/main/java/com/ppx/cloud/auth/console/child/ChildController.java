package com.ppx.cloud.auth.console.child;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.bean.MerchantAccount;
import com.ppx.cloud.auth.console.grant.GrantServiceImpl;
import com.ppx.cloud.auth.console.res.ResourceServiceImpl;
import com.ppx.cloud.common.contoller.ControllerReturn;
import com.ppx.cloud.common.page.Page;

/**
 * 子帐号管理
 * @author mark
 * @date 2018年7月2日
 */
@Controller
public class ChildController {

    @Autowired
    private ChildServiceImpl impl;

    @GetMapping
    public ModelAndView child() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("listJson", list(new Page(), new MerchantAccount()));
        return mv;
    }

    @PostMapping
    @ResponseBody
    public Map<?, ?> list(Page page, MerchantAccount child) {
        List<MerchantAccount> list = impl.listChild(page, child);
        return ControllerReturn.success(list, page);
    }

    @PostMapping
    @ResponseBody
    public Map<?, ?> insertChild(MerchantAccount bean) {
        int r = impl.insertChild(bean);
        return ControllerReturn.success(r);
    }

    @PostMapping
    @ResponseBody
    public Map<?, ?> getChild(@RequestParam Integer id) {
        MerchantAccount bean = impl.getChild(id);
        return ControllerReturn.success(bean);
    }

    @PostMapping
    @ResponseBody
    public Map<?, ?> updateAccount(MerchantAccount bean) {
        int r = impl.updateAccount(bean);
        return ControllerReturn.success(r);
    }

    @PostMapping
    @ResponseBody
    public Map<?, ?> updatePassword(@RequestParam Integer accountId,
            @RequestParam String loginPassword) {
        int r = impl.updatePassword(accountId, loginPassword);
        return ControllerReturn.success(r);
    }

    @PostMapping @ResponseBody
    public Map<?, ?> deleteChild(Integer id) {
        int r = impl.deleteChild(id);
        return ControllerReturn.success(r);
    }
    
    @PostMapping @ResponseBody
    public Map<?, ?> disable(Integer id) {
        int r = impl.disable(id);
        return ControllerReturn.success(r);
    }
    
    @PostMapping @ResponseBody
    public Map<?, ?> enable(Integer id) {
        int r = impl.enable(id);
        return ControllerReturn.success(r);
    }
    
    
    

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>子帐号权限
    @Autowired
    private GrantServiceImpl grantImpl;

    @Autowired
    private ResourceServiceImpl resourceImpl;

    @GetMapping
    public ModelAndView grantToChild() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("listJson", listChildAccount(new Page(), new MerchantAccount()));
        return mv;
    }

    @PostMapping
    @ResponseBody
    public Map<?, ?> listChildAccount(Page page, MerchantAccount child) {
        List<MerchantAccount> list = impl.listChild(page, child);
        return ControllerReturn.success(list, page);
    }

    @PostMapping
    @ResponseBody
    public Map<?, ?> getAuthorize(@RequestParam Integer accountId) {
        Map<?, ?> resMap = resourceImpl.getResource();
        if (resMap == null) {
            return ControllerReturn.success(-1);
        }
        Map<Object, Object> returnMap = ControllerReturn.success(resMap);
        returnMap.put("resIds", grantImpl.getGrantResIds(accountId));
        return returnMap;
    }

    @PostMapping @ResponseBody
    public Map<?, ?> saveAuthorize(@RequestParam Integer accountId, @RequestParam String resIds) {
        long r = grantImpl.saveGrantResIds(accountId, resIds);
        return ControllerReturn.success(r);
    }
    

}