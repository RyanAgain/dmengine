package com.dmengine.dao;

import com.dmengine.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    //通过用户名和密码查询登陆信息
    User selectLogin(@Param("username") String username, @Param("password") String password);

    int checkEmail(String email);

    String queryPhone(String username);

    int checkPhone(@Param("username") String username, @Param("phone") String phone);

    int updatePasswordByUserName(@Param("username") String username, @Param("password") String password);

    int checkPassword(@Param("id") Integer id, @Param("oldPassword") String oldPassword);

    int checkEmailByUserId(@Param("email") String email, @Param("id") Integer id);
}