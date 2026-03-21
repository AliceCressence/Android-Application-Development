package com.joelfah.gradecalculator

import android.app.Application
import com.joelfah.gradecalculator.data.HistoryRepository

class GradeApplication : Application() {
    lateinit var historyRepository: HistoryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        historyRepository = HistoryRepository(this)
    }
}
