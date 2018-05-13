package com.qunyi.modules.dao;

import com.qunyi.modules.model.Role;

import java.util.List;

/**
 * @author Administrator
 */
public interface  RoleMapper {

      int deleteByPrimaryKey(String id);

      int insert(Role record);

      int insertSelective(Role record);

      Role selectByPrimaryKey(String id);

      List<Role> selectByUserId(String userId);

      int updateByPrimaryKeySelective(Role record);

     int updateByPrimaryKey(Role record);
}