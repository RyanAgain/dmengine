package com.dmengine.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * 2018-06-21-9:21 Author By AgainP
 */

//高可用的状态响应
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL) //忽略存在null值的响应，避免出现无用信息
public class ServerResponse<T> implements Serializable {

    //返回前端响应状态码
    private int status;

    //返回对应状态码的响应信息
    private String msg;

    //泛型数据，方便成功时返回对应的对象;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    //避免无关信息被序列化至Json响应
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    //普通成功响应
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    //成功响应携带信息
    public static <T> ServerResponse<T> createBySuccessMsg(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    //成功响应响应携带对象
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    //成功响应携带所有信息
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    //公共错误响应
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    //公共错误响应携带错误信息
    public static <T> ServerResponse<T> createByErrorMsg(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMsg);
    }

    //所有另外的异常的响应构造器
    public static <T> ServerResponse<T> createByException(int ExceptionCode,String ExceptionMsg){
        return new ServerResponse<T>(ExceptionCode,ExceptionMsg);
    }

}
