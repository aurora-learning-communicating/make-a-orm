package com.steiner.make_a_orm;

import com.steiner.make_a_orm.column.date.DateColumn;
import com.steiner.make_a_orm.column.date.TimeColumn;
import com.steiner.make_a_orm.column.date.TimestampColumn;
import com.steiner.make_a_orm.column.number.DoubleColumn;
import com.steiner.make_a_orm.column.number.IntegerColumn;
import com.steiner.make_a_orm.column.number.SmallIntColumn;
import com.steiner.make_a_orm.column.number.TinyIntColumn;
import com.steiner.make_a_orm.column.string.CharacterVaryingColumn;
import com.steiner.make_a_orm.database.Database;
import com.steiner.make_a_orm.key.ForeignKey;
import com.steiner.make_a_orm.key.PrimaryKey;
import com.steiner.make_a_orm.statement.select.ResultRow;
import com.steiner.make_a_orm.table.impl.IntIdTable;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.unit.TimeUnit;
import com.steiner.make_a_orm.vendor.dialect.Dialects;
import com.steiner.make_a_orm.where.statement.WhereStatement;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TestORM {
    static class NumberCollectionTable extends IntIdTable {
        public IntegerColumn column1 = integer("integer").withDefault(1);
        public DoubleColumn column2 = float64("double").withDefault(1.1);
        public SmallIntColumn column3 = smallint("short").withDefault((short) 1);
        public TinyIntColumn column4 = tinyint("byte").withDefault((byte) 2);

        public NumberCollectionTable() {
            super("numbers", Dialects.PostgreSQL);
        }
    }

    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("environment.yaml");
    Environment environment = Environment.loadFrom(Objects.requireNonNull(inputStream));

    Database database = Database.builder((builder) -> {
        builder.driver = new org.postgresql.Driver();
        builder.url = environment.url;
        builder.username = environment.username;
        builder.password = environment.password;
    });

    NumberCollectionTable numbers = new NumberCollectionTable();

    @Test
    public void testQuery() {
        Transaction.transaction(database, () -> {
            // common search
            numbers.selectAll().stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(numbers.column1);
                Double column2 = resultRow.get(numbers.column2);
                Short column3 = resultRow.get(numbers.column3);
                Byte column4 = resultRow.get(numbers.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });

            // search with where
            numbers.selectAll().where(numbers.column1.less(8)).stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(numbers.column1);
                Double column2 = resultRow.get(numbers.column2);
                Short column3 = resultRow.get(numbers.column3);
                Byte column4 = resultRow.get(numbers.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });

            // search with where combination
            WhereStatement whereStatement = numbers.column1.greater(5).and(numbers.column2.less(10.0));
            numbers.selectAll().where(whereStatement).stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(numbers.column1);
                Double column2 = resultRow.get(numbers.column2);
                Short column3 = resultRow.get(numbers.column3);
                Byte column4 = resultRow.get(numbers.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });



            // DONE: 字段 切片 验证，来自同一地方，需要 exposed 验证, 详见 kotlin/com.steiner.make_a_orm/queryWithMultiTable 测试方法

        });

        // TODO: 我好像还没有 实现 建表？
    }

    // TODO: 1. 测试 格式化SQL 输出
    @Test
    public void testFormatSQL() {
        var users = new IntIdTable("users", Dialects.PostgreSQL) {
            CharacterVaryingColumn name = characterVarying("name", 50).withDefault("unnamed");
            CharacterVaryingColumn password = characterVarying("password", 50);
            CharacterVaryingColumn email = characterVarying("email", 100).nullable();
            IntegerColumn age = integer("age").nullable();
        };

        System.out.println(users.toSQL());
    }

    // TODO: 测试 上述的 SQL 能否运行，并 pretty print

    // TODO: 1. 测试默认值

    // TODO: 2. 测试 复合键 作为主键的情况
    @Test
    public void testComposite() {
        class Users extends Table {
            CharacterVaryingColumn name;
            CharacterVaryingColumn password;

            public Users() {
                super("users", Dialects.PostgreSQL);
                this.name = characterVarying("name", 50);
                this.password = characterVarying("password", 50);
            }

            @Override
            public @Nullable PrimaryKey primaryKey() {
                return new PrimaryKey.Composite(name, password);
            }
        }
        Users users = new Users();
        System.out.println(users.toSQL());
    }
    // TODO: 3. 测试 外键
    @Test
    public void testForeignKey() {
        var scores = new IntIdTable("scores", Dialects.PostgreSQL) {
            IntegerColumn score = integer("score").uniqueIndex();
        };

        class Users extends Table {
            CharacterVaryingColumn name;
            CharacterVaryingColumn password;
            ForeignKey<IntegerColumn> score;

            public Users() {
                super("users", Dialects.PostgreSQL);
                this.name = characterVarying("name", 50);
                this.password = characterVarying("password", 50);
                this.score = reference("score", scores.score);
                // primaryKey(name, password);
            }

            @Override
            public @Nullable PrimaryKey primaryKey() {
                return new PrimaryKey.Composite(name, password);
            }
        }

        Users users = new Users();
        System.out.println(scores.toSQL());
        System.out.println(users.toSQL());
    }

    // TODO: 测试 check 限制
    @Test
    public void testCheck() {
        var table = new IntIdTable("items", Dialects.PostgreSQL) {
            IntegerColumn value1 = integer("value1");
            IntegerColumn value2 = integer("value2").check("ck_1", (column) -> column.greater(1));
            CharacterVaryingColumn value3 = characterVarying("value3", 20).check("ck_2", (column) -> column.like("%hello%"));
        };

        System.out.println(table.toSQL());
    }

    // TODO: 测试 where 各种语句 以及 and/or/not

    // TODO: 测试 查询

    @Test
    public void testInsert() {
        Transaction.transaction(database, () -> {
            Random random = new Random();
            for (int count = 1; count <= 10; count += 1) {
                int countCopy = count;
                numbers.insert(statement -> {
                    statement.set(numbers.column1, countCopy);
                    statement.set(numbers.column2, random.nextDouble());
                    statement.set(numbers.column3, (short) countCopy);
                    statement.set(numbers.column4, (byte) countCopy);
                });
            }

            numbers.selectAll().stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(numbers.column1);
                Double column2 = resultRow.get(numbers.column2);
                Short column3 = resultRow.get(numbers.column3);
                Byte column4 = resultRow.get(numbers.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });
        });
    }

    @Test
    public void testInsertWithDefault() {
        Transaction.transaction(database, () -> {
            for (int count = 1; count <= 10; count += 1) {
                numbers.insert(statement -> {});
            }

            numbers.selectAll().stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(numbers.column1);
                Double column2 = resultRow.get(numbers.column2);
                Short column3 = resultRow.get(numbers.column3);
                Byte column4 = resultRow.get(numbers.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });

        });
    }

    @Test
    public void testInsertReturning() {
        Transaction.transaction(database, () -> {
            ResultRow resultRow = numbers.insertReturning((statement) -> {
                statement.set(numbers.column1, 2);
                statement.set(numbers.column2, 2.1);
                statement.set(numbers.column3, (short) 2);
                statement.set(numbers.column4, (byte) 2);
            }, numbers.column1, numbers.column2);

            // TODO: query with known column
            int column1 = resultRow.get(numbers.column1);
            double column2 = resultRow.get(numbers.column2);

            System.out.println("column1: %s, column2: %s".formatted(column1, column2));
            // TODO: query with unknown column

            // short column3 = resultRow.get(table.column3);
        });
    }

    static class Times extends IntIdTable {
        DateColumn date = date("date");
        TimestampColumn dateTime = timestamp("datetime");
        TimestampColumn timestamp = timestamp("timestamp");
        TimeColumn time = time("time");

        public Times() {
            super("times", Dialects.PostgreSQL);
        }
    }

    @Test
    public void testTimes() {
        Times table = new Times();

        Transaction.transaction(database, () -> {
            LocalDateTime localDateTime = LocalDateTime.of(2020, 10, 10, 10, 10, 10);
            table.insert(statement -> {
                statement.set(table.date, LocalDate.of(2010, 10, 10));
                statement.set(table.dateTime, localDateTime);
                statement.set(table.timestamp, localDateTime);
                statement.set(table.time, LocalTime.of(20, 0, 0));
            });

            table.selectAll().stream().forEach(resultRow -> {
                LocalDate date = resultRow.get(table.date);
                LocalDateTime dateTime = resultRow.get(table.dateTime);
                LocalDateTime timestamp = resultRow.get(table.timestamp);
                LocalTime time = resultRow.get(table.time);

                System.out.println("date: %s, dateTime: %s, timestamp: %s, time: %s".formatted(date, dateTime, timestamp, time));
            });
        });
    }

    @Test
    public void testUpdate() {
        Transaction.transaction(database, () -> {
//            numbers.update((statement) -> {
//                statement.set(numbers.column2, 10.0);
//                statement.set(numbers.column3, (short) 10);
//                statement.set(numbers.column4, (byte) 10);
//
//                statement.where(numbers.id().equal(1));
//            });

            numbers.update(statement -> {
                statement.plus(numbers.column1, 1);

                statement.where(numbers.id().equal(1));
            });
        });
    }

    @Test
    public void testUpdateTimes() {
        Times times = new Times();

        Transaction.transaction(database, () -> {
            times.update(statement -> {
                statement.plus(times.date, 1, TimeUnit.Date.Day);
                statement.plus(times.dateTime, 2, TimeUnit.DateTime.Month);
                statement.plus(times.timestamp, 3, TimeUnit.DateTime.Minute);
                statement.plus(times.time, 4, TimeUnit.Time.Hour);

                statement.where(times.id().equal(1));
            });
        });
    }

    @Test
    public void testDeleteNumbers() {
        Transaction.transaction(database, () -> {
            numbers.delete(statement -> {
                statement.where(numbers.id().inList(List.of(18, 19, 20)));
            });

            numbers.selectAll().stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(numbers.column1);
                Double column2 = resultRow.get(numbers.column2);
                Short column3 = resultRow.get(numbers.column3);
                Byte column4 = resultRow.get(numbers.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });
        });
    }

    @Test
    public void testSelectLimt() {
        Transaction.transaction(database, () -> {
            numbers.selectAll()
                    .offset(12)
                    .limit(10)
                    .orderBy(numbers.id())
                    .stream().forEach(resultRow -> {
                Integer id = resultRow.get(numbers.id());
                Integer column1 = resultRow.get(numbers.column1);
                Double column2 = resultRow.get(numbers.column2);
                Short column3 = resultRow.get(numbers.column3);
                Byte column4 = resultRow.get(numbers.column4);

                System.out.println("%s %s %s %s %s".formatted(id, column1, column2, column3, column4));
            });
        });
    }
}
