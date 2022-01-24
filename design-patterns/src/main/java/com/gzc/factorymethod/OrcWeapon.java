package com.gzc.factorymethod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 兽人武器
 */
@RequiredArgsConstructor
@Getter
public class OrcWeapon implements Weapon {

    private final WeaponType weaponType;

    @Override
    public String toString() {
        return "一把兽人" + weaponType;
    }
}
