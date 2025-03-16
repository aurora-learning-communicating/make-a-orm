package com.steiner.make_a_orm.update.timeunit;

public enum TimeUnit {
    Hour("hour"),
    Minute("minute"),
    Second("second"),
    MicroSecond("microsecond");

    public final String unit;
    TimeUnit(String unit) {
        this.unit = unit;
    }
}
