package com.steiner.make_a_orm

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class TestDDL {
    object Table1: IntIdTable("table1") {
        val column1 = integer("column1")
        val column2 = varchar("column2", 50)
        val column3 = char("column3", 50)

        val employee = reference("employee", Employees.id)
    }

    object Employees: IntIdTable("employees") {
        val name = text("name")
        val email = text("email")
        val customerId = integer("customer_id")
        val product = text("product")
        val amount = integer("amount")
    }

    val input = this.javaClass.classLoader.getResourceAsStream("environment.yaml") ?: error("no such file environment.yaml")
    val environment: Environment = Environment.loadFrom(input)
    val database = Database.connect(url = environment.url, user = environment.username!! , password = environment.password!!)
    // val database = Database.connect(url = "jdbc:h2:/tmp/data.db")

    @Test
    fun testCreateTable() {
        transaction(database) {
            SchemaUtils.create(Table1, Employees)
            SchemaUtils.createStatements(Table1, Employees).forEach { statement ->
                println(statement)
            }
        }
    }
}