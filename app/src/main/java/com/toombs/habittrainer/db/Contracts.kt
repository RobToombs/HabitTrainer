package com.toombs.habittrainer.db

import android.provider.BaseColumns

const val DATABASE_NAME = "habittrainer.db"
const val DATABASE_VERSION = 10

object HabitEntry : BaseColumns {
    val TABLE_NAME = "habit"
    val _ID = "id"
    val TITLE_COL = "title"
    val DESCR_COL = "description"
    val FILE_COL = "file"
}

