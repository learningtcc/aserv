/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-4 下午6:16:05
 */
package com.absir.aserv.game.value;

public interface IBuffAttack<T> extends IBuffFrom<T> {

    public int attack(int atk, T damageFrom, IResult result);

}
