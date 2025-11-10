package com.steiner.make_a_orm;

import com.steiner.make_a_orm.column.numeric.IntegerColumn;
import com.steiner.make_a_orm.column.string.CharacterVaryingColumn;
import com.steiner.make_a_orm.key.ForeignKey;
import com.steiner.make_a_orm.key.PrimaryKey;
import com.steiner.make_a_orm.table.IntIdTable;
import com.steiner.make_a_orm.table.Table;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class TestORM {
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
}
