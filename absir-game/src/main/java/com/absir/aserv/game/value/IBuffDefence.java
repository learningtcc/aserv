/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-4 下午4:46:23
 */
package com.absir.aserv.game.value;

public interface IBuffDefence<T> extends IBuffFrom<T> {

    public int defence(int atk, T damageFrom, IResult result);

}
