package com.steiner.make_a_orm.vendor.dialect.postgresql;

import com.steiner.make_a_orm.vendor.DataTypeProvider;
import jakarta.annotation.Nonnull;

public class PostgreSQLDataTypeProvider implements DataTypeProvider {
    public PostgreSQLDataTypeProvider() {

    }


    @Nonnull
    @Override
    public String booleanType() {
        return "boolean";
    }

    @Nonnull
    @Override
    public String byteType() {
        return "smallint";
    }

    @Nonnull
    @Override
    public String shortType() {
        return "smallint";
    }

    @Nonnull
    @Override
    public String integerType() {
        return "integer";
    }

    @Nonnull
    @Override
    public String longType() {
        return "bigint";
    }

    @Nonnull
    @Override
    public String integerAutoIncrementType() {
        return "serial";
    }

    @Nonnull
    @Override
    public String longAutoIncrementType() {
        return "bigserial";
    }

    @Nonnull
    @Override
    public String floatType() {
        return "real";
    }

    @Nonnull
    @Override
    public String doubleType() {
        return "double precision";
    }

    @Nonnull
    @Override
    public String charType(int length) {
        return "character";
    }

    @Nonnull
    @Override
    public String varcharType(int length) {
        return "character varying";
    }

    @Nonnull
    @Override
    public String textType() {
        return "text";
    }

    @Nonnull
    @Override
    public String timestampType() {
        return "timestamp";
    }

    @Nonnull
    @Override
    public String dateTimeType() {
        return this.timestampType();
    }

    @Nonnull
    @Override
    public String timeType() {
        return "time";
    }

    @Nonnull
    @Override
    public String dateType() {
        return "date";
    }
}
