package com.brainsci.springsecurity.vo;

/**
 * 注册接口封装对象
 * @author Zeng
 * @date Dec 10, 2018
 */
public class RegBean {
    private String email;
    private String password;
    private String avatar;
    private String name;
    private String verifyCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Override
    public String toString(){
        return "LoginBean{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", passwd='" + password + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                ", avatar='" + !avatar.equals("") + '\'' +
                '}';
    }
}
