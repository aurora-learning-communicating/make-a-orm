package com.steiner.make_a_orm

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class TestExposed {
    object Numbers: IntIdTable("numbers") {
        val column1 = integer("integer").default(1)
        val column2 = double("double").default(1.1)
        val column3 = short("short").default(1)
        val column4 = byte("byte").default(2)
    }

    val input = this.javaClass.getResourceAsStream("environment.yaml") ?: error("no such file environment.yaml")
    val environment: Environment = Environment.loadFrom(input)
    val database = Database.connect(url = environment.url!!, user = environment.username!! , password = environment.password!!)

    @Test
    fun createTable() {
        transaction(database) {
            SchemaUtils.drop(Numbers)
            SchemaUtils.create(Numbers)
        }
    }

    @Test
    fun fakeData() {
        transaction(database) {


            val numbers = 1..10

            with (Numbers) {
                for (number in numbers) {
                    insert {
                        it[column1] = number
                        it[column2] = number.toDouble()
                        it[column3] = number.toShort()
                        it[column4] = number.toByte()
                    }
                }
            }
        }
    }

    @Test
    fun query() {
        transaction(database) {
            with (Numbers) {
                selectAll().forEach {
                    val value1 = it[column1]
                    val value2 = it[column2]
                    val value3 = it[column3]
                    val value4 = it[column4]

                    println("value1: ${value1.javaClass}, value2: ${value2.javaClass}, value3: ${value3.javaClass}, value4: ${value4.javaClass}")
                }
            }
        }
    }

    object Table1: IntIdTable("table1") {
        val value1 = integer("value1")
    }

    object Table2: IntIdTable("table2") {
        val value1 = integer("value1")
    }

    fun queryWithMultiTable() {
        transaction(database) {
            SchemaUtils.create(Table1, Table2)

            for (number in 1..10) {
                Table1.insert {
                    it[value1] = number
                }

                Table2.insert {
                    it[value1] = number
                }
            }

            Table1.select(Table2.value1).forEach { resultRow ->
                println(resultRow[Table2.value1])
            } // this should throw runtime error

            Table1.selectAll().where {
                Table1.value1.less(Table2.value1)
            }.forEach {
                println(it[Table2.value1])
            }
        }
    }
}

