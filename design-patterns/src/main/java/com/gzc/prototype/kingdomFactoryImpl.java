package com.gzc.prototype;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class kingdomFactoryImpl implements KingdomFactory {

    private final Army army;

    private final King king;

    private final Castle castle;

    @Override
    public Army createArmy() {
        return army.copy();
    }

    @Override
    public King createKing() {
        return king.copy();
    }

    @Override
    public Castle createCastle() {
        return castle.copy();
    }
}
