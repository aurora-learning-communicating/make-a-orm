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

public class TestAggregate {
    InputStream input = getClass().getClassLoader().getResourceAsStream("environment.yaml");
    Environment environment = Environment.loadFrom(input);
    Database database = Database.builder(builder -> {
        builder.driver = new Driver();
        builder.url = environment.url;
        builder.username = environment.username;
        builder.password = environment.password;
    });


    /**
     * 1. float/double column
     * 2. count
     * 3. sum
     * 4. max
     * 5. min
     * 6. avg
     */

    static class Employees extends IntIdTable {
        public final TextColumn name;
        public final TextColumn department;
        public final DecimalColumn salary;

        public Employees() {
            super("employees", Dialects.PostgreSQL);

            name = text("name");
            department = text("department");
            salary = decimal("salary", 10, 2);
        }
    }

    Employees employees = new Employees();

    @Test
    public void testSelect() {
        Transaction.transaction(database, () -> {
            employees.selectAll()
                    .stream()
                    .forEach(resultRow -> {
                        int id = resultRow.get(employees.id());
                        String name = resultRow.get(employees.name);
                        String department = resultRow.get(employees.department);
                        BigDecimal salary = resultRow.get(employees.salary);

                        System.out.println("id: %s, name: %s, department: %s, salary: %s".formatted(id, name, department, salary));
                    });
        });
    }


    // group by
    @Test
    public void testSumAndAvg() {
        Transaction.transaction(database, () -> {
            employees.select(employees.department, employees.salary.min(), employees.salary.max(), employees.salary.sum(), employees.salary.avg(), employees.department.count())
                    .groupBy(employees.department)
                    .stream()
                    .forEach(resultRow -> {
                        String department = resultRow.get(employees.department);
                        BigDecimal sum = resultRow.get(employees.salary.sum());
                        BigDecimal avg = resultRow.get(employees.salary.avg(2));
                        BigDecimal max = resultRow.get(employees.salary.max());
                        BigDecimal min = resultRow.get(employees.salary.min());
                        long count = resultRow.get(employees.department.count());
                        System.out.println("count: %s, department: %s, sum: %s, avg: %s, max: %s, min: %s".formatted(count, department, sum, avg, max, min));
                    });

        });
    }
}
