package com.qunyi.modules.utils;

import com.qunyi.modules.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 基于jwt的api接口认证方式
 */
@RestController
public class JsonWebToken {
    //@Autowired
    //private UserInfoRepository userRepositoy;

    @Autowired
    private Audience audienceEntity;




    @RequestMapping("oauth/token")
    @ResponseBody
    public Object getAccessToken(LoginPara loginPara, HttpServletRequest request, HttpServletResponse response) {
        ResultMsg resultMsg;
        try {
            if (loginPara.getClientId() == null
                    || (loginPara.getClientId().compareTo(audienceEntity.getClientId()) != 0)) {
                resultMsg = new ResultMsg(ResultStatusCode.INVALID_CLIENTID.getErrcode(),
                        ResultStatusCode.INVALID_CLIENTID.getErrmsg());
                return resultMsg;
            }

            //验证码校验在后面章节添加


            //验证用户名密码
            UserInfo user = new UserInfo();// userRepositoy.findUserInfoByName(loginPara.getUserName());
            //测试数据
            user.setUserName("123");
            user.setPassword("123");
            user.setName("123");
            user.setRole("123");

            if (user == null) {
                resultMsg = new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),
                        ResultStatusCode.INVALID_PASSWORD.getErrmsg());
                return resultMsg;
            } else {
               String md5Password ="123";// MyUtils.getMD5(loginPara.getPassword() + user.getSalt());
                if (md5Password.compareTo(user.getPassword()) != 0) {
                    resultMsg = new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),
                            ResultStatusCode.INVALID_PASSWORD.getErrmsg());
                    return resultMsg;
                }
            }

            //拼装accessToken
            String accessToken = JwtHelper.createJWT(loginPara.getUserName(), String.valueOf(user.getName()),
                    user.getRole(), audienceEntity.getClientId(), audienceEntity.getName(),
                    audienceEntity.getExpiresSecond() * 1000, audienceEntity.getBase64Secret());

            //返回accessToken
            AccessToken accessTokenEntity = new AccessToken();
            accessTokenEntity.setAccess_token(accessToken);
            accessTokenEntity.setExpires_in(audienceEntity.getExpiresSecond());
            accessTokenEntity.setToken_type("bearer");
            resultMsg = new ResultMsg(ResultStatusCode.OK.getErrcode(),
                    ResultStatusCode.OK.getErrmsg(), accessTokenEntity);

            //方式1，设置响应头
            response.setHeader("Authorization","bearer "+accessToken);
            //post.setHeader("Authorization", "Basic "+token);

            //方式2，设置cookies
            Cookie[] cookies= request.getCookies();
            Cookie token =new Cookie("ticket",accessToken);
            response.addCookie(token);
            return resultMsg;

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMsg = new ResultMsg(ResultStatusCode.SYSTEM_ERR.getErrcode(),
                    ResultStatusCode.SYSTEM_ERR.getErrmsg());
            return resultMsg;
        }
    }


    @RequestMapping("/user/getusers")
    @ResponseBody
    public Object getusers() {

        return "请求认证成功";

    }
}