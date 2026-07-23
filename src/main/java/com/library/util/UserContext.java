package com.library.util;
import com.library.common.model.LoginUser;
public final class UserContext { private static final ThreadLocal<LoginUser> HOLDER=new ThreadLocal<>(); private UserContext(){} public static void set(LoginUser u){HOLDER.set(u);} public static LoginUser get(){return HOLDER.get();} public static void clear(){HOLDER.remove();} }
