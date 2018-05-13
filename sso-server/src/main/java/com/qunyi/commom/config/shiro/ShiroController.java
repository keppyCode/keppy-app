package com.qunyi.commom.config.shiro;

import com.qunyi.modules.model.User;
import com.qunyi.modules.pojo.ResultMsg;
import com.qunyi.modules.utils.ResultStatusCode;
import org.apache.shiro.SecurityUtils;

import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * shiro 控制层数操作类
 * @author  liuqiuping
 * @date    2018-4-21
 */
@Controller
public class ShiroController {
    protected final static Logger logger = LoggerFactory.getLogger(ShiroController.class);

    /**
     * 登录验证
     * @param user
     * @return
     */
    @RequestMapping(value="/loginAction",method = {RequestMethod.POST})
    @ResponseBody
    public ResultMsg login(User user, boolean rememberMe, HttpServletRequest request){
        logger.info("正在执行登录验证方法》》》》》》》》》》》》》》》》》》》》");
        ResultMsg resultMsg = null;

        try {
                Subject subject = SecurityUtils.getSubject();

            DefaultUsernamepasswordToken usernamePasswordToken = new DefaultUsernamepasswordToken(user.getLoginName(), user.getPassWord(),user.getLoginType(),rememberMe);
                //登录认证
                subject.login(usernamePasswordToken);
                //获取targetUrl
                SavedRequest savedRequest = WebUtils.getSavedRequest(request);
                resultMsg= new ResultMsg(ResultStatusCode.OK.getErrcode(),ResultStatusCode.OK.getErrmsg(),null==savedRequest?"":savedRequest.getRequestUrl());

                logger.info("登录认证成功，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]，targeturl：[{}]",user.getLoginName(),user.getPassWord(),rememberMe,resultMsg.getTargetUrl());

        }catch (UnknownAccountException uae) {

            logger.info("登录认证失败！账号错误或不存在，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]",user.getLoginName(),user.getPassWord(),rememberMe);

            resultMsg= new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),ResultStatusCode.INVALID_PASSWORD.getErrcode());

            return resultMsg;
        } catch (IncorrectCredentialsException ice) {
                logger.info("登录认证失败！密码错误，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]",user.getLoginName(),user.getPassWord(),rememberMe);

                resultMsg= new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),ResultStatusCode.INVALID_PASSWORD.getErrcode());

                return resultMsg;
        } catch (LockedAccountException lae) {

                logger.info("登录认证失败！帐号被锁定，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]",user.getLoginName(),user.getPassWord(),rememberMe);

                resultMsg= new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),ResultStatusCode.INVALID_PASSWORD.getErrcode());

                return resultMsg;
        } catch (ExcessiveAttemptsException ae) {
                logger.info("登录认证失败！登录失败次数过多，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]",user.getLoginName(),user.getPassWord(),rememberMe);

                resultMsg= new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),ResultStatusCode.INVALID_PASSWORD.getErrcode());
                return resultMsg;

        } catch (DisabledAccountException ea){

                logger.info("登录认证失败！帐号被禁用，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]",user.getLoginName(),user.getPassWord(),rememberMe);

                resultMsg= new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),ResultStatusCode.INVALID_PASSWORD.getErrcode());

                return resultMsg;
        } catch (ExpiredCredentialsException et){

                logger.info("登录认证失败！凭证过期，userName账号：[{}],passWord密码：[{}]，rememberMe记住用户可选参数值：[{}]",user.getLoginName(),user.getPassWord(),rememberMe);

                resultMsg= new ResultMsg(ResultStatusCode.INVALID_PASSWORD.getErrcode(),ResultStatusCode.INVALID_PASSWORD.getErrcode());

                return resultMsg;
        }



        return resultMsg;
    }


    /**
     * 主页
     * @return
     */
    @RequestMapping(value="/index",method = {RequestMethod.GET, RequestMethod.POST})
    public String index(){

        return "/index";
    }


}
