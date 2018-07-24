package com.toombs.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.toombs.habittrainer.Habit

class HabitDbTable(context: Context) {

    private val TAG = HabitDbTable::class.java.simpleName

    private val dbHelper = HabitTrainerDb(context)

    fun store(habit: Habit): Long
    {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        with(values) {
            put(HabitEntry.TITLE_COL, habit.title)
            put(HabitEntry.DESCR_COL, habit.description)
            put(HabitEntry.FILE_COL, habit.file)
        }

        // Pass a function from this DB to our transaction function that will invoke
        // all functions from this DB hence the lack of 'it' identifier4
        val id = db.transaction {
            insert(HabitEntry.TABLE_NAME, null, values)
        }

        db.close()

        Log.d(TAG, "Stored new habit to the DB $habit")

        return id
    }

    fun readAllHabits(): List<Habit>
    {
        val columns = arrayOf(HabitEntry._ID, HabitEntry.TITLE_COL, HabitEntry.DESCR_COL, HabitEntry.FILE_COL)
        val order = "${HabitEntry._ID} ASC"
        val db = dbHelper.readableDatabase
        val cursor = db.doQuery(HabitEntry.TABLE_NAME, columns, orderBy = order)

        return parseHabitsFrom(cursor)
    }

    private fun parseHabitsFrom(cursor: Cursor): MutableList<Habit>
    {
        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(HabitEntry.TITLE_COL)
            val desc = cursor.getString(HabitEntry.DESCR_COL)
            val filePath = cursor.getString(HabitEntry.FILE_COL)
            habits.add(Habit(title, desc, filePath))
        }
        cursor.close()
        return habits
    }

}

private fun Cursor.getString(colName: String): String = getString(getColumnIndex(colName))

private fun SQLiteDatabase.doQuery(table: String, columns: Array<String>, selection: String? = null,
                                   selectionArgs: Array<String>? = null, groupBy: String? = null,
                                   having: String? = null, orderBy: String? = null) : Cursor
{
    return query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
}

// Added an extension function to SQLiteDatabase that
// takes in a SQLiteDatabase function that has a return type of 'T' (our id in this case)
// Inline the function so that the compiler replaces the code block we are call this from, with this
// code itself so we don't suffer the performance loss of creating an Anonymous object every time
// to call this function from
private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T
{
    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()
        returnValue
    } finally {
        endTransaction()
    }

    close()

    return result
}