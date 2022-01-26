package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ElfArmy extends Army {

    private final String description;

    public ElfArmy(ElfArmy elfMage) {
        super(elfMage);
        this.description = elfMage.description;
    }
    @Override
    public ElfArmy copy() {
        return new ElfArmy(this);
    }

    @Override
    public String toString() {
        return description;
    }
}
