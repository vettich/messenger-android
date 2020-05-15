package ru.vettich.messenger.models

import ru.vettich.messenger.*

data class Message(
    val id: String,
    val text: String,
    val user: User?,
    val createdAt: Any
) {
    companion object {
        fun fromChatListPreview(msg: ChatListQuery.Preview?): Message? {
            if (msg == null) return null
            return Message("", msg.text, User.fromChatListUser(msg.user), msg.createdAt)
        }

        fun fromWatchChatList(msg: WatchChatListSubscription.Preview?): Message? {
            if (msg == null) return null
            return Message("", msg.text, User.fromWatchChatList(msg.user), msg.createdAt)
        }

        fun fromWatchMessageInChat(msg: WatchNewMessagesInChatSubscription.WatchNewMessagesInChat?): Message? {
            if (msg == null) return null
            return Message(msg.id, msg.text, User.fromWatchMessagesInChat(msg.user), msg.createdAt)
        }

        fun fromMessages(list: List<MessagesQuery.Message?>?): ArrayList<Message>? {
            if (list == null) return null
            val messages = ArrayList<Message>()
            list.forEach {
                messages.add(
                    Message(
                        it!!.id,
                        it.text,
                        User.fromMessages(it.user),
                        it.createdAt
                    )
                )
            }
            return messages
        }
    }
}