package com.steiner.make_a_orm.column.numeric;

import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.column.trait.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;

public final class DecimalColumn extends Column<BigDecimal>
        implements
        IAutoIncrementColumn<BigDecimal, DecimalColumn>,
        IDefaultValueColumn<BigDecimal, DecimalColumn>,
        IEqualColumn<BigDecimal, DecimalColumn>,
        ICompareColumn<BigDecimal, DecimalColumn>,
        IBetweenColumn<BigDecimal, DecimalColumn>,
        IInListColumn<BigDecimal, DecimalColumn>,
        INullOrNotColumn<BigDecimal, DecimalColumn>,
        IPlusColumn<BigDecimal, DecimalColumn>,
        IMinusColumn<BigDecimal, DecimalColumn>,
        IMulDivColumn<BigDecimal, DecimalColumn> {
    public int precision;
    public int scale;

    private static void validate(@Nonnull BigDecimal value, int precision, int scale) throws SQLException {
        if (value.precision() > precision) {
            throw new SQLException("总位数超出定义");
        }

        if (value.scale() > scale) {
            throw new SQLException("小数部分超出定义");
        }

        int integerDigits = value.precision() - value.scale();
        int maxIntegerDigits = precision - scale;
        if (integerDigits > maxIntegerDigits) {
            throw new SQLException("整数部分超出定义");
        }
    }

    public DecimalColumn(String name, int precision, int scale) {
        super(name);
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public void inject(@Nonnull PreparedStatement statement, int index, @Nonnull BigDecimal value) {
        try {
            validate(value, precision, scale);
            statement.setBigDecimal(index, value);
        } catch (SQLException e) {
            throw new SQLBuildException(e);
        }
    }

    @Nonnull
    @Override
    public String sqlType() {
        return "decimal(%s, %s)".formatted(precision, scale);
    }

    @Nonnull
    @Override
    public String format(@Nonnull BigDecimal value) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(scale); // 设置最大小数位数
        df.setMinimumFractionDigits(scale); // 设置最小小数位数
        df.setMaximumIntegerDigits(precision - scale); // 设置最大整数位数
        return df.format(value);
    }

    @Nonnull
    @Override
    public DecimalColumn self() {
        return this;
    }

    @Override
    public DecimalColumn defaultValue(@Nullable BigDecimal value) {
        IDefaultValueColumn.super.defaultValue(value);
        // validate and throw a SQLBuildException
        if (value != null) {
            try {
                validate(value, precision, scale);
            } catch (SQLException e) {
                throw new SQLBuildException(e);
            }
        }

        return this;
    }
}
