package com.gzc.factorymethod;

/**
 * 武器工厂接口
 */
public interface WeaponFactory {

    /**
     * 制造武器
     */
    Weapon manufactureWeapon(WeaponType weaponType);
}
