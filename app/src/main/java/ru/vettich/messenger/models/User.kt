package ru.vettich.messenger.models

import ru.vettich.messenger.ChatListQuery
import ru.vettich.messenger.MessagesQuery
import ru.vettich.messenger.NewMessagesInChatSubscription

data class User(
    val id: String,
    val username: String
) {
    companion object {
        fun fromChatListUser(user: ChatListQuery.User?): User? {
            if (user == null) return null
            return User("", user.username)
        }

        fun fromMessages(user: MessagesQuery.User?): User? {
            if (user == null) return null
            return User(user.id, user.username)
        }

        fun fromWatchMessagesInChat(user: NewMessagesInChatSubscription.User?): User? {
            if (user == null) return null
            return User("", user.username)
        }
    }
}