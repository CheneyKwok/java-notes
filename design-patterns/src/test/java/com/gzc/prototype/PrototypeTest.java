package com.gzc.prototype;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class PrototypeTest<P extends Prototype> {

    static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.of(new ElfArmy("这是一只精灵军队"), "这是一只精灵军队"),
                Arguments.of(new ElfKing("这是一位精灵国王"), "这是一位精灵国王"),
                Arguments.of(new ElfCastle("这是一座精灵城堡"), "这是一座精灵城堡"),
                Arguments.of(new OrcArmy("这是一只兽人军队"), "这是一只兽人军队"),
                Arguments.of(new OrcKing("这是一位兽人国王"), "这是一位兽人国王"),
                Arguments.of(new OrcCastle("这是一座兽人城堡"), "这是一座兽人城堡")
        );
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void testPrototype(P testedPrototype, String expectedToString) {
        Assertions.assertEquals(expectedToString, testedPrototype.toString());

        Object clone = testedPrototype.copy();
        Assertions.assertNotNull(clone);
        Assertions.assertEquals(clone, testedPrototype);
        Assertions.assertNotSame(clone, testedPrototype);
        Assertions.assertSame(clone.getClass(), testedPrototype.getClass());
    }


}
