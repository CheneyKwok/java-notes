package com.gzc.abstractfactory;

import lombok.Getter;

import java.util.function.Supplier;

/**
 * 王国工厂枚举
 */
@Getter
public enum KingdomFactoryType {

    ELF(ElfKingdomFactory::new),
    ORC(OrcKingdomFactory::new);

    private final Supplier<KingdomFactory> constructor;

    KingdomFactoryType(Supplier<KingdomFactory> constructor) {
        this.constructor = constructor;
    }
}
