package com.qunyi.modules.dao;

import com.qunyi.modules.model.User;

public interface UserMapper {

    /**
     * 通过账户名查询用户对象
     * @param loginName
     * @return
     */
    User findByLoginName(String loginName);

    User getUserInfo(String loginName);

    /**
     * 测试数据库查询
     * @param loginName
     * @return
     */
    User findByLoginNameTest(String loginName);
}
