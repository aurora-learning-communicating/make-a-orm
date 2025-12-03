package com.steiner.make_a_orm

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

object Customers: IntIdTable("customers") {
    val name = text("name")
    val email = text("email")
}

object Orders: IntIdTable("orders") {
    val customerId = integer("customer_id")
    val product = text("product")
    val amount = integer("amount")
}

class TestJoinTable {
    val resource = javaClass.classLoader.getResourceAsStream("environment.yaml") ?: error("cannot find environment.yaml")
    val environment = Environment.loadFrom(resource)
    val database = Database.connect(url = environment.url, user = environment.username!!, password = environment.password!!)

    @Test
    fun testFakeJoinData() {
        transaction(database) {
            SchemaUtils.create(Customers, Orders)

            with (Customers) {
                insert {
                    it[name] = "Alice"
                    it[email] = "alice@example.com"
                }

                insert {
                    it[name] = "Bob"
                    it[email] = "bob@example.com"
                }

                insert {
                    it[name] = "Charlie"
                    it[email] = "charlie@example.com"
                }
            }

            with (Orders) {
                insert {
                    it[customerId] = 1
                    it[product] = "Laptop"
                    it[amount] = 1200
                }

                insert {
                    it[customerId] = 1
                    it[product] = "Mouse"
                    it[amount] = 25
                }

                insert {
                    it[customerId] = 2
                    it[product] = "Keyboard"
                    it[amount] = 75
                }

                insert {
                    it[customerId] = 4
                    it[product] = "Monitor"
                    it[amount] = 300
                }
            }
        }
    }
}