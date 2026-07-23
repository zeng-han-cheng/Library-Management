package com.library.model.entity;
public class Admin extends BaseEntity { private String username,password,realName,phone; private Integer status;
 public String getUsername(){return username;} public void setUsername(String v){username=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;} public String getRealName(){return realName;} public void setRealName(String v){realName=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public Integer getStatus(){return status;} public void setStatus(Integer v){status=v;} }
