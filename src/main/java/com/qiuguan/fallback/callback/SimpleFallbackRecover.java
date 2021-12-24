package com.qiuguan.fallback.callback;

import com.qiuguan.fallback.handler.MethodInvocationRecover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author created by qiuguan on 2021/12/24 11:10
 */
public class SimpleFallbackRecover implements FallbackRecover {

    private final Logger logger = LoggerFactory.getLogger(SimpleRecoverCallback.class);

    private FallbackPolicy fallbackPolicy;

    public SimpleFallbackRecover(FallbackPolicy fallbackPolicy) {
        this.fallbackPolicy = fallbackPolicy;
    }

    @Override
    public <T, E extends Throwable> T recover(FallbackCallback<T, E> fallbackCallback) throws Throwable {
        return recover(fallbackCallback, null);
    }

    @Override
    public <T, E extends Throwable> T recover(FallbackCallback<T, E> fallbackCallback, RecoverCallback<T> recoverCallback) throws Throwable {

        FallbackPolicy fallbackPolicy = this.fallbackPolicy;
        Throwable t = null;

        FallbackContext fallbackContext = fallbackPolicy.getFallbackContext();

        try {

            return fallbackCallback.doExecute(fallbackContext);

        } catch (Throwable e) {
            t = e;

            try {
                if (fallbackPolicy.canFallback(t)) {
                    fallbackContext.registerThrowable(t);
                }
            } catch (Throwable ex) {
                throw new IllegalStateException("register throwable error");
            }

        } finally {
            //doClose();
        }

        return ifNecessaryWrap(recoverCallback, fallbackContext);
    }

    private <T> T ifNecessaryWrap(RecoverCallback<T> recoverCallback, FallbackContext fallbackContext) throws Throwable {

        Throwable t = fallbackContext.getThrowable();
        if (recoverCallback == null || t == null){
            throw new IllegalStateException("cloud not fallback....");
        }

        T recover = recoverCallback.recover(fallbackContext);
        return recover;

    }


    public FallbackPolicy getFallbackPolicy() {
        return fallbackPolicy;
    }

    public void setFallbackPolicy(FallbackPolicy fallbackPolicy) {
        this.fallbackPolicy = fallbackPolicy;
    }
}
