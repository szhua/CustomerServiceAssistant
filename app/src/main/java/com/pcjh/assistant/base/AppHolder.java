package com.pcjh.assistant.base;


import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;

/**
 * Created by szhua on 2016/10/19.
 */
public class AppHolder {


    private String password ;

    private String token  ;


    private UserInfo user  =new UserInfo();
    private Users users =new Users() ;

    public void setUsers(Users users) {
        this.users = users;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public Users getUsers() {
        return users;
    }

    public void setHasMsg(boolean hasMsg) {
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public UserInfo getUser() {
        return user;
    }

    private static AppHolder ourInstance = new AppHolder();

    public static AppHolder getInstance() {
        return ourInstance;
    }

    private AppHolder() {
    }






}
