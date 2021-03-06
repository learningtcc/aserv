/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-4 下午5:17:18
 */
package com.absir.aserv.game.value;

import com.absir.core.kernel.KernelClass;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class OBuffFrom<T, O extends OObject> extends OBuff<O> implements IBuffFrom<T> {

    private Class<T> formType;

    public OBuffFrom() {
        formType = KernelClass.typeClass(getClass(), IBuffFrom.T_VARIABLE);
    }

    @Override
    public boolean supportsFrom(Object from) {
        return from == null ? isFromNullable() : formType.isAssignableFrom(formType.getClass());
    }

    public boolean isFromNullable() {
        return false;
    }
}
