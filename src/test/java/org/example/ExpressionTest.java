package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {
    @Test
    public void testWrongFormat() throws Exception {
        Expression calc = new Expression();

        Exception e = assertThrows(
                Exception.class,
                () -> calc.validate("!5 + 5")
        );

        assertEquals("Wrong format. Allowed symbols: a-z, A-Z, 0-9, +, -, *, /, ^", e.getMessage());

        assertTrue(calc.validate("(a * b) / (12 + 14) ^ (3 - 5)"));
    }

    @Test
    public void testActions() throws Exception {
        Expression calc = new Expression();

        assertEquals(0, calc.getresult("0 + 0"));
        assertEquals(1, calc.getresult("0 + 1"));
        assertEquals(1, calc.getresult("1 + 0"));
        assertEquals(2, calc.getresult("1 + 1"));

        assertEquals(0, calc.getresult("0 - 0"));
        assertEquals(-1, calc.getresult("0 - 1"));
        assertEquals(1, calc.getresult("1 - 0"));
        assertEquals(0, calc.getresult("1 - 1"));

        assertEquals(0, calc.getresult("0 * 0"));
        assertEquals(0, calc.getresult("0 * 2"));
        assertEquals(0, calc.getresult("2 * 0"));
        assertEquals(4, calc.getresult("2 * 2"));

        Exception e = assertThrows(
                Exception.class,
                () -> calc.getresult("0 / 0")
        );

        assertEquals("Dividing by zero", e.getMessage());

        e = assertThrows(
                Exception.class,
                () -> calc.getresult("2 / 0")
        );

        assertEquals("Dividing by zero", e.getMessage());

        assertEquals(0, calc.getresult("0 / 2"));
        assertEquals(1, calc.getresult("2 / 2"));
        assertEquals(2, calc.getresult("8 / 2 / 2"));

        assertEquals(256, calc.getresult("2 ^ 8"));

        assertEquals(1, calc.getresult("(899 + 1) / (3 * 10) ^ (3 - 1)"));
    }
}
