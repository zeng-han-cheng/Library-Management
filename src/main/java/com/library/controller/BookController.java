package com.library.controller;

import com.library.common.annotation.RequireRole;
import com.library.common.constant.RoleConstant;
import com.library.common.exception.BusinessException;
import com.library.common.model.ApiResult;
import com.library.common.model.PageResult;
import com.library.model.dto.BookQuery;
import com.library.model.entity.Book;
import com.library.model.vo.BookVO;
import com.library.service.BookService;
import com.library.util.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResult<PageResult<BookVO>> page(BookQuery query) {
        return ApiResult.success(service.page(query));
    }

    @GetMapping("/{id}")
    public ApiResult<Book> get(@PathVariable Long id) {
        return ApiResult.success(service.get(id));
    }

    @GetMapping("/available-overview")
    public ApiResult<Map<String, Object>> availableOverview() {
        return ApiResult.success(service.availableOverview());
    }

    @PostMapping
    @RequireRole(RoleConstant.ADMIN)
    public ApiResult<Void> save(@Valid @RequestBody Book book) {
        service.save(book);
        return ApiResult.success();
    }

    @PutMapping
    @RequireRole(RoleConstant.ADMIN)
    public ApiResult<Void> update(@Valid @RequestBody Book book) {
        service.update(book);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole(RoleConstant.ADMIN)
    public ApiResult<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResult.success();
    }

    @GetMapping("/stats")
    @RequireRole(RoleConstant.ADMIN)
    public ApiResult<List<Map<String, Object>>> stats() {
        if (UserContext.get() == null || !RoleConstant.ADMIN.equals(UserContext.get().getRole())) {
            throw new BusinessException("仅管理员可以查看统计分析");
        }
        return ApiResult.success(service.stats());
    }

    /** Returns the live inventory metrics displayed on the administrator dashboard. */
    @GetMapping("/overview")
    @RequireRole(RoleConstant.ADMIN)
    public ApiResult<Map<String, Object>> overview() {
        return ApiResult.success(service.overview());
    }

    /** Recalculates available inventory from active borrowing records. */
    @PutMapping("/inventory/repair")
    @RequireRole(RoleConstant.ADMIN)
    public ApiResult<Void> repairInventory() {
        service.repairInventory();
        return ApiResult.success();
    }
}
