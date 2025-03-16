# Make a ORM

## 介绍

最近对 ORM 比较有兴趣，想自己做一个玩玩
由于自己实在不喜欢 mybatis 那种自己写 SQL 的框架，这个 SQL 在编写时检查不到错误，只有在
运行时才会有错误检测，还是数据库抛出来的

近几年我在学习 Kotlin ，虽然写的很舒服，渐渐的我意识到编程语言只是一个工具，不应该把它视作一个宗教信仰，
不应该把它归为 甜豆腐脑 还是 咸豆腐脑，Emacs 还是 Vim 这样的问题
因此，我决定先把业界最常用的 Java 学好，这个项目就先用 Java 来写吧

还有一点，我在看一些 ORM 框架的时候，有些框架选定字段的时候用的是字符串，比如说我要选定一个名叫 `name` 的字段，
在这个 ORM 中他这样指定 "name"  
我们要做的是类型安全的 ORM ，我们假定有一个 Table 表，有 `name` 这个成员变量表示一个叫做 `name` 的数据库 column ，这样他就能在编写代码时由
LSP 识别出来，具体参考 Kotlin 的 ORM 框架

## 用法

这个项目的 SQL 语法暂时不考虑方言，都基于 MySQL 语法
这个 ORM 的写法参考自 Kotlin 的 Exposed 框架

### 数据表的定义

```java
public class NumberCollectionTable extends IntIdTable {
    @Nullable
    private static NumberCollectionTable singleton = null;

    @Nonnull
    public static NumberCollectionTable getInstance() {
        if (singleton == null) {
            singleton = new NumberCollectionTable();
        }

        return singleton;
    }

    public final BooleanColumn column1 = bool("boolean").defaultValue(false);
    public final DecimalColumn column2 = decimal("decimal", 3, 2).defaultValue(BigDecimal.valueOf(1.23));
    public final DoubleColumn column3 = float64("double").defaultValue(1.23);
    public final FloatColumn column4 = float32("float").defaultValue(1.23f);
    public final SmallIntColumn column5 = smallint("smallint").defaultValue((short) 12);
    public final TinyIntColumn column6 = tinyint("tinyint").defaultValue((byte) 8);
    public final BigIntColumn column7 = bigint("bigint").defaultValue(1L);

    private NumberCollectionTable() {
        super("long-id-table");
    }
}
```

### 数据库和表实例的创建

```java
// com.mysql.cj.jdbc.Driver
Driver driver = Result.from(Driver::new).get();
Database database = Database.builder()
        .driver(driver)
        .username("steiner")
        .password("*******")
        .url("jdbc:mysql://localhost/playground")
        .build().get();

NumberCollectionTable numberCollectionTable = NumberCollectionTable.getInstance();
```

### 操作

#### 创建表

```java
@Test
public void testNumericDefault() {
    Transaction.runWith(database, () -> {
        SchemaUtils.drop(numberCollectionTable);
        SchemaUtils.create(numberCollectionTable);
    });
}
```

#### 查询

```java
@Test
public void testNumericSelectWhere() {
    Transaction.runWith(database, () -> {
        numberCollectionTable.selectAll()
                .where(numberCollectionTable.id.greater(2))
                .stream()
                .forEach(resultRow -> {
                    String message = numberCollectionTable.columns
                            .stream()
                            .map(resultRow::get)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));

                    System.out.println(message);
                });



    });

}

@Test
public void testNumericSelectWhereCombination() {
    Transaction.runWith(database, () -> {
        numberCollectionTable.selectAll()
                .where(() -> {
                    return numberCollectionTable.column1.equal(true)
                            .and(numberCollectionTable.column3.less(1.1))
                            .and(numberCollectionTable.column4.greater(0.0f));
                })
                .orderBy(numberCollectionTable.column6)
                .stream()
                .forEach(resultRow -> {
                    String message = numberCollectionTable.columns
                            .stream()
                            .map(resultRow::get)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));

                    System.out.println(message);
                });
    });
}
```

#### 插入

```java
@Test
public void testNumericInsert() {
    Transaction.runWith(database, () -> {
        for (int count = 1; count <= 100; count += 1) {
            int outsideCount = count;
            Random random = new Random();
            numberCollectionTable.insert(statement -> {
                statement.set(numberCollectionTable.column1, outsideCount % 2 == 0);
                statement.set(numberCollectionTable.column3, random.nextDouble());
                statement.set(numberCollectionTable.column4, random.nextFloat());
                statement.set(numberCollectionTable.column5, (short) outsideCount);
                statement.set(numberCollectionTable.column6, (byte) outsideCount);
                statement.set(numberCollectionTable.column7, random.nextLong());
            });
        }

        // insert with default value
        numberCollectionTable.insert(statement -> {});
    });
}
```

#### 插入并返回

```java
@Test
public void testNumericInsertReturning() {
    Transaction.runWith(database, () -> {
        ResultRow resultRow = numberCollectionTable.insertReturning(
                statement -> {},
                numberCollectionTable.id,
                numberCollectionTable.column1,
                numberCollectionTable.column2
        );

        int id = resultRow.get(numberCollectionTable.id);
        System.out.println(resultRow.get(numberCollectionTable.column1));
        System.out.println(resultRow.get(numberCollectionTable.column2));
        System.out.println(id);
    });

}
```

#### 更新

```java
@Test
public void testNumericUpdate() {
    Transaction.runWith(database, () -> {
        WhereStatement where = numberCollectionTable.id.between(40, 80);
        numberCollectionTable.update(where, statement -> {
            statement.set(numberCollectionTable.column1, true);
        });
    });
}
```

#### 删除 + where 条件

```java
@Test
public void testNumericDeleteWithWhere() {
    Transaction.runWith(database, () -> {
        numberCollectionTable.deleteWhere(() -> numberCollectionTable.column1.equal(true));
    });
}
```

### 字段限制

1. `nullable()`
2. `defaultValue(@Nullable T value)`
3. `primaryKey()`
4. `uniqueIndex()`
5. `autoIncrement()`

### where 语句

1. `between`
2. `greater`, `greaterEq`, `less`, `lessEq`
3. `equal`, `notEqual`
4. `in`
5. `like`
6. `isNull`, `notNull`
7. `and`, `or`, `not`

## 数据表的字段类型翻译

[MySQL 数据类型](https://www.runoob.com/mysql/mysql-data-types.html)

## MySQL 字段类型分类, Java 类型的对应，框架对字段的支持

### 1. 数值类型

这些类型用于存储数值数据，包括整数和浮点数。

| MySQL 类型            | Java 类型                | 字段创建|
|-----------------------|--------------------------|----|
| `TINYINT(1)`          | `boolean`                | bool(String name)|
| `TINYINT`             | `byte`                   | tinyint(String name) |
| `SMALLINT`            | `short`                  | smallint(String name) |
| `MEDIUMINT`, `INT`    | `int`                    | integer(String name) |
| `BIGINT`              | `long`                   | bigint(String name) |
| `FLOAT`               | `float`                  | float32(String name) |
| `DOUBLE`              | `double`                 | float64(String name) |
| `DECIMAL`             | `java.math.BigDecimal`   | decimal(String name, int precision, int scale)|

### 2. 字符串 + Clob + Blob

这些类型用于存储字符数据，包括固定长度和可变长度的字符串。

| MySQL 类型            | Java 类型                | 字段创建|
|-----------------------|--------------------------|-------|
| `char`| `String` | character(String name, int length) |
| `varchar` | `String` | characterVarying(String name, int length) |
| `text` | `String` | text(String name) |
| `mediumtext` | `Reader` |  mediumText(String name) |
| `longtext` | `Reader` | longText(String name) |
| `blob` | `InputStream` | blob(String name) |
| `mediumblob` | `InputStream` | mediumBlob(String name) |
| `longblob` | `InputStream` | longBlob(String name) |

### 3. 日期和时间类型

这些类型用于存储日期和时间信息。

| MySQL 类型            | Java 类型                | 字段创建 |
|-----------------------|--------------------------|----------------------------------------------------------------------|
| `DATE`                | `java.sql.Date`          | date(String name) |
| `TIME`                | `java.sql.Time`          | time(String name) |
| `DATETIME`, `TIMESTAMP`| `java.sql.Timestamp`     | timestamp(String name) |
| `YEAR`                | `short` 或 `int`         | 暂不支持 |

### 4. 位类型

这些类型用于存储位字段。

| MySQL 类型            | Java 类型                | 字段创建 |
|-----------------------|--------------------------|--------|
| `BIT`                 | `boolean[]` 或 `byte`    | 暂不支持 |
