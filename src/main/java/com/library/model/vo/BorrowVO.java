package com.library.model.vo;
import com.library.model.entity.BorrowRecord;
public class BorrowVO extends BorrowRecord { private String bookTitle,readerName; public String getBookTitle(){return bookTitle;} public void setBookTitle(String v){bookTitle=v;} public String getReaderName(){return readerName;} public void setReaderName(String v){readerName=v;} }
