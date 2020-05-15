package ru.vettich.messenger.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_change_password.*
import ru.vettich.messenger.Api
import ru.vettich.messenger.R

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        setSupportActionBar(toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener { onBackPressed() }

        change_password_bt.setOnClickListener { change() }
    }

    fun change() {
        val old = old_password_et.text.toString()
        val new = new_password_et.text.toString()
        if (new.isEmpty()) {
            runOnUiThread {
                Toast.makeText(this, "enter your new password", Toast.LENGTH_LONG).show()
            }
            return
        }

        change_password_progress_bar.visibility = View.VISIBLE
        Api.getInstance(this).changePassword(old, new) { error ->
            var txt = "Success"

            if (error != null) txt = error
            else runOnUiThread { onBackPressed() }

            runOnUiThread { Toast.makeText(this, txt, Toast.LENGTH_LONG).show() }

            change_password_progress_bar.visibility = View.INVISIBLE
        }
    }
}
