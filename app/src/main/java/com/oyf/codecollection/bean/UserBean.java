package com.oyf.codecollection.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.oyf.codecollection.BR;


/**
 * @创建者 oyf
 * @创建时间 2019/12/2 10:11
 * @描述
 **/
public class UserBean extends BaseObservable {

    private String username;
    private String usernamehint;
    private String pwd;
    private String pwdHint;

    public UserBean() {
    }

    public UserBean(String username, String usernamehint, String pwd, String pwdHint) {
        this.username = username;
        this.usernamehint = usernamehint;
        this.pwd = pwd;
        this.pwdHint = pwdHint;
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(com.oyf.codecollection.BR.username);
    }

    public String getUsernamehint() {
        return usernamehint;
    }

    public void setUsernamehint(String usernamehint) {
        this.usernamehint = usernamehint;
    }

    @Bindable
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
        notifyPropertyChanged(com.oyf.codecollection.BR.pwd);
    }

    public String getPwdHint() {
        return pwdHint;
    }

    public void setPwdHint(String pwdHint) {
        this.pwdHint = pwdHint;
    }
}
