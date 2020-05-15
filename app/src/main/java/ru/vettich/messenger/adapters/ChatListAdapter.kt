package ru.vettich.messenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vettich.messenger.ChatListQuery
import ru.vettich.messenger.R
import ru.vettich.messenger.Tools
import ru.vettich.messenger.models.Chat

class ChatListAdapter(
    private val onChatListener: OnChatListener
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    private var chatList: MutableList<Chat>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_chat_list, parent, false)
        return ViewHolder(
            view,
            onChatListener
        )
    }

    override fun getItemCount(): Int {
        if (chatList == null) return 0
        return chatList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        chatList?.get(position)?.let { holder.bind(it) }
    }

    fun setChatList(v: ArrayList<Chat>?) {
        chatList = v
    }

    fun setChat(chat: Chat) {
        if (chatList == null) return
        val idx = chatList!!.indexOfFirst { it.id == chat.id }
        if (idx == -1) chatList!!.add(chat)
        else chatList!![idx] = chat
    }

    class ViewHolder(itemView: View, private val onChatListener: OnChatListener) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var chatNameView: TextView = itemView.findViewById(R.id.chat_name_tv)
        var previewTextView: TextView = itemView.findViewById(R.id.chat_preview_text_tv)
        var previewTimeView: TextView = itemView.findViewById(R.id.chat_preview_time_tv)
        private var chatId: String = ""
        private var chatName: String = ""

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(chat: Chat) {
            chatNameView.text = chat.name

            if (chat.preview != null) {
                previewTextView.text = chat.preview.text
                previewTimeView.text = Tools.dateTimeFormat(chat.preview.createdAt.toString())
            }

            chatId = chat.id
            chatName = chat.name
        }

        override fun onClick(v: View?) {
            onChatListener.onSelectedChat(chatId, chatName)
        }
    }

    interface OnChatListener {
        fun onSelectedChat(chatId: String, chatName: String)
    }
}