package com.steiner.make_a_orm

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import java.math.BigDecimal


object Employees: IntIdTable("employees") {
    val name = text("name")
    val department = text("department")
    val salary = decimal("salary", precision = 10, scale = 2)
}

class TestGroupBy {
    val resource = javaClass.classLoader.getResourceAsStream("environment.yaml") ?: error("cannot find environment.yaml")
    val environment = Environment.loadFrom(resource)
    val database = Database.connect(url = environment.url, user = environment.username!!, password = environment.password!!)

    @Test
    fun testGroupBy() {
        transaction(database) {
            SchemaUtils.create(Employees)
            with (Employees) {
                insert {
                    it[name] = "Alice"
                    it[department] = "Engineering"
                    it[salary] = BigDecimal("8000.00")
                }

                insert {
                    it[name] = "Bob"
                    it[department] = "Engineering"
                    it[salary] = BigDecimal("9000.00")
                }

                insert {
                    it[name] = "Charlie"
                    it[department] = "Marketing"
                    it[salary] = BigDecimal("6000.00")
                }

                insert {
                    it[name] = "David"
                    it[department] = "Marketing"
                    it[salary] = BigDecimal("5500.00")
                }

                insert {
                    it[name] = "Eve"
                    it[department] = "HR"
                    it[salary] = BigDecimal("7000.00")
                }

                select(department, salary.sum(), salary.avg())
                    .groupBy(department)
                    .forEach {
                        val dept = it[department]
                        val sum = it[salary.sum()]
                        val avg = it[salary.avg()]

                        println("dept: $dept, sum: $sum, avg: $avg")
                    }
            }
        }
    }
}