package com.library.service.impl;

import com.library.common.constant.RoleConstant;
import com.library.common.exception.BusinessException;
import com.library.common.model.LoginUser;
import com.library.common.model.PageResult;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.mapper.ReaderMapper;
import com.library.model.dto.BorrowDTO;
import com.library.model.dto.ReturnDTO;
import com.library.model.entity.Book;
import com.library.model.entity.BorrowRecord;
import com.library.model.entity.Reader;
import com.library.model.vo.BorrowVO;
import com.library.service.BorrowService;
import com.library.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BorrowServiceImpl implements BorrowService {
    private final BorrowMapper borrowMapper;
    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;

    public BorrowServiceImpl(BorrowMapper borrowMapper, BookMapper bookMapper, ReaderMapper readerMapper) {
        this.borrowMapper = borrowMapper;
        this.bookMapper = bookMapper;
        this.readerMapper = readerMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrow(BorrowDTO request) {
        LoginUser user = UserContext.get();
        if (user != null && RoleConstant.READER.equals(user.getRole()) && !user.getId().equals(request.readerId())) {
            throw new BusinessException("读者不能代替其他读者借书");
        }

        Reader reader = readerMapper.findById(request.readerId());
        if (reader == null || !Integer.valueOf(1).equals(reader.getStatus())) {
            throw new BusinessException("读者不存在或已被禁用");
        }
        int quantity = request.quantity() == null ? 1 : request.quantity();
        int maxBorrow = reader.getMaxBorrow() == null ? 5 : reader.getMaxBorrow();
        int borrowingCount = readerMapper.countBorrowing(request.readerId());
        if (borrowingCount + quantity > maxBorrow) {
            throw new BusinessException("借阅数量超过上限，当前还可借" + Math.max(maxBorrow - borrowingCount, 0) + "本");
        }

        Book book = bookMapper.findById(request.bookId());
        int availableStock = book == null || book.getAvailableStock() == null ? 0 : book.getAvailableStock();
        if (availableStock < quantity) {
            throw new BusinessException("可借库存不足，当前剩余" + availableStock + "本");
        }
        int days = request.days() == null ? 30 : request.days();
        LocalDateTime dueTime = LocalDateTime.now().plusDays(days);
        for (int index = 0; index < quantity; index++) {
            if (bookMapper.decreaseStock(request.bookId()) != 1) {
                throw new BusinessException("库存更新失败，请重试");
            }
            BorrowRecord record = new BorrowRecord();
            record.setReaderId(request.readerId());
            record.setBookId(request.bookId());
            record.setDueTime(dueTime);
            record.setStatus(1);
            record.setRemark(request.remark());
            record.setOperatorId(user != null && RoleConstant.ADMIN.equals(user.getRole()) ? user.getId() : null);
            if (borrowMapper.insert(record) != 1) {
                throw new BusinessException("借阅记录保存失败");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(ReturnDTO request) {
        BorrowRecord record = borrowMapper.findById(request.recordId());
        LoginUser user = UserContext.get();
        if (record == null || !Integer.valueOf(1).equals(record.getStatus())) {
            throw new BusinessException("借阅记录不存在或已经归还");
        }
        if (user != null && RoleConstant.READER.equals(user.getRole()) && !user.getId().equals(record.getReaderId())) {
            throw new BusinessException("不能归还其他读者的记录");
        }
        record.setStatus(request.status());
        record.setReturnTime(LocalDateTime.now());
        record.setRemark(request.remark());
        if (borrowMapper.returnBook(record) != 1 || bookMapper.increaseStock(record.getBookId()) != 1) {
            throw new BusinessException("归还失败，请重试");
        }
    }

    @Override
    public PageResult<BorrowVO> page(Long readerId, int page, int size) {
        return new PageResult<>(borrowMapper.count(readerId), page, size, borrowMapper.page(readerId, (page - 1) * size, size));
    }

    @Override
    public Map<String, Object> overview(Long readerId) {
        Map<String, Object> result = new HashMap<>();
        result.put("borrowingCount", readerId == null ? 0 : readerMapper.countBorrowing(readerId));
        result.put("recordCount", readerId == null ? 0 : borrowMapper.count(readerId));
        return result;
    }

    @Override
    public List<BorrowVO> timeline(Long readerId) {
        return borrowMapper.timeline(readerId);
    }
}
