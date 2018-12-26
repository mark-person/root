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
import com.ppx.cloud.common.contoller.ReturnMap;
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
        return ReturnMap.of(page, list);
    }

    
    public Map<?, ?> insertChild(AuthAccount bean) {
        return impl.insertChild(bean);
    }

    
    public Map<?, ?> getChild(@RequestParam Integer id) {
        return ReturnMap.of("pojo", impl.getChild(id));
    }

   
    public Map<?, ?> updateAccount(AuthAccount bean) {
        return impl.updateAccount(bean);
    }

   
    public Map<?, ?> updatePassword(@RequestParam Integer accountId,
            @RequestParam String loginPassword) {
        return impl.updatePassword(accountId, loginPassword);
    }

    public Map<?, ?> deleteChild(Integer id) {
        return impl.deleteChild(id);
    }
    
    public Map<?, ?> disable(Integer id) {
    	return impl.disable(id);
    }
    
    public Map<?, ?> enable(Integer id) {
    	return impl.enable(id);
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
        return ReturnMap.of(page, list);
    }

    public Map<?, ?> getAuthorize(@RequestParam Integer accountId) {
        Map<?, ?> resMap = resourceImpl.getResource();
        if (resMap == null) {
            return ReturnMap.of(4001, "资源为空");
        }
        return ReturnMap.of("resIds", grantImpl.getGrantResIds(accountId));
    }

    public Map<?, ?> saveAuthorize(@RequestParam Integer accountId, @RequestParam String resIds) {
        return grantImpl.saveGrantResIds(accountId, resIds);
    }
    

}