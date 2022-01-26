package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 军队
 */
@EqualsAndHashCode
@NoArgsConstructor
public abstract class Army implements Prototype{

    public Army(Army source) {

    }

    @Override
    public abstract Army copy();
}
