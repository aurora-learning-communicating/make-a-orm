package com.steiner.make_a_orm;

import com.steiner.make_a_orm.column.number.DecimalColumn;
import com.steiner.make_a_orm.column.string.TextColumn;
import com.steiner.make_a_orm.database.Database;
import com.steiner.make_a_orm.table.impl.IntIdTable;
import com.steiner.make_a_orm.transaction.Transaction;
import com.steiner.make_a_orm.vendor.dialect.Dialects;
import org.junit.jupiter.api.Test;
import org.postgresql.Driver;

import java.io.InputStream;
import java.math.BigDecimal;

public class TestCreateTable {
    InputStream input = getClass().getClassLoader().getResourceAsStream("environment.yaml");
    Environment environment = Environment.loadFrom(input);
    Database database = Database.builder(builder -> {
        builder.driver = new Driver();
        builder.url = environment.url;
        builder.username = environment.username;
        builder.password = environment.password;
    });

    static class Employees2 extends IntIdTable {
        public final TextColumn name;
        public final TextColumn department;
        public final DecimalColumn salary;

        public Employees2() {
            super("employees2", Dialects.PostgreSQL);

            name = text("name");
            department = text("department");
            salary = decimal("salary", 10, 2);
        }
    }

    Employees2 employees = new Employees2();

    @Test
    public void createTable() {
        Transaction.transaction(database, () -> {
            SchemaUtils.create(employees);

            employees.insert(statement -> {
                statement.set(employees.name, "Alice");
                statement.set(employees.department, "Engineering");
                statement.set(employees.salary, new BigDecimal("8000.00"));
            });

            employees.insert(statement -> {
                statement.set(employees.name, "Bob");
                statement.set(employees.department, "Engineering");
                statement.set(employees.salary, new BigDecimal("9000.00"));
            });

           employees.insert(statement -> {
               statement.set(employees.name, "Charlie");
               statement.set(employees.department, "Marketing");
               statement.set(employees.salary, new BigDecimal("6000.00"));
           });

           employees.insert(statement -> {
               statement.set(employees.name, "David");
               statement.set(employees.department, "Marketing");
               statement.set(employees.salary, new BigDecimal("5500.00"));
           });

           employees.insert(statement -> {
               statement.set(employees.name, "Eve");
               statement.set(employees.department, "HR");
               statement.set(employees.salary, new BigDecimal("7000.00"));
           });

           employees.select(employees.department, employees.salary.sum(), employees.salary.avg(2))
                   .groupBy(employees.department)
                   .stream()
                   .forEach(resultRow -> {
                       String department = resultRow.get(employees.department);
                       BigDecimal sum = resultRow.get(employees.salary.sum());
                       BigDecimal avg = resultRow.get(employees.salary.avg());

                       System.out.println("department: %s, sum: %s, avg: %s".formatted(department, sum, avg));
                   });
        });
    }

    @Test
    public void dropTable() {
        Transaction.transaction(database, () -> {
            SchemaUtils.drop(employees);
        });
    }
}
