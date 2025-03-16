package com.steiner.make_a_orm.update.timeunit;

public enum DateUnit {
    Year("year"),
    Quarter("quarter"),
    Month("month"),
    Week("week"),
    Day("day");

    public final String unit;
    DateUnit(String unit) {
        this.unit = unit;
    }
}
