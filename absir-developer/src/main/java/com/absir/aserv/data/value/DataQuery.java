/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-13 下午5:16:36
 */
package com.absir.aserv.data.value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataQuery {

    String value();

    boolean nativeQuery() default false;

    boolean cacheable() default false;

    Class<?> excuteType() default void.class;

    Class<?> aliasType() default void.class;
}
