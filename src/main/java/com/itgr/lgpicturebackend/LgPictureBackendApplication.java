package com.itgr.lgpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.itgr.lgpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class LgPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LgPictureBackendApplication.class, args);
    }

}
