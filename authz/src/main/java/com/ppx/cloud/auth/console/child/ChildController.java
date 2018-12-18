package com.ppx.cloud.auth.console.child;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.auth.console.grant.GrantServiceImpl;
import com.ppx.cloud.auth.console.res.ResServiceImpl;
import com.ppx.cloud.auth.pojo.AuthAccount;
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

    public ModelAndView child() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("list", list(new Page(), new AuthAccount()));
        return mv;
    }

  
    public Map<?, ?> list(Page page, AuthAccount child) {
        List<AuthAccount> list = impl.listChild(page, child);
        return ControllerReturn.success(list, page);
    }

    
    public Map<?, ?> insertChild(AuthAccount bean) {
        int r = impl.insertChild(bean);
        return ControllerReturn.success(r);
    }

    
    public Map<?, ?> getChild(@RequestParam Integer id) {
        AuthAccount bean = impl.getChild(id);
        return ControllerReturn.success(bean);
    }

   
    public Map<?, ?> updateAccount(AuthAccount bean) {
        int r = impl.updateAccount(bean);
        return ControllerReturn.success(r);
    }

   
    public Map<?, ?> updatePassword(@RequestParam Integer accountId,
            @RequestParam String loginPassword) {
        int r = impl.updatePassword(accountId, loginPassword);
        return ControllerReturn.success(r);
    }

    public Map<?, ?> deleteChild(Integer id) {
        int r = impl.deleteChild(id);
        return ControllerReturn.success(r);
    }
    
    public Map<?, ?> disable(Integer id) {
        int r = impl.disable(id);
        return ControllerReturn.success(r);
    }
    
    public Map<?, ?> enable(Integer id) {
        int r = impl.enable(id);
        return ControllerReturn.success(r);
    }
    
    
    

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>子帐号权限
    @Autowired
    private GrantServiceImpl grantImpl;

    @Autowired
    private ResServiceImpl resourceImpl;

    public ModelAndView grantToChild() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("list", listChildAccount(new Page(), new AuthAccount()));
        return mv;
    }

    public Map<?, ?> listChildAccount(Page page, AuthAccount child) {
        List<AuthAccount> list = impl.listChild(page, child);
        return ControllerReturn.success(list, page);
    }

    public Map<?, ?> getAuthorize(@RequestParam Integer accountId) {
        Map<?, ?> resMap = resourceImpl.getResource();
        if (resMap == null) {
            return ControllerReturn.success(-1);
        }
        Map<Object, Object> returnMap = ControllerReturn.success(resMap);
        returnMap.put("resIds", grantImpl.getGrantResIds(accountId));
        return returnMap;
    }

    public Map<?, ?> saveAuthorize(@RequestParam Integer accountId, @RequestParam String resIds) {
        long r = grantImpl.saveGrantResIds(accountId, resIds);
        return ControllerReturn.success(r);
    }
    

}