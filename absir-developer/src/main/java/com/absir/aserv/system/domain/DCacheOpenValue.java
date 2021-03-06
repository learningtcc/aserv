/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月19日 上午10:17:04
 */
package com.absir.aserv.system.domain;

import com.absir.aserv.system.bean.value.JiOpenValue;

public class DCacheOpenValue<V, K extends JiOpenValue<V>> extends DCache<K, V> {

    public DCacheOpenValue(Class<K> entityClass, String entityName) {
        super(entityClass, entityName);
    }

    protected String genReloadHsql() {
        return "SELECT o FROM " + entityName + " o WHERE o.open = true";
    }

    @Override
    protected V getCacheValue(K entity) {
        if (!entity.isOpen()) {
            return null;
        }

        return entity.forValue();
    }

}
