package com.library.controller;

import com.library.common.annotation.RequireRole;
import com.library.common.constant.RoleConstant;
import com.library.common.exception.BusinessException;
import com.library.common.model.ApiResult;
import com.library.common.model.LoginUser;
import com.library.common.model.PageResult;
import com.library.model.dto.BorrowDTO;
import com.library.model.dto.ReturnDTO;
import com.library.model.vo.BorrowVO;
import com.library.service.BorrowService;
import com.library.util.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {
    private final BorrowService service;

    public BorrowController(BorrowService service) {
        this.service = service;
    }

    @PostMapping
    @RequireRole({RoleConstant.ADMIN, RoleConstant.READER})
    public ApiResult<Void> borrow(@Valid @RequestBody BorrowDTO request) {
        LoginUser user = UserContext.get();
        Long readerId = RoleConstant.READER.equals(user.getRole()) ? user.getId() : request.readerId();
        if (readerId == null) {
            throw new BusinessException("请选择读者");
        }
        service.borrow(new BorrowDTO(readerId, request.bookId(), request.days(), request.quantity(), request.remark()));
        return ApiResult.success();
    }

    @PutMapping("/return")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.READER})
    public ApiResult<Void> returnBook(@Valid @RequestBody ReturnDTO request) {
        service.returnBook(request);
        return ApiResult.success();
    }

    @GetMapping
    public ApiResult<PageResult<BorrowVO>> page(@RequestParam(required = false) Long readerId,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Long currentReaderId = RoleConstant.READER.equals(UserContext.get().getRole()) ? UserContext.get().getId() : readerId;
        return ApiResult.success(service.page(currentReaderId, page, size));
    }

    @GetMapping("/overview")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.READER})
    public ApiResult<Map<String, Object>> overview() {
        LoginUser user = UserContext.get();
        Long readerId = RoleConstant.READER.equals(user.getRole()) ? user.getId() : null;
        return ApiResult.success(service.overview(readerId));
    }

    @GetMapping("/timeline")
    @RequireRole({RoleConstant.ADMIN, RoleConstant.READER})
    public ApiResult<List<BorrowVO>> timeline(@RequestParam(required = false) Long readerId) {
        Long currentReaderId = RoleConstant.READER.equals(UserContext.get().getRole()) ? UserContext.get().getId() : readerId;
        return ApiResult.success(service.timeline(currentReaderId));
    }
}
