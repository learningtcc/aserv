/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-27 下午4:37:48
 */
package com.absir.aserv.configure.xls;

import com.absir.core.kernel.KernelDyna;

import java.io.Serializable;
import java.util.Collection;

public abstract class XlsDao<T, ID extends Serializable> {

    private Class<ID> idType;

    public XlsDao(Class<ID> idType) {
        this.idType = idType;
    }

    public Class<ID> getIdType() {
        return idType;
    }

    public abstract T get(ID id);

    public abstract Collection<T> getAll();

    public T find(Object id) {
        ID identifier = KernelDyna.to(id, idType);
        return identifier == null ? null : get(identifier);
    }
}
