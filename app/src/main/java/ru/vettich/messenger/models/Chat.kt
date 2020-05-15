package ru.vettich.messenger.models

import ru.vettich.messenger.ChatListQuery

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
    }
}