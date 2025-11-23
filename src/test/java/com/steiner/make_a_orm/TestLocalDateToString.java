package com.steiner.make_a_orm;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class TestLocalDateToString {
    @Test
    public void testFormat() {
        LocalDate localDate = LocalDate.of(2025, 10, 10);
        System.out.println(localDate.toString());
    }
}
