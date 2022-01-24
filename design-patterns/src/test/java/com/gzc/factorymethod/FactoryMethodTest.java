package com.gzc.factorymethod;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryMethodTest {

    @Test
    void testOrcWeaponFactoryWithSpear() {
        WeaponFactory weaponFactory = new OrcWeaponFactory();
        Weapon weapon = weaponFactory.manufactureWeapon(WeaponType.SPEAR);
        verifyWeapon(weapon, WeaponType.SPEAR, OrcWeapon.class);
    }

    @Test
    void testOrcWeaponFactoryWithAxe() {
        WeaponFactory weaponFactory = new OrcWeaponFactory();
        Weapon weapon = weaponFactory.manufactureWeapon(WeaponType.AXE);
        verifyWeapon(weapon, WeaponType.AXE, OrcWeapon.class);
    }

    @Test
    void testElfWeaponFactoryWithShortSword() {
        WeaponFactory weaponFactory = new ElfWeaponFactory();
        Weapon weapon = weaponFactory.manufactureWeapon(WeaponType.SHORT_SWORD);
        verifyWeapon(weapon, WeaponType.SHORT_SWORD, ElfWeapon.class);
    }

    @Test
    void testElfWeaponFactoryWithSpear() {
        WeaponFactory weaponFactory = new ElfWeaponFactory();
        Weapon weapon = weaponFactory.manufactureWeapon(WeaponType.SPEAR);
        verifyWeapon(weapon, WeaponType.SPEAR, ElfWeapon.class);
    }

    /**
     * 验证传递的武器对象使指定 clazz 的实例并且武器类型是期待的武器类型
     *
     * @param weapon 待验证的武器对象
     * @param expectedWeaponType 期待的武器类型
     * @param clazz 期待的武器类
     */
    private void verifyWeapon(Weapon weapon, WeaponType expectedWeaponType, Class<?> clazz) {
        Assertions.assertTrue(clazz.isInstance(weapon), "Weapon must be an Object of: " + clazz.getName());
        Assertions.assertEquals(expectedWeaponType, weapon.getWeaponType(), "Weapon must be of weaponType: " + expectedWeaponType);
    }
}
