package com.ppx.cloud.auth.filter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ppx.cloud.auth.common.LoginAccount;
import com.ppx.cloud.auth.config.AuthProperties;
import com.ppx.cloud.auth.config.AuthUtils;
import com.ppx.cloud.auth.pojo.AuthAccount;
import com.ppx.cloud.common.context.CommonContext;
import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.exception.security.PermissionUriException;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.common.util.CookieUtils;

/**
 * 权限过滤工具
 * 
 * @author mark
 * @date 2018年7月2日
 */
public class AuthFilterUtils {
	
    private static final Logger logger = LoggerFactory.getLogger(AuthFilterUtils.class);

    /**
     * # 跳到login的情况 1. 没有token 2. tockcen校验异常(token不同法或jwt密码被改) 3. 帐号状态异常 4.
     * modified不同（账号和密码被修改时） # 说明 1.token过期时，将重新验证，合法就产生新的token，不合法就跳到login页
	  * 获取token里的用户信息
     * 
     * @param request
     * @return 返回null则跳到login页
     */
    public static LoginAccount getLoginAccout(HttpServletRequest request, HttpServletResponse response, String uri) {

        // 从cookie中取得token
        String token = CookieUtils.getCookieMap(request).get(AuthUtils.PPXTOKEN);

        // token为空,表示未登录
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        Algorithm algorithm = null;
        DecodedJWT jwt = null;
        try {
            algorithm = Algorithm.HMAC256(AuthUtils.getJwtPassword());
            JWTVerifier verifier = JWT.require(algorithm).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            // 不打印轨迹(token不合法或jwt密码修改，防止攻击)
            logger.error(e.getMessage());
            return null;
        }

        Integer accountId = jwt.getClaim("accountId").asInt();
        String loginAccount = jwt.getClaim("loginAccount").asString();
        Integer userId = jwt.getClaim("userId").asInt();
        String userName = jwt.getClaim("userName").asString();

        LoginAccount account = new LoginAccount();
        account.setAccountId(accountId);
        account.setLoginAccount(loginAccount);
        account.setUserId(userId);
        account.setUserName(userName);
        
        // 传值给 monitor
        CommonContext.setAccountId(accountId);
        
        // 超级管理员不拦截
        if ("admin".equals(loginAccount)) {
            return account;
        }

        // 修改密码后原token无效,帐号置无效后token无效 >>>>>>>>>>>>>
        int validateSecond = AuthProperties.JWT_VALIDATE_SECOND;

        if (System.currentTimeMillis() - jwt.getIssuedAt().getTime() >= validateSecond * 1000) {
            // 重新检验,并重新生成token
            AuthFilterServiceImpl filterService = ApplicationUtils.context.getBean(AuthFilterServiceImpl.class);
            AuthAccount accountDb = filterService.getAccountFromDb(accountId);
            if (accountDb.getAccountStatus() != AuthUtils.ACCOUNT_STATUS_EFFECTIVE) {
                // 状态无效，重新登录
                return null;
            }
            Date modified = jwt.getClaim("modified").asDate();
            if (accountDb.getModified().equals(modified)) {
                // 密码或帐号被修改，重新登录
                return null;
            }

            // 重新在cookie上生成一个token
            token = JWT.create().withIssuedAt(new Date()).withClaim("accountId", accountId)
                    .withClaim("loginAccount", loginAccount).withClaim("userId", userId).withClaim("userName", userName)
                    .withClaim("modified", accountDb.getModified()).sign(algorithm);
            CookieUtils.setCookie(response, AuthUtils.PPXTOKEN, token);
        }

        // 所有用户进入/auto/index/(菜单、修改密码等)或/auto/home的不拦截
        if (uri.startsWith("/auto/index/") || uri.startsWith("/auto/home/")) {
            return account;
        }

        // 主帐号进入/child/菜单不拦截
        if (account.isMainAccount() && uri.startsWith("/auto/child/")) {
            return account;
        }

        return filterUri(request, uri, account);
    }

    
    private static LoginAccount filterUri(HttpServletRequest request, String uri, LoginAccount account) {
        AuthFilterServiceImpl filterService = ApplicationUtils.context.getBean(AuthFilterServiceImpl.class);

        // 提示:/*所有权限；/auto/*以/auto/开头权限；/auto/test/*以/auto/test/*开头权限
        
        // 去掉支持带一个参数功能，防止参数攻击
        // uri = uri.replaceFirst("/auto", "");
        // 大中小权限 /* /uriItem/* /uriItem1/uriItem2 /uriItem1/uriItem2?q=1
        List<String> testUriList = new ArrayList<String>(4);
        testUriList.add(uri);
        testUriList.add("/auto/" + uri.split("/")[2] + "/*");
        testUriList.add("/auto/*");
        testUriList.add("/*");
        
        
        boolean missUri = true;
        for (String testUri : testUriList) {
            // 取得URI对应的index
            Integer index = filterService.getIndexFromUri(testUri);
            if (index == null) {
                continue;
            }

            // 判断帐号是否有uri的权限
            BitSet grantBitset = filterService.getAccountResBitSet(account.getAccountId());
            

            if (!account.isMainAccount()) {
                // 当子帐号登录时，必须判用户权限，防止撤消用户权限时，子帐号还有权限()
                BitSet userGrantBitset = filterService.getAccountResBitSet(account.getUserId());
                if (!userGrantBitset.get(index)) {
                    continue;
                }
            }

            if (grantBitset.get(index)) {
                // uri合法访问, 通过request.setAttribute设置操作权限 传入真实URI
                setOperationPermission(request, filterService.getOpUri(uri), grantBitset);
                return account;
            }
            missUri = false;
        }

        if (missUri) {
            // 从数据库找不到对应uri,资源对应的uri找不到
            throw new PermissionUriException(ErrorCode.PERMISSION_URI, "Unauthorized.miss uri:" + uri);
        } else {
            // 帐号没有该URI的权限
            throw new PermissionUriException(ErrorCode.PERMISSION_URI, "Unauthorized.forbiddens:" + uri);
        }
    }
    
    private static void setOperationPermission(HttpServletRequest request, List<Map<String, Object>> opList, BitSet grantBitset) {
        if (opList == null) {
            return;
        }
        for (Map<String, Object> map : opList) {
        	Integer uriSeq = (Integer) map.get("uri_seq");
            if (grantBitset.get(uriSeq)) {
                setOperationRequest(request, (String) map.get("uri_text"));
            }
        }
    }

    // 操作权限设置(可扩展作数据权限用)
    private static void setOperationRequest(HttpServletRequest request, String uri) {
    	uri = uri.replace("/auto", "");
        // /test/saveTest改名成_test_saveTest
        request.setAttribute(uri.replace("/", "_"), true);
    }
}
