package com.qiuguan.fallback;

import com.qiuguan.fallback.biz.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author created by qiuguan on 2021/12/24 17:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MainTest {

    @Autowired
    private UserService userService;

    @Test
    public void test(){
        userService.runTask(false);
    }
}
