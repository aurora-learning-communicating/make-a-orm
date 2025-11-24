package com.steiner.make_a_orm.unit;

import jakarta.annotation.Nonnull;

public class TimeUnit {
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
}
