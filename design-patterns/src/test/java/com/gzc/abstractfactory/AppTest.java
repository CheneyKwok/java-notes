package com.gzc.abstractfactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    void shouldExecuteWithoutException() {
        Assertions.assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
