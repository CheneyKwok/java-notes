package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 精灵国王
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ElfKing extends King {

    private final String description;

    public ElfKing(ElfKing elfBeast) {
        super(elfBeast);
        this.description = elfBeast.description;
    }

    @Override
    public ElfKing copy() {
        return new ElfKing(this);
    }

    @Override
    public String toString() {
        return description;
    }
}
