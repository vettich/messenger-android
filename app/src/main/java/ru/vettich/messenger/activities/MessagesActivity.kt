package ru.vettich.messenger.activities

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_messages.*
import ru.vettich.messenger.Api
import ru.vettich.messenger.R
import ru.vettich.messenger.adapters.MessagesAdapter

class MessagesActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private val messagesAdapter = MessagesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        setSupportActionBar(toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = chatName()
        toolbar.setNavigationOnClickListener { onBackPressed() }

        messages_rv.layoutManager = LinearLayoutManager(this)
        messages_rv.setHasFixedSize(true)
        messages_rv.adapter = messagesAdapter
        messagesAdapter.setUserId(userId())

        fetchMessages()
        subscribeNewMessages()

        message_text_et.setOnEditorActionListener(this)
        send_msg_bt.setOnClickListener {
            sendMessage(message_text_et.text.toString())
            message_text_et.text.clear()
        }
    }

    private fun fetchMessages() {
        Api.getInstance(this).getMessages(chatId()) { messages, error ->
            if (error == null) {
                messagesAdapter.setMessages(messages!!)
                if (messages!!.isNotEmpty()) empty_messages_tv.visibility = View.INVISIBLE
                runOnUiThread {
                    messagesAdapter.notifyDataSetChanged()
                    messages_rv.smoothScrollToPosition(messagesAdapter.itemCount)
                }
            } else {
                Log.d("messages", error)
            }
        }
    }

    private fun subscribeNewMessages() {
        Api.getInstance(this).watchMessages(chatId()) { msg, error ->
            if (error == null) {
                messagesAdapter.addMessage(msg!!)
                empty_messages_tv.visibility = View.INVISIBLE
                runOnUiThread {
                    messagesAdapter.notifyDataSetChanged()
                    messages_rv.smoothScrollToPosition(messagesAdapter.itemCount)
                }
                Log.d("messages", msg.text)
            } else {
                Log.d("messages", error)
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (v != null) {
                sendMessage(v.text.toString())
                v.text = ""
            }
            return true;
        }
        return false;
    }

    private fun sendMessage(text: String) {
        Api.getInstance(this).sendMessage(chatId(), text) { if (it != null) Log.d("messages", it) }
    }

    private fun chatName() = intent.getStringExtra("chat_name")!!

    private fun chatId() = intent.getStringExtra("chat_id")!!

    private fun userId() = intent.getStringExtra("user_id")!!
}
