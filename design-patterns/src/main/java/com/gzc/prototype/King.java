package com.gzc.prototype;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 国王
 */
@EqualsAndHashCode
@NoArgsConstructor
public abstract class King implements Prototype {
    public King(King source) {

    }

    @Override
    public abstract King copy();
}
