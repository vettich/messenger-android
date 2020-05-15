package ru.vettich.messenger.models

import ru.vettich.messenger.ChatListQuery
import ru.vettich.messenger.MessagesQuery
import ru.vettich.messenger.NewMessagesInChatSubscription

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

        fun fromWatchMessageInChat(msg: NewMessagesInChatSubscription.WatchNewMessagesInChat?): Message? {
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