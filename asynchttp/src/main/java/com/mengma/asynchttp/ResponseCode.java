package com.mengma.asynchttp;

/**
 * Created by szhua 2016/3/11
 */
public class ResponseCode {
    public final static String SUCCESS = "000"; //访问成功
    public final static String SERVER_ERROR = "001"; //服务器异常
    public final static String REGISTER_SUCCESS = "002"; //注册成功
    public final static String REGISTER_ALREADY = "003"; //此账号已经注册过了
    public final static String LOGIN_SUCCESS = "004"; //登录成功
    public final static String PASSWORD_WRONG = "005"; //密码不正确
    public final static String ACCOUNT_NOT_EXIST = "006"; //账号不存在
    public final static String HEAD_CHANGE_SUCCESS = "007"; //头像修改成功
    public final static String HEAD_CHANGE_FAIL = "008"; //头像修改失败
    public final static String PASSWORD_CHANGE_SUCCESS = "009"; //修改密码成功
    public final static String OLD_PASSWORD_WRONG = "010"; //旧密码不正确
    public final static String LOSE_PARAMS = "011"; //缺少必要的参数
    public final static String INFO_CHANGE_SUCCESS = "012"; //修改信息成功

}
