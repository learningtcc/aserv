/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-26 下午4:39:08
 */
package com.absir.async;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopProxyHandler;
import com.absir.context.core.ContextUtils;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class AsyncRunnable {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AsyncRunnable.class);

    protected long timeout;

    protected boolean thread;

    public AsyncRunnable(long timeout, boolean thread) {
        this.timeout = timeout;
        this.thread = thread;
    }

    public void aysnc(final Object proxy, final Iterator<AopInterceptor> iterator, final AopProxyHandler proxyHandler,
                      final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
        aysncRun(new Runnable() {

            @Override
            public void run() {
                try {
                    proxyHandler.invoke(proxy, iterator, method, args, methodProxy);

                } catch (Throwable e) {
                    LOGGER.error("async run", e);
                }
            }
        });
    }

    public void aysncRun(Runnable runnable) {
        if (timeout > 0 || thread) {
            final Thread doThread = new Thread(runnable);
            doThread.setName("asyncRun");
            doThread.setDaemon(true);
            doThread.start();
            if (timeout > 0) {
                Runnable timeoutRunnable = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(timeout);
                            doThread.interrupt();

                        } catch (InterruptedException e) {
                        }
                    }
                };

                if (thread) {
                    Thread thread = new Thread(timeoutRunnable);
                    thread.setName("asyncRun.timeout");
                    thread.setDaemon(true);
                    thread.start();

                } else {
                    ContextUtils.getThreadPoolExecutor().execute(timeoutRunnable);
                }
            }

        } else {
            ContextUtils.getThreadPoolExecutor().execute(runnable);
        }
    }
}
