package com.qunyi.modules.controller;

import com.qunyi.modules.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class test {

    /**
     * 通过用户名查询用户对象
     * @param name
     * @return
     */
    @RequestMapping(value="user/test",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public User findByLoginName(String name){


        User User = new User(); //;
        User.setLoginName("123");
        return User;
    }

}
