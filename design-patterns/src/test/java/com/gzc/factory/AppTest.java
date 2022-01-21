package com.gzc.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    void shouldExecutedWithoutException() {
        Assertions.assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
