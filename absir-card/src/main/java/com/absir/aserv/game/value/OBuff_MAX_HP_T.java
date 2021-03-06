/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-5 下午2:34:52
 */
package com.absir.aserv.game.value;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OBuff_MAX_HP_T extends OBuffRound<OCard> {

    // 最大生命提升
    int maxHpT;

    // 积累最大生命提升
    int maxHpTR;

    public int getMaxHpT() {
        return maxHpT;
    }

    public void setMaxHpT(int maxHpT) {
        this.maxHpT = maxHpT;
    }

    @Override
    public void stepRound(OCard object, long time, int round, IResult result) {
        maxHpTR += maxHpT;
        int maxHp = object.getMaxHp();
        object.setMaxHp((int) object.getBuffAtt("maxHp", object.baseHp(), maxHpT));
        maxHp -= maxHp;
        if (maxHp > 0) {
            object.damage(null, maxHp, null, result);

        } else {
            object.treat(null, -maxHp, null, result);
        }
    }

    @Override
    public void revert(OCard object, IResult result) {
        object.setMaxHp((int) object.getBuffAtt("maxHp", object.baseHp(), -maxHpTR));
        if (object.getHp() > object.getMaxHp()) {
            object.setHp(object.getMaxHp());
        }
    }

    @Override
    public void effect(OCard object, IResult result) {
    }

}
