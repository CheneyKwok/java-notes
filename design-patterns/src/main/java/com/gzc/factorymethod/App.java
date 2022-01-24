package com.gzc.factorymethod;

import lombok.extern.slf4j.Slf4j;

/**
 * 工厂方法
 *
 * 工厂方法是一种创建型设计模式，它使用工厂方法来处理创建对象的问题，而无需指定将要创建的对象的确切类。
 * 定义一个用于创建对象的接口，但让子类去决定要实例化哪个对象，即提供了一种将对象的实例化逻辑委托为子类的方法。
 * WeaponFactory (接口)
 * WeaponFactory.manufactureWeapon() (创建对象的方法)
 * OrcWeaponFactory、ElfWeaponFactory 具体的实现类去覆盖 manufactureWeapon() 方法已提供相对应的对象创建
 *
 */
@Slf4j
public class App {

    private static final String MANUFACTURED = "{} 制造 {}";

    public static void main(String[] args) {
        WeaponFactory weaponFactory = new ElfWeaponFactory();
        Weapon weapon = weaponFactory.manufactureWeapon(WeaponType.AXE);
        log.info(MANUFACTURED, weaponFactory, weapon);
        weapon = weaponFactory.manufactureWeapon(WeaponType.SHORT_SWORD);
        log.info(MANUFACTURED, weaponFactory, weapon);


        weaponFactory = new OrcWeaponFactory();
        weapon = weaponFactory.manufactureWeapon(WeaponType.SHORT_SWORD);
        log.info(MANUFACTURED, weaponFactory, weapon);
        weapon = weaponFactory.manufactureWeapon(WeaponType.SPEAR);
        log.info(MANUFACTURED, weaponFactory, weapon);

    }
}
