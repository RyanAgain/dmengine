package com.dmengine.service.Impl;

import com.dmengine.common.Const;
import com.dmengine.common.TokenCache;
import com.dmengine.dao.UserMapper;
import com.dmengine.common.ServerResponse;
import com.dmengine.pojo.User;
import com.dmengine.service.IUserService;
import com.dmengine.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 2018-06-07-17:24 Author By AgainP
 */
@Service("IUserService")
public class IUserServiceImpl implements IUserService {


    @Autowired
    private UserMapper userMapper;

    //通用检查类
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //开始参数校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMsg("用户名已存在");
                }
            }else if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMsg("email已存在");
                }
            }else{
                return ServerResponse.createByErrorMsg("参数错误");
            }
        }else{
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccessMsg("校验成功");

    }

    public ServerResponse<User> login(String username, String password) {

            int resultCount = userMapper.checkUserName(username);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMsg("用户名不存在");
            }

            String md5Password = MD5Util.MD5EncodeUtf8(password);

            User user = userMapper.selectLogin(username,md5Password);
            if(user == null ){
                return ServerResponse.createByErrorMsg("密码错误");
            }

            user.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse<String> vaildResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }

        vaildResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!vaildResponse.isSuccess()){
            return vaildResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultConut = userMapper.insert(user);

        if (resultConut == 0){
            return ServerResponse.createByErrorMsg("注册失败");
        }

        return ServerResponse.createBySuccessMsg("注册成功");
    }

    public ServerResponse<String> queryPhone(String username){

        ServerResponse vaildResponse = this.checkValid(username,Const.USERNAME);
        if(vaildResponse.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        String phone = userMapper.queryPhone(username);
        if (StringUtils.isNotBlank(phone)){
            return ServerResponse.createBySuccess(MD5Util.MD5EncodeUtf8(phone));
        }

        return ServerResponse.createByErrorMsg("该用户手机号为空");

    }

    public ServerResponse<String> checkPhone(String username,String phone){

        int resultCount = userMapper.checkPhone(username, phone);
        if(resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMsg("手机号错误");
    }



    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("参数错误，请按正常流程处理");
        }

        ServerResponse<String> vaildResponse = this.checkValid(username,Const.USERNAME);
        if (vaildResponse.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        String token  = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);

        if (StringUtils.isBlank(token)){
            return ServerResponse.createBySuccessMsg("Token无效或过期");
        }

        if (StringUtils.equals(token,forgetToken)){
            String md5PassWd = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUserName(username,md5PassWd);

            if (rowCount>0){
                return ServerResponse.createBySuccessMsg("密码修改成功");
            }

        }else{
            return ServerResponse.createBySuccessMsg("Token错误或过期,请重新获取重置密码的Token");
        }

        return ServerResponse.createByErrorMsg("密码修改失败");

    }

    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        //防止横向越权，需要校验用户旧密码

        int resultConut = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(oldPassword));
        if(resultConut == 0){
            return ServerResponse.createByErrorMsg("旧密码错误，请重新输入");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMsg("密码更新成功！");
        }

        return ServerResponse.createByErrorMsg("密码更新失败，请重试");

    }

    public ServerResponse<User> updateInformation(User user) {
        //username不能更新
        //email要进行校验，校验新的email是不是已经存在，存在则不能更新为当前用户的email
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount > 0){
            return ServerResponse.createByErrorMsg("该Email已被占用，请更换");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setNickname(user.getNickname());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            return ServerResponse.createBySuccess("个人信息更新成功",updateUser);
        }

        return ServerResponse.createByErrorMsg("个人信息更新失败，请重试");

    }

    public ServerResponse<User> getInformation(Integer id) {
        User user =userMapper.selectByPrimaryKey(id);

        if (user == null){
            return ServerResponse.createByErrorMsg("无法找到个人信息");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }
}
