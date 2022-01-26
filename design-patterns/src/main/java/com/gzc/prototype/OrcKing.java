package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 兽人国王
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrcKing extends King {

    private final String description;

    public OrcKing(OrcKing orcKing) {
        super(orcKing);
        this.description = orcKing.description;
    }

    @Override
    public OrcKing copy() {
        return new OrcKing(this);
    }

    @Override
    public String toString() {
        return description;
    }
}
