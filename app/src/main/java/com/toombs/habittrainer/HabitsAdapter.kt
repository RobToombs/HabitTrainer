package com.toombs.habittrainer

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.single_card.view.*

class HabitsAdapter(val habits: List<Habit>) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>()
{
    class HabitViewHolder(val card: View) : RecyclerView.ViewHolder(card)

    override fun getItemCount(): Int = habits.size

    //  Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_card, parent, false)
        return HabitViewHolder(view)
    }

    // Specifies the contents for the shown habit
    override fun onBindViewHolder(holder: HabitViewHolder, index: Int)
    {
        if(holder != null) {
            val habit = habits[index]
            with(holder.card) {
                tv_title.text = habit.title
                tv_description.text = habit.description
                val stream = context.openFileInput(habit.file)
                iv_icon.setImageBitmap(BitmapFactory.decodeStream(stream))
            }
        }
    }
}