package com.library.server.service; import com.library.pojo.dto.LoginDTO; import com.library.pojo.vo.LoginVO;
public interface AuthService { LoginVO login(LoginDTO dto); }
