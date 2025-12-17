package com.steiner.make_a_orm;

import com.steiner.make_a_orm.column.number.IntegerColumn;
import com.steiner.make_a_orm.column.string.TextColumn;
import com.steiner.make_a_orm.database.Database;
import com.steiner.make_a_orm.table.impl.IntIdTable;
import com.steiner.make_a_orm.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.postgresql.Driver;

import java.io.InputStream;

public class TestJoin {


    InputStream input = getClass().getClassLoader().getResourceAsStream("environment.yaml");
    Environment environment = Environment.loadFrom(input);
    Database database = Database.builder(builder -> {
        builder.driver = new Driver();
        builder.url = environment.url;
        builder.username = environment.username;
        builder.password = environment.password;
    });

    @Test
    public void testJoin() {
        var Customers = new IntIdTable("customers") {
            public final TextColumn name = text("name");
            public final TextColumn email = text("email");
        };

        var Orders = new IntIdTable("orders") {
            public final IntegerColumn customerId = integer("customer_id");
            public final TextColumn product = text("product");
            public final IntegerColumn amount = integer("amount");
        };

        Transaction.transaction(database, () -> {
            Orders.leftJoin(Customers, Orders.customerId, Customers.id())
                    .select(Orders.id(), Customers.name, Orders.product, Orders.amount)
                    .stream()
                    .forEach(resultRow -> {
                        System.out.println("order id: %s, customer name: %s, order amount: %s"
                                .formatted(resultRow.get(Orders.id()), resultRow.getOrNull(Customers.name), resultRow.get(Orders.amount)));
                    });
        });
    }
}
