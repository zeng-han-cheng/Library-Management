package com.library.service; import com.library.model.dto.LoginDTO; import com.library.model.vo.LoginVO;
public interface AuthService { LoginVO login(LoginDTO dto); }
