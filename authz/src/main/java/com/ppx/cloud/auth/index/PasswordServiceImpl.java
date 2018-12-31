package com.ppx.cloud.auth.index;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.auth.common.AuthContext;
import com.ppx.cloud.auth.config.AuthUtils;

/**
 * 
 * @author mark
 * @date 2018年12月18日
 */
@Service
public class PasswordServiceImpl extends MyDaoSupport {

    public Map<String, Object> updatePassword(String oldPassword, String newPassord) {
        Integer accoutId = AuthContext.getLoginAccount().getAccountId();
        Map<String, Object> map = getJdbcTemplate()
                .queryForMap("select login_password from auth_account where account_id = ?", accoutId);
        String password = (String) map.get("login_password");
        if (!password.equals(AuthUtils.getMD5Password(oldPassword))) {
            // 旧密码不正确
            return ReturnMap.of(4001, "旧密码不正确");
        }
        // 更新密码
       getJdbcTemplate().update("update auth_account set login_password = ?, modified = now() where account_id = ?",
                AuthUtils.getMD5Password(oldPassword), accoutId);
        return ReturnMap.of();
    }

}
