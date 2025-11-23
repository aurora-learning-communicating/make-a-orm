package com.steiner.make_a_orm.unit.time;

import jakarta.annotation.Nonnull;

public enum DateTime {
    Year("year"),
    Quarter("quarter"),
    Month("month"),
    Week("week"),
    Day("day"),

    Hour("hour"),
    Minute("minute"),
    Second("second"),
    MicroSecond("microsecond");

    public final String unit;
    DateTime(@Nonnull String unit) {
        this.unit = unit;
    }
}
