package com.library.common.model;

public class LoginUser {
    private Long id; private String username; private String realName; private String role;
    public LoginUser() { }
    public LoginUser(Long id, String username, String realName, String role) { this.id=id; this.username=username; this.realName=realName; this.role=role; }
    public Long getId(){return id;} public void setId(Long v){id=v;} public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getRealName(){return realName;} public void setRealName(String v){realName=v;} public String getRole(){return role;} public void setRole(String v){role=v;}
}
