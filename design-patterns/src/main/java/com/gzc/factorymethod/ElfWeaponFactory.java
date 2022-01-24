package com.gzc.factorymethod;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * 精灵武器工厂
 *
 * 制造精灵所需的各种武器
 */
public class ElfWeaponFactory implements WeaponFactory {

    private static final Map<WeaponType, Weapon> ELF_ARSENAL;

    static {
        ELF_ARSENAL = new EnumMap<>(WeaponType.class);
        Arrays.stream(WeaponType.values()).forEach(e -> ELF_ARSENAL.put(e, new ElfWeapon(e)));
    }

    @Override
    public Weapon manufactureWeapon(WeaponType weaponType) {
        return ELF_ARSENAL.get(weaponType);
    }

    @Override
    public String toString() {
        return "精灵武器工厂";
    }
}
