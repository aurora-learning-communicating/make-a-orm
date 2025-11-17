package com.steiner.make_a_orm;

import com.steiner.make_a_orm.column.numeric.DoubleColumn;
import com.steiner.make_a_orm.column.numeric.IntegerColumn;
import com.steiner.make_a_orm.column.numeric.SmallIntColumn;
import com.steiner.make_a_orm.column.numeric.TinyIntColumn;
import com.steiner.make_a_orm.column.string.CharacterVaryingColumn;
import com.steiner.make_a_orm.database.Database;
import com.steiner.make_a_orm.key.ForeignKey;
import com.steiner.make_a_orm.key.PrimaryKey;
import com.steiner.make_a_orm.table.IntIdTable;
import com.steiner.make_a_orm.table.Table;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.where.statement.WhereStatement;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class TestORM {
    static class NumberCollectionTable extends IntIdTable {
        public IntegerColumn column1 = integer("integer").withDefault(1);
        public DoubleColumn column2 = float64("double").withDefault(1.1);
        public SmallIntColumn column3 = smallint("short").withDefault((short) 1);
        public TinyIntColumn column4 = tinyint("byte").withDefault((byte) 2);

        public NumberCollectionTable() {
            super("numbers");
        }
    }

    Database database = Database.builder((builder) -> {
        builder.driver = new org.postgresql.Driver();
        builder.url = "jdbc:postgresql://192.168.1.10/orm-test";
        builder.username = "steiner";
        builder.password = "779151714";
    });

    NumberCollectionTable table = new NumberCollectionTable();

    @Test
    public void testQuery() {
        Transaction.transaction(database, () -> {
            // common search
            table.selectAll().stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(table.column1);
                Double column2 = resultRow.get(table.column2);
                Short column3 = resultRow.get(table.column3);
                Byte column4 = resultRow.get(table.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });

            // search with where
            table.selectAll().where(table.column1.less(8)).stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(table.column1);
                Double column2 = resultRow.get(table.column2);
                Short column3 = resultRow.get(table.column3);
                Byte column4 = resultRow.get(table.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });

            // search with where combination
            WhereStatement whereStatement = table.column1.greater(5).and(table.column2.less(10.0));
            table.selectAll().where(whereStatement).stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(table.column1);
                Double column2 = resultRow.get(table.column2);
                Short column3 = resultRow.get(table.column3);
                Byte column4 = resultRow.get(table.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });



            // DONE: 字段 切片 验证，来自同一地方，需要 exposed 验证, 详见 kotlin/com.steiner.make_a_orm/queryWithMultiTable 测试方法

        });

        // TODO: 我好像还没有 实现 建表？
    }

    // TODO: 1. 测试 格式化SQL 输出
    @Test
    public void testFormatSQL() {
        var users = new IntIdTable("users") {
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
                super("users");
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
        var scores = new IntIdTable("scores") {
            IntegerColumn score = integer("score").uniqueIndex();
        };

        class Users extends Table {
            CharacterVaryingColumn name;
            CharacterVaryingColumn password;
            ForeignKey<IntegerColumn> score;

            public Users() {
                super("users");
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
        var table = new IntIdTable("items") {
            IntegerColumn value1 = integer("value1");
            IntegerColumn value2 = integer("value2").check("ck_1", (column) -> {
                return column.greater(1);
            });

            CharacterVaryingColumn value3 = characterVarying("value3", 20).check("ck_2", (column) -> {
                return column.like("%hello%");
            });
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
                table.insert(statement -> {
                    statement.set(table.column1, countCopy);
                    statement.set(table.column2, random.nextDouble());
                    statement.set(table.column3, (short) countCopy);
                    statement.set(table.column4, (byte) countCopy);
                });
            }

            table.selectAll().stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(table.column1);
                Double column2 = resultRow.get(table.column2);
                Short column3 = resultRow.get(table.column3);
                Byte column4 = resultRow.get(table.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });
        });
    }

    @Test
    public void testInsertWithDefault() {
        Transaction.transaction(database, () -> {
            for (int count = 1; count <= 10; count += 1) {
                table.insert(statement -> {});
            }

            table.selectAll().stream().forEach(resultRow -> {
                Integer column1 = resultRow.get(table.column1);
                Double column2 = resultRow.get(table.column2);
                Short column3 = resultRow.get(table.column3);
                Byte column4 = resultRow.get(table.column4);

                System.out.println("%s %s %s %s".formatted(column1, column2, column3, column4));
            });

        });
    }
}
