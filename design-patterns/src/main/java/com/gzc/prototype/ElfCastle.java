package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 精灵城堡
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ElfCastle extends Castle {

    private final String description;

    public ElfCastle(ElfCastle elfCastle) {
        super(elfCastle);
        this.description = elfCastle.description;
    }

    @Override
    public ElfCastle copy() {
        return new ElfCastle(this);
    }

    @Override
    public String toString() {
        return description;
    }
}
