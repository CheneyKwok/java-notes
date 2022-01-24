package com.gzc.factorymethod;

import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * 兽人武器工厂
 *
 * 制造兽人所需的各种武器
 */

public class OrcWeaponFactory implements WeaponFactory {

    private static final Map<WeaponType, Weapon> ORC_ARSENAL;

    static {
        ORC_ARSENAL = new EnumMap<>(WeaponType.class);
        Arrays.stream(WeaponType.values()).forEach(e -> ORC_ARSENAL.put(e, new OrcWeapon(e)));
    }

    @Override
    public Weapon manufactureWeapon(WeaponType weaponType) {
        return ORC_ARSENAL.get(weaponType);
    }

    @Override
    public String toString() {
        return "兽人武器工厂";
    }
}
