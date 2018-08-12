package com.toombs.habittrainer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import com.toombs.habittrainer.db.HabitDbTable
import kotlinx.android.synthetic.main.activity_create_habit.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class CreateHabitActivity : AppCompatActivity() {

    private val TAG = CreateHabitActivity::class.java.simpleName

    private val CHOOSE_IMAGE_REQUEST = 1
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
    }

    fun storeHabit(v: View)
    {
        if(et_title.isBlank() || et_description.isBlank()) {
            Log.d(TAG, "No habit stored: title or description missing")
            displayErrorMessage("Your habit needs an engaging title and description.")
            return
        } else if(imageBitmap == null) {
            Log.d(TAG, "No habit stored: file missing.")
            displayErrorMessage("Add a motivating picture to your habit.")
            return
        }

        tv_errors.visibility - View.INVISIBLE

        // Store the habit
        val title = et_title.text.toString()
        val description = et_description.text.toString()
        val fileName = System.currentTimeMillis().toString() + ".png"

        openFileOutput(fileName, Context.MODE_PRIVATE).use {
            val stream = ByteArrayOutputStream()
            imageBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            it.write(stream.toByteArray())
        }

        val habit = Habit(title, description, fileName, -1)
        val id = HabitDbTable(this).store(habit)

        if(id == -1L) {
            displayErrorMessage("Habit could not be stored... let's not make this a habit.")
        } else {
            habit.id = id
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayErrorMessage(message: String) {
        tv_errors.text = message
        tv_errors.visibility = View.VISIBLE
    }

    fun chooseImage(v: View)
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        val chooser = Intent.createChooser(intent, "Choose file for habit")
        startActivityForResult(chooser, CHOOSE_IMAGE_REQUEST)

        Log.d(TAG, "Intent to choose file sent...")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null)
        {
            Log.d(TAG, "An file was chosen by the user.")

            val bitmap = tryReadBitmap(data.data)

            bitmap?.let {
                this.imageBitmap = bitmap
                iv_preview.setImageBitmap(bitmap)
                Log.d(TAG, "Read file bitmap and updated file view.")
            }
        }
    }

    private fun tryReadBitmap(data: Uri): Bitmap?
    {
        return try {
            MediaStore.Images.Media.getBitmap(contentResolver, data)
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

private fun EditText.isBlank() = this.text.toString().isBlank()
