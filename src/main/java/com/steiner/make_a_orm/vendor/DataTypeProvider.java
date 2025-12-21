package com.steiner.make_a_orm.vendor;

import jakarta.annotation.Nonnull;

public interface DataTypeProvider {
    @Nonnull
    String booleanType();
    @Nonnull
    String byteType();
    @Nonnull
    String shortType();
    @Nonnull
    String integerType();
    @Nonnull
    String longType();

    @Nonnull
    String integerAutoIncrementType();
    @Nonnull
    String longAutoIncrementType();

    @Nonnull
    String floatType();
    @Nonnull
    String doubleType();

    @Nonnull
    String charType(int length);
    @Nonnull
    String varcharType(int length);
    @Nonnull
    String textType();

    @Nonnull
    String timestampType();
    @Nonnull
    String dateTimeType(); // the same as timestamp Type
    @Nonnull
    String timeType();
    @Nonnull
    String dateType();
}
