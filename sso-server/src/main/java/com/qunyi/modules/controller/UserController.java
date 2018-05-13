package com.qunyi.modules.controller;

import com.qunyi.modules.model.User;
import com.qunyi.modules.service.UserManagerService;
import com.qunyi.modules.utils.BaseViewVO;
import com.qunyi.modules.utils.CommonMethodResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *shrio 认证测试类
 * @author liuqiuping
 * @Description
 * @date 2018-4-15
 */
@Controller
public class UserController {
    protected final static Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    UserManagerService userManagerService;


    /**
     * 测试从sso主库查询数据
     * @param name
     * @return
     */
    @RequestMapping(value="user/findByLoginName",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public User findByLoginName(String name){


        User User = userManagerService.findByLoginName("123");
        return User;
    }

    /**
     * 测试从ssoCluster库查询数据
     * @return
     */
    @RequestMapping(value="events/findByLoginName",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseViewVO roleAdim(){
        //DateUtil.getDate();
        User User = userManagerService.findByLoginNameTest("liuqiuping");
        //方式1
        CommonMethodResultUtil CommonMethodResultUtil = new CommonMethodResultUtil();
        CommonMethodResultUtil.setStatus("123").setMessage("2344");
        CommonMethodResultUtil.toString();
        logger.info(CommonMethodResultUtil.toString());

        //方式2
        return new BaseViewVO(true,"测试请求成功",User);
    }

}
