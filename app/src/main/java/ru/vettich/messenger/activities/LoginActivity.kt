package ru.vettich.messenger.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import ru.vettich.messenger.Api
import ru.vettich.messenger.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_bt.setOnClickListener {
            processLogin()
        }
    }

    private fun processLogin() {
        val username = username_et.text.toString()
        val password = password_et.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill out username/password")
            return
        }

        login_progress_bar.visibility = View.VISIBLE
        login(username, password)
    }

    private fun login(username: String, pass: String) {
        Api.getInstance(this).login(username, pass) { error ->
            if (error != null) {
                showError(error)
                if (error == "user not found") register(username, pass)
            } else {
                logged()
            }
        }
    }

    private fun register(username: String, pass: String) {
        Api.getInstance(this).register(username, pass) { error ->
            if (error != null) showError(error)
            else logged()
        }
    }

    private fun showError(txt: String) {
        Log.d("login", txt)
        login_progress_bar.visibility = View.INVISIBLE
        runOnUiThread {
            Toast.makeText(this@LoginActivity, txt, Toast.LENGTH_LONG).show()
        }
    }

    private fun logged() {
        login_progress_bar.visibility = View.INVISIBLE
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
