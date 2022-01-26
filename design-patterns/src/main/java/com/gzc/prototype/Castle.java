package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 城堡
 */
@EqualsAndHashCode
@NoArgsConstructor
public abstract class Castle implements Prototype{

    public Castle(Castle source) {

    }

    @Override
    public abstract Castle copy();
}
