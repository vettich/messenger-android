package ru.vettich.messenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vettich.messenger.R
import ru.vettich.messenger.Tools
import ru.vettich.messenger.models.Message

class MessagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MY_TYPE = 0;
    private val THEIR_TYPE = 1;

    private var messages: MutableList<Message> = ArrayList()
    private var userId : String = ""

    fun setMessages(msgs: ArrayList<Message>) {
        messages = msgs
    }

    fun addMessage(msg: Message) {
        messages.add(msg)
    }

    fun setUserId(id: String) {
        userId = id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MY_TYPE) {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_my_message, parent, false)
            return MyMessageViewHolder(view)
        }
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_their_message, parent, false)
        return TheirMessageViewHolder(view)
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        if (messages[position].user!!.id == userId) return MY_TYPE
        return THEIR_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == MY_TYPE) {
            (holder as MyMessageViewHolder).bind(messages[position])
        } else {
            (holder as TheirMessageViewHolder).bind(messages[position])
        }
    }

    class MyMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textView: TextView = itemView.findViewById(R.id.my_message_tv)
        private var timeView: TextView = itemView.findViewById(R.id.my_message_time_tv)

        fun bind(msg: Message) {
            textView.text = msg.text
            timeView.text = Tools.dateTimeFormat(msg.createdAt.toString())
        }
    }

    class TheirMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textView: TextView = itemView.findViewById(R.id.their_message_tv)
        private var timeView: TextView = itemView.findViewById(R.id.their_message_time_tv)

        fun bind(msg: Message) {
            textView.text = msg.text
            timeView.text = Tools.dateTimeFormat(msg.createdAt.toString())
        }
    }
}