package com.joelfah.gradecalculator.data

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.joelfah.gradecalculator.models.GradeSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class HistoryRepository(private val context: Context) {

    private val driver = AndroidSqliteDriver(
        schema = GradeHistoryDatabase.Schema,
        context = context,
        name = "grade_history.db"
    )
    private val database = GradeHistoryDatabase(driver)
    private val queries = database.gradeHistoryQueries

    fun saveSession(session: GradeSession) {
        val studentsJson = Json.encodeToString(session.students)
        val headersJson = Json.encodeToString(session.subjectHeaders)
        
        queries.insert(
            fileName = session.fileName,
            processedAt = session.processedAt,
            totalStudents = session.totalStudents.toLong(),
            passCount = session.passCount.toLong(),
            failCount = session.failCount.toLong(),
            classAverage = session.classAverage,
            studentsJson = studentsJson,
            headersJson = headersJson
        )
    }

    fun getAllSessions(): List<GradeSession> {
        return queries.selectAll().executeAsList().map { entity ->
            GradeSession(
                id = entity.id,
                fileName = entity.fileName,
                processedAt = entity.processedAt,
                totalStudents = entity.totalStudents.toInt(),
                passCount = entity.passCount.toInt(),
                failCount = entity.failCount.toInt(),
                classAverage = entity.classAverage,
                students = Json.decodeFromString(entity.studentsJson),
                subjectHeaders = Json.decodeFromString(entity.headersJson)
            )
        }
    }

    fun deleteSession(id: Long) {
        queries.deleteById(id)
    }

    fun clearAll() {
        queries.deleteAll()
    }
}
