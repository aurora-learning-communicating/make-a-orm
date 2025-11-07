package com.steiner.make_a_orm

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

object Scores: IntIdTable("scores") {
    val score = integer("score").uniqueIndex()
}

object Users: IntIdTable("users") {
    val name = varchar("name", 50)
    val password = varchar("password", 50).check {
        it like "hello"
    }

    val score = reference("score", Scores.score)
}

class TestExposed {
    @Test
    fun test() {
        val database = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction(database) {
            SchemaUtils.create(Scores, Users)

        }
    }
}