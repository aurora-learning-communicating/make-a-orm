package com.steiner.make_a_orm.unit.time;

import jakarta.annotation.Nonnull;

public enum Date {
    Year("year"),
    Quarter("quarter"),
    Month("month"),
    Week("week"),
    Day("day");

    public final String unit;
    Date(@Nonnull String unit) {
        this.unit = unit;
    }
}
