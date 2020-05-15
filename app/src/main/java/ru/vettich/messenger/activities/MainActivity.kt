package ru.vettich.messenger.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_header.*
import ru.vettich.messenger.Api
import ru.vettich.messenger.R
import ru.vettich.messenger.adapters.ChatListAdapter
import ru.vettich.messenger.models.User

class MainActivity : AppCompatActivity(), ChatListAdapter.OnChatListener,
    NavigationView.OnNavigationItemSelectedListener {

    private var chatListAdapter = ChatListAdapter(this)

    private var user: User? = null

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkLoggedIn()) return

        setSupportActionBar(toolbar)

        nav_view.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        fetchUserInfo()
        fetchChatList()

        chat_list_rv.layoutManager = LinearLayoutManager(this)
        chat_list_rv.setHasFixedSize(true)
        chat_list_rv.adapter = chatListAdapter
    }

    private fun fetchUserInfo() {
        Api.getInstance(this).getUserInfo() { user, error ->
            if (error != null) {
                runOnUiThread { Toast.makeText(this, error, Toast.LENGTH_LONG).show() }
            } else {
                this.user = user
                username_tv?.text = user!!.username
            }
        }
    }

    private fun fetchChatList() {
        Api.getInstance(this).getChatList { chats, error ->
            if (error == null) {
                chatListAdapter.setChatList(chats)
                runOnUiThread { chatListAdapter.notifyDataSetChanged() }
            } else {
                runOnUiThread { Toast.makeText(this, error, Toast.LENGTH_LONG).show() }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun checkLoggedIn(): Boolean {
        if (!Api.getInstance(this).isAuth()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return false
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_change_pass -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                Api.getInstance(this).logout()
                checkLoggedIn()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true
    }

    override fun onSelectedChat(chatId: String, chatName: String) {
        val intent = Intent(this, MessagesActivity::class.java)
        intent.putExtra("chat_id", chatId)
        intent.putExtra("chat_name", chatName)
        intent.putExtra("user_id", user!!.id)
        startActivity(intent)
    }
}
