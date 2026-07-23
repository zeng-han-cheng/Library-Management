package com.library.service;

import com.library.common.model.PageResult;
import com.library.model.dto.BorrowDTO;
import com.library.model.dto.ReturnDTO;
import com.library.model.vo.BorrowVO;

import java.util.List;
import java.util.Map;

public interface BorrowService {
    void borrow(BorrowDTO request);
    void returnBook(ReturnDTO request);
    PageResult<BorrowVO> page(Long readerId, int page, int size);
    Map<String, Object> overview(Long readerId);
    List<BorrowVO> timeline(Long readerId);
}
