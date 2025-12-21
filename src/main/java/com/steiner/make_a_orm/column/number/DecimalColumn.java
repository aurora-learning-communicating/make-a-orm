package com.steiner.make_a_orm.column.number;

import com.steiner.make_a_orm.aggregate.Summary;
import com.steiner.make_a_orm.column.trait.aggregate.ISummary;
import com.steiner.make_a_orm.column.trait.predicate.*;
import com.steiner.make_a_orm.vendor.dialect.Dialect;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DecimalColumn extends NumberColumn<BigDecimal>
        implements
        IEqual<BigDecimal, DecimalColumn>,
        ICompare<BigDecimal, DecimalColumn>,
        IBetween<BigDecimal, DecimalColumn>,
        IInList<BigDecimal, DecimalColumn>,
        INullOrNot<BigDecimal, DecimalColumn>,
        ISummary<BigDecimal, BigDecimal> {

    public int precision;
    public int scale;

    public DecimalColumn(@Nonnull String name, @Nonnull Table fromTable, int precision, int scale) {
        super(name, fromTable);

        this.precision = precision;
        this.scale = scale;
    }


    @Override
    @Nonnull
    public String typeQuote(@Nonnull Dialect dialect) {
        return "decimal(%s, %s)".formatted(precision, scale);
    }

    @Override
    public int sqlType() {
        return Types.DECIMAL;
    }

    @Override
    public void write(@Nonnull PreparedStatement statement, int index, @Nonnull BigDecimal value) throws SQLException {
        statement.setBigDecimal(index, value);
    }

    @Nullable
    @Override
    public BigDecimal read(@Nonnull ResultSet resultSet) throws SQLException {
        return resultSet.getBigDecimal(name);
    }

    @Nonnull
    @Override
    public Summary<BigDecimal, BigDecimal> sum() {
        return new Summary<BigDecimal, BigDecimal>(this) {
            @Nullable
            @Override
            public BigDecimal read(@Nonnull ResultSet resultSet) throws SQLException {
                return resultSet.getBigDecimal(alias());
            }
        };
    }

    @Nonnull
    @Override
    public DecimalColumn self() {
        return this;
    }
}
