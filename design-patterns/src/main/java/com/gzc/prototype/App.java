package com.gzc.prototype;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {
        log.info("精灵王国");
        KingdomFactory factory = new kingdomFactoryImpl(
                new ElfArmy("这是一只精灵军队"),
                new ElfKing("这是一位精灵国王"),
                new ElfCastle("这是一座精灵城堡"));
        King king = factory.createKing();
        Army army = factory.createArmy();
        Castle castle = factory.createCastle();
        log.info(king.toString());
        log.info(army.toString());
        log.info(castle.toString());

        log.info("兽人王国");
        factory = new kingdomFactoryImpl(
                new OrcArmy("这是一只兽人军队"),
                new OrcKing("这是一位兽人国王"),
                new OrcCastle("这是一座兽人城堡"));
        king = factory.createKing();
        army = factory.createArmy();
        castle = factory.createCastle();
        log.info(king.toString());
        log.info(army.toString());
        log.info(castle.toString());

    }
}
