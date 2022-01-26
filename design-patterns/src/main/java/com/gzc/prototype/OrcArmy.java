package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 兽人军队
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrcArmy extends Army {

    private final String description;

    public OrcArmy(OrcArmy orcArmy) {
        super(orcArmy);
        this.description = orcArmy.description;
    }

    @Override
    public OrcArmy copy() {
        return new OrcArmy(this);
    }

    @Override
    public String toString() {
        return description;
    }
}
