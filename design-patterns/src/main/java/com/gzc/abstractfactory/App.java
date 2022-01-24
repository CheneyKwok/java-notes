package com.gzc.abstractfactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 抽象工厂模式
 *
 * 为访问类提供一组用于创建相关或依赖对象的接口，且无需指定它们的具体类
 */
@Slf4j
public class App implements Runnable {

    private final Kingdom kingdom = new Kingdom();

    public Kingdom getKingdom() {
        return kingdom;
    }

    @Override
    public void run() {
        log.info("精灵王国");
        createKingdom(KingdomFactoryType.ELF);
        log.info(kingdom.toString());

        log.info("兽人王国");
        createKingdom(KingdomFactoryType.ORC);
        log.info(kingdom.toString());
    }


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void createKingdom(KingdomFactoryType factoryType) {
        KingdomFactory kingdomFactory = Kingdom.FactoryMaker.make(factoryType);
        kingdom.setCastle(kingdomFactory.createCastle());
        kingdom.setKing(kingdomFactory.createKing());
        kingdom.setArmy(kingdomFactory.createArmy());
    }
}
