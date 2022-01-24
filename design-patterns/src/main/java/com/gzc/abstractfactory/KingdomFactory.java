package com.gzc.abstractfactory;

/**
 * 王国工厂接口
 */
public interface KingdomFactory {

    Castle createCastle();

    King createKing();

    Army createArmy();
}
