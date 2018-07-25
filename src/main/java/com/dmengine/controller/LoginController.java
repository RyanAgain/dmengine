package com.dmengine.controller;


import com.dmengine.common.Const;
import com.dmengine.common.ServerResponse;
import com.dmengine.pojo.User;
import com.dmengine.common.ResponseCode;
import com.dmengine.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 2018-05-29-10:57 Author By AgainP
 */
@Controller
@RequestMapping("/user/")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IUserService IUserService;


    /**
     * 用户登陆服务
     * @param username
     * @param password
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession httpSession) {

        ServerResponse<User> result = IUserService.login(username, password);
        if (result.isSuccess()) {
            httpSession.setAttribute(Const.CURRENT_USER, result.getData());
        }
        return result;
    }

    @RequestMapping(value = "logout.do",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<String> logout(HttpSession httpSession){
        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }


    @RequestMapping(value = "register.do",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<String> register(User user){
        return IUserService.register(user);
    }


    /**
     * 用户名及Email校验接口
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return IUserService.checkValid(str,type);
    }


    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<User>getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMsg("用户未登陆，无法获取当前用户信息");
    }

    /**
     * 当忘记密码时：1、匹配对应用户的手机号码
     * @param username
     * @return
     */
    @RequestMapping(value = "query_phone.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> queryPhone(String username){
        return IUserService.queryPhone(username);
    }


    /**
     * 当忘记密码时：2、验证对应的手机号
     * @param username
     * @param phone
     * @return
     */
    @RequestMapping(value = "check_phone.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> checkPhone(String username,String phone){
        return IUserService.checkPhone(username,phone);
    }

    /**
     * 当忘记密码时：3、重置密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        return IUserService.forgetResetPassword(username,newPassword,forgetToken);
    }

    /**
     * 登陆下的重置密码
     * @param oldPassword
     * @param newPassword
     * @param session
     * @return
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        return IUserService.resetPassword(oldPassword,newPassword,user);
    }

    /**
     * 更新个人用户信息
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<User> updateInformation(User user,HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }

        //防止纵向越权，强制设定用户信息，避免用户修改id及用户名
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> serverResponse = IUserService.updateInformation(user);
        if (serverResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
        }
        return serverResponse;

    }

    /**
     * 获取个人用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ServerResponse<User>getInformation(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByException(ResponseCode.NEED_LOGIN.getCode(),"未登陆要求强制登陆,status = 10");
        }
        return IUserService.getInformation(currentUser.getId());
    }

}
