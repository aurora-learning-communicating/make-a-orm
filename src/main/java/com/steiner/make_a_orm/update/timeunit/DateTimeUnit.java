package com.steiner.make_a_orm.update.timeunit;

public enum DateTimeUnit {
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
    DateTimeUnit(String unit) {
        this.unit = unit;
    }
}
