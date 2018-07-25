package com.dmengine.common;

/**
 * 2018-06-21-11:03 Author By AgainP
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public static final String PHONE = "phone";

    //tips:利用接口对常量进行分类
    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;  //管理员
    }

}
