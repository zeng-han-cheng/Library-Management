package com.library.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.library")
@MapperScan("com.library.server.mapper")
public class LibraryApplication { public static void main(String[] args) { SpringApplication.run(LibraryApplication.class,args); } }
