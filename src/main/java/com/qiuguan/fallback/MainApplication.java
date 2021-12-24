package com.qiuguan.fallback;

import com.qiuguan.fallback.ann.EnableFallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author created by qiuguan on 2021/12/23 16:50
 */
@EnableFallback
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
