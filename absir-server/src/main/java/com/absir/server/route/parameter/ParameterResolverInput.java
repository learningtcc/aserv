/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-16 下午5:12:04
 */
package com.absir.server.route.parameter;

import com.absir.bean.inject.value.Bean;
import com.absir.core.dyna.DynaBinder;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.RouteMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Bean
public class ParameterResolverInput implements ParameterResolver<Object> {

    @Override
    public Object getParameter(int i, String[] parameterNames, Class<?>[] parameterTypes, Annotation[][] annotations, Method method) {
        return Input.class.isAssignableFrom(parameterTypes[i]) ? Boolean.TRUE : null;
    }

    @Override
    public Object getParameterValue(OnPut onPut, Object parameter, Class<?> parameterType, String beanName, RouteMethod routeMethod) {
        return DynaBinder.to(onPut.getInput(), parameterType);
    }
}
