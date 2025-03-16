package com.steiner.make_a_orm.column;

import com.steiner.make_a_orm.column.constraint.AbstractConstraint;
import com.steiner.make_a_orm.column.constraint.ConstraintType;
import com.steiner.make_a_orm.column.constraint.impl.*;
import com.steiner.make_a_orm.exception.SQLBuildException;
import com.steiner.make_a_orm.exception.SQLRuntimeException;
import com.steiner.make_a_orm.table.Table;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Column<T> {
    @Nullable
    private Table fromTable;

    @Nonnull
    public String name;

    @Nonnull
    public List<AbstractConstraint> constraints;

    public boolean isPrimaryKey;
    public boolean isAutoIncrement;
    public boolean setDefault;
    public boolean isNullable;

    public Column(@Nonnull String name) {
        this.fromTable = null;
        this.name = name;
        this.constraints = new LinkedList<>();
        this.constraints.add(new NotNullConstraint());
        this.isPrimaryKey = false;
        this.isAutoIncrement = false;
        this.setDefault = false;
        this.isNullable = false;
    }

    public void setFromTable(@Nonnull Table fromTable) {
        this.fromTable = fromTable;
    }

    @Nonnull
    public Table getFromTable() {
        return Objects.requireNonNull(this.fromTable);
    }
    // constraints
    public Column<T> nullable() {
        constraints.removeIf(constraint -> constraint instanceof NotNullConstraint);
        isNullable = true;
        return this;
    }

    public Column<T> uniqueIndex() {
        constraints.add(new UniqueConstraint());
        return this;
    }

    public boolean hasDefault() {
        // 不考虑 primary key auto_increment 字段
        return setDefault;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public T valueFromDB(@Nonnull ResultSet result) {
        try {
            return (T) result.getObject(name);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            throw new SQLRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * use it when meeting where and set
     */
    public abstract void inject(@Nonnull PreparedStatement statement, int index, @Nonnull T value);
    public final void injectDefault(@Nonnull PreparedStatement statement, int index) {
        //noinspection unchecked
        constraints.stream()
                .filter(constraint -> constraint instanceof DefaultValueConstraint<?>)
                .findFirst()
                .map(constraint -> (DefaultValueConstraint<T>) constraint)
                .ifPresentOrElse(constraint -> {
                    @Nullable T defaultValue = constraint.value;

                    if (defaultValue == null) {
                        try {
                            statement.setObject(index, null);
                        } catch (SQLException exception) {
                            throw new SQLBuildException(exception);
                        }
                    } else {
                        inject(statement, index, constraint.value);
                    }

                }, () -> {
                    if (!(isPrimaryKey && isAutoIncrement)) {
                        throw new SQLBuildException("there is no default value set in `%s`".formatted(name));
                    }

                });
    }


    @Nonnull
    public abstract String sqlType();
    @Nonnull
    public abstract String format(@Nonnull T value);

    public final String inlineConstraints() {
        return constraints.stream()
                .filter(constraint -> constraint.type() == ConstraintType.Inline)
                .map(AbstractConstraint::constraint)
                .collect(Collectors.joining(" "));
    }

    public final List<String> standAloneConstraints() {
        return constraints.stream()
                .filter(constraint -> constraint.type() == ConstraintType.StandAlone)
                .map(AbstractConstraint::constraint)
                .toList();
    }

    public final Optional<String> suffixConstraints() {
        return constraints.stream()
                .filter(constraint -> constraint.type() == ConstraintType.Suffix)
                .findFirst()
                .map(AbstractConstraint::constraint);
    }
}
