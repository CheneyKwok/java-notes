package com.gzc.factorymethod;

/**
 * 精灵武器
 */
public class ElfWeapon implements Weapon{

    private final WeaponType weaponType;

    public ElfWeapon(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    @Override
    public WeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public String toString() {
        return "一把精灵" + weaponType;
    }
}
