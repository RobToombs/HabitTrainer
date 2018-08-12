package com.toombs.habittrainer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.toombs.habittrainer.db.HabitDbTable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adapter -> defines data
        // Recycler View -> implement 3 methods
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = HabitsAdapter(HabitDbTable(this).readAllHabits())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_habit) {
            switchTo(CreateHabitActivity::class.java)
        }

        return true
    }

    private fun switchTo(c: Class<*>) {
        val intent = Intent(this, c)
        startActivity(intent)
    }

    fun deleteHabit(v: View) {
        val id = v.tag as Long
        HabitDbTable(this).remove(id)
        (v.parent.parent as ViewGroup).removeAllViews()
    }
}
