package com.qiuguan.fallback.biz.service;

import com.qiuguan.fallback.ann.Fallback;
import org.springframework.stereotype.Service;



/**
 * @author created by qiuguan on 2021/12/23 18:05
 */
@Service
public class UserService {

    @Fallback(value = ArithmeticException.class)
//    @Fallback
    public void runTask(boolean isSuccess) {
        if (isSuccess) {
            System.out.println("任务正常执行成功");
            return;
        }

        throw new RuntimeException("run task fail ........");
    }

    public void fallback(Exception throwable){
        System.out.println("fallback.............successfully; ex: " + throwable);
    }
}
