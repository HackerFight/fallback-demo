package com.qiuguan.fallback.handler;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author created by qiuguan on 2021/12/24 21:56
 */
public class RefectionTest {

    public static void main(String[] args) {

        try {
            Student student = Student.class.newInstance();
            Method say = ReflectionUtils.findMethod(Student.class, "say", Throwable.class);
            System.out.println(say);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class Student {

//        public  void say() {
//            System.out.println("无参数的say方法");
//        }

        public void say(Throwable throwable) {
            System.out.println("有参数的say方法：" + throwable);
        }
    }
}
