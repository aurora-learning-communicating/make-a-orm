package com.steiner.make_a_orm;

import com.mysql.cj.jdbc.Driver;
import com.steiner.make_a_orm.column.Column;
import com.steiner.make_a_orm.database.Database;
import com.steiner.make_a_orm.impl.NumberCollectionTable;
import com.steiner.make_a_orm.select.ResultRow;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.utils.GlobalLogger;
import com.steiner.make_a_orm.utils.SchemaUtils;
import com.steiner.make_a_orm.utils.result.Result;
import com.steiner.make_a_orm.where.WhereStatement;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class ColumnTest {
    Driver driver = Result.from(Driver::new).get();
    Database database = Database.builder()
            .driver(driver)
            .username("steiner")
            .password("779151714")
            .url("jdbc:mysql://localhost/playground")
            .build().get();

    NumberCollectionTable numberCollectionTable = NumberCollectionTable.getInstance();
    // TODO test string column

    // TODO test numeric column

    // TODO test clob column with stream

    // TODO test bool column

    // TODO test blob column with inputstream

    // TODO test default value of numeric, using Column::format
    @Test
    public void testNumericDefault() {

        Transaction.runWith(database, () -> {
            SchemaUtils.drop(numberCollectionTable);
            SchemaUtils.create(numberCollectionTable);
        });
    }

    // TODO test insert
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

            numberCollectionTable.selectAll()
                    .stream()
                    .forEach(resultRow -> {
                        for (Column<?> column: numberCollectionTable.columns) {
                            System.out.println(resultRow.get(column));
                        }
                    });
        });

    }

    // TODO test if insert can return id
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

    @Test
    public void testNumericDeleteWithWhere() {
        Transaction.runWith(database, () -> {
            numberCollectionTable.deleteWhere(() -> numberCollectionTable.column1.equal(true));
        });
    }


    // TODO test where condition
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

    @Test
    public void testBuildWhere() throws SQLException {
        List<WhereStatement> whereStatements = new ArrayList<>();

        WhereStatement whereStatement = numberCollectionTable.column1.equal(true)
                .and(numberCollectionTable.column3.less(1.1))
                .and(numberCollectionTable.column4.greater(0.0f));

        if (whereStatement.shouldInject) {
            whereStatements.add(whereStatement);
        }

        if (whereStatement.otherStatements != null) {
            whereStatements.addAll(
                    whereStatement.otherStatements
                            .stream()
                            .filter(stat -> stat.shouldInject)
                            .toList()
            );
        }

        String sql = "select * from `long-id-table` where %s".formatted(buildWhere(whereStatement));
        PreparedStatement statement = database.connection.prepareStatement(sql);

        // flatten
        int startIndex = 1;
        GlobalLogger.logger().debug("1. startIndex: {}", startIndex);

        whereStatements.get(0).setInjectIndex(startIndex);
        startIndex = whereStatements.get(0).inject(statement);
        GlobalLogger.logger().debug("2. startIndex: {}", startIndex);


        whereStatements.get(1).setInjectIndex(startIndex);
        startIndex = whereStatements.get(1).inject(statement);
        GlobalLogger.logger().debug("3. startIndex: {}", startIndex);

        whereStatements.get(2).setInjectIndex(startIndex);
        startIndex = whereStatements.get(2).inject(statement);
        GlobalLogger.logger().debug("4. startIndex: {}", startIndex);

//        int startIndex = 1;
//        for (WhereStatement where: whereStatements) {
//            where.setInjectIndex(startIndex);
//            startIndex = where.inject(statement);
//
//            if (startIndex == -1) {
//                System.out.println("here");
//                break;
//            }
//        }

        statement.executeQuery();
    }

    @Nonnull
    public String buildWhere(@Nonnull WhereStatement whereStatement) {
        StringBuilder stringBuilder = new StringBuilder();
        String first = whereStatement.toSQL();
        stringBuilder.append(first).append(" ");

        if (whereStatement.otherStatements != null) {
            String others = whereStatement.otherStatements
                    .stream()
                    .map(WhereStatement::toSQL)
                    .collect(Collectors.joining(" "));

            stringBuilder.append(others);
        }

        return stringBuilder.toString();
    }

    // TODO test between and in list
    @Test
    public void testNumericBetweenAndInList() {
        Transaction.runWith(database, () -> {
            numberCollectionTable.selectAll()
                    .where(() -> {
                        return numberCollectionTable.id.between(30, 100)
                                .and(numberCollectionTable.column1.equal(true))
                                .and(numberCollectionTable.column3.less(1.1))
                                .and(numberCollectionTable.column4.greater(0.0f));
                    })
                    .stream()
                    .forEach(resultRow -> {
                        String message = numberCollectionTable.columns
                                .stream()
                                .map(resultRow::get)
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "));

                        System.out.println(message);
                    });

            System.out.println();

            numberCollectionTable.selectAll()
                    .where(() -> numberCollectionTable.id.between(30, 100))
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

    // TODO test update
    @Test
    public void testNumericUpdate() {
        Transaction.runWith(database, () -> {
            WhereStatement where = numberCollectionTable.id.between(40, 80);
            numberCollectionTable.update(where, statement -> {
                statement.set(numberCollectionTable.column1, true);
            });
        });
    }
}
