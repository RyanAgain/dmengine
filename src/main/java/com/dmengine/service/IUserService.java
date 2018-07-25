package com.dmengine.service;

import com.dmengine.common.ServerResponse;
import com.dmengine.pojo.User;

/**
 * 2018-06-07-17:13 Author By AgainP
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> queryPhone(String username);

    ServerResponse<String> checkPhone(String username,String phone);

    ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken);

    ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer id);

}
