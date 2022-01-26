package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 兽人国王
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrcCastle extends Castle {

    private final String description;

    public OrcCastle(OrcCastle orcCastle) {
        super(orcCastle);
        this.description = orcCastle.description;
    }

    @Override
    public OrcCastle copy() {
        return new OrcCastle(this);
    }

    @Override
    public String toString() {
        return description;
    }
}
