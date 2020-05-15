package ru.vettich.messenger.models

import ru.vettich.messenger.ChatListQuery
import ru.vettich.messenger.WatchChatListSubscription

data class Chat(
    val id: String,
    val name: String,
    val preview: Message?
) {
    companion object {
        fun fromChatList(chatList: List<ChatListQuery.Chat?>?): ArrayList<Chat>? {
            if (chatList == null) return null
            val chats = ArrayList<Chat>()
            chatList.forEach {
                chats.add(
                    Chat(
                        it!!.id,
                        it.name,
                        Message.fromChatListPreview(it.preview)
                    )
                )
            }
            return chats
        }

        fun fromWatchChatList(chat: WatchChatListSubscription.WatchChatList?): Chat? {
            if (chat == null) return null
            return Chat(chat.id, chat.name, Message.fromWatchChatList(chat.preview))
        }
    }
}