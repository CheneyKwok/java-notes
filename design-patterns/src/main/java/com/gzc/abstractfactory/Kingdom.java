package com.gzc.abstractfactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 王国类
 */
@Getter
@Setter
@EqualsAndHashCode
public class Kingdom {

    private Castle castle;

    private King king;

    private Army army;

    public static class FactoryMaker {
        public static KingdomFactory make(KingdomFactoryType type) {
            return type.getConstructor().get();
        }
    }

    @Override
    public String toString() {
        return castle.getDescription() + " " + king.getDescription() + " " + army.getDescription();
    }
}
