package com.qiuguan.fallback.callback;

import com.qiuguan.fallback.handler.MethodInvocationRecover;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author created by qiuguan on 2021/12/24 15:41
 */
public class SimpleRecoverCallback implements RecoverCallback<Object> {

    private Object[] args;

    private final MethodInvocationRecover<?> recover;


    public SimpleRecoverCallback(Object[] args, MethodInvocationRecover<?> recover) {
        this.args = args;
        this.recover = recover;
    }

    @Override
    public Object recover(FallbackContext fallbackContext) {
        return recover.recover(this.args, fallbackContext.getThrowable());
    }

}
