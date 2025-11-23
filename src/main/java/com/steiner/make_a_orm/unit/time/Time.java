package com.steiner.make_a_orm.unit.time;

import jakarta.annotation.Nonnull;

public enum Time {
    Hour("hour"),
    Minute("minute"),
    Second("second"),
    MicroSecond("microsecond");

    public final String unit;

    Time(@Nonnull String unit) {
        this.unit = unit;
    }
}
