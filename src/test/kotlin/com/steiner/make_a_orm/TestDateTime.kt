package com.steiner.make_a_orm

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDate
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.time
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class TestDateTime {
    val input = this.javaClass.classLoader.getResourceAsStream("environment.yaml") ?: error("no such file environment.yaml")
    val environment: Environment = Environment.loadFrom(input)
    val database = Database.connect(url = environment.url, user = environment.username!! , password = environment.password!!)

    object Times: IntIdTable("times") {
        val date = date("date")
        val datetime = datetime("datetime")
        val timestamp = timestamp("timestamp")
        val time = time("time")
    }


    @Test
    fun test() {
        transaction(database) {
            SchemaUtils.drop(Times)
            SchemaUtils.create(Times)

            with (Times) {
                insert {
                    it[date] = CurrentDate
                    it[datetime] = CurrentDateTime
                    it[timestamp] = CurrentTimestamp
                    it[time] = LocalTime(hour = 20, minute = 10, second = 0)
                }

                insert {
                    it[date] = LocalDate(year = 2025, month = Month.APRIL, dayOfMonth = 20)
                    it[datetime] = LocalDateTime(year = 2025, month = Month.APRIL, dayOfMonth = 20, hour = 20, minute = 0, second = 0)
                    it[timestamp] = Clock.System.now()
                    it[time] = LocalTime(hour = 10, minute = 10, second = 0)
                }
            }
        }
    }
}