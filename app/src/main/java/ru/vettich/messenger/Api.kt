package ru.vettich.messenger

import android.content.Context
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.request.RequestHeaders
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import okhttp3.OkHttpClient
import ru.vettich.messenger.models.Chat
import ru.vettich.messenger.models.Message
import ru.vettich.messenger.models.User
import ru.vettich.messenger.type.MessageInput
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.concurrent.TimeUnit

class Api(
    private val context: Context
) {

    companion object {
        private var INSTANCE: Api? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Api(context).also {
                    INSTANCE = it
                }
            }
    }

    // TODO надо бы вынести это в конфиги
//    private val httpUrl = "http://m.local.vettich.ru:5008/graphql"
//    private val wsUrl = "ws://m.local.vettich.ru:5008/graphql"
    private val httpUrl = "http://288150-ck36157.tmweb.ru:5008/graphql"
    private val wsUrl = "ws://288150-ck36157.tmweb.ru:5008/graphql"

    private val okHttpClient = OkHttpClient().newBuilder()
        .retryOnConnectionFailure(true)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val apolloClient = ApolloClient.builder()
        .serverUrl(httpUrl)
        .okHttpClient(okHttpClient)
        .build()

    private val apolloClientSubs = ApolloClient.builder()
        .serverUrl(httpUrl)
        .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory(wsUrl, okHttpClient))
        .build()

    private val tokenFilename = "token"

    var token: String = ""

    private fun authHeaders() = RequestHeaders.builder()
        .addHeader("Authorization", "Bearer $token")

    init {
        readToken()
    }

    private fun readToken() {
        val f = File(context.applicationContext.cacheDir, tokenFilename)
        if (f.exists()) FileReader(f).buffered().use { token = it.readText() }
        else token = ""
    }

    private fun saveToken(value: String) {
        token = value
        val out = FileOutputStream(File(context.applicationContext.cacheDir, tokenFilename))
        out.buffered().use { it.write(value.toByteArray()) }
        out.close()
    }

    fun isAuth() = token.isNotEmpty()

    fun logout() = saveToken("")

    fun login(username: String, password: String, callback: (error: String?) -> Unit) {
        apolloClient
            .mutate(LoginMutation(username, password))
            .enqueue(object : ApolloCall.Callback<LoginMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(e.toString())
                }

                override fun onResponse(response: Response<LoginMutation.Data>) {
                    response.data?.login?.let {
                        saveToken(it.value)
                        callback(null)
                    }
                    response.errors?.let {
                        callback(joinErrors(it))
                    }
                }
            })
    }

    fun register(username: String, password: String, callback: (error: String?) -> Unit) {
        apolloClient
            .mutate(RegisterMutation(username, password))
            .enqueue(object : ApolloCall.Callback<RegisterMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(e.toString())
                }

                override fun onResponse(response: Response<RegisterMutation.Data>) {
                    response.data?.signup?.let {
                        saveToken(it.value)
                        callback(null)
                    }
                    response.errors?.let {
                        callback(joinErrors(it))
                    }
                }
            })
    }

    fun changePassword(old: String, new: String, callback: (error: String?) -> Unit) {
        apolloClient
            .mutate(ChangePasswordMutation(old, new))
            .requestHeaders(authHeaders().build())
            .enqueue(object : ApolloCall.Callback<ChangePasswordMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(e.toString())
                }

                override fun onResponse(response: Response<ChangePasswordMutation.Data>) {
                    if (response.errors == null) {
                        callback(null)
                    } else {
                        response.errors?.let { callback(joinErrors(it)) }
                    }
                }
            })
    }

    fun getUserInfo(callback: (user: User?, error: String?) -> Unit) {
        apolloClient
            .query(UserQuery())
            .requestHeaders(authHeaders().build())
            .enqueue(object : ApolloCall.Callback<UserQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(null, e.toString())
                }

                override fun onResponse(response: Response<UserQuery.Data>) {
                    response.data?.user?.let { callback(User(it.id, it.username), null) }
                    response.errors?.let { callback(null, joinErrors(it)) }
                }
            })
    }

    fun getChatList(callback: (chats: ArrayList<Chat>?, error: String?) -> Unit) {
        apolloClient
            .query(ChatListQuery())
            .requestHeaders(authHeaders().build())
            .enqueue(object : ApolloCall.Callback<ChatListQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(null, e.toString())
                }

                override fun onResponse(response: Response<ChatListQuery.Data>) {
                    response.data?.chats?.let { callback(Chat.fromChatList(it), null) }
                    response.errors?.let { callback(null, joinErrors(it)) }
                }
            })
    }

    fun watchChatList(callback: (chat: Chat?, error: String?) -> Unit) {
        apolloClientSubs
            .subscribe(WatchChatListSubscription(token))
            .execute(object :
                ApolloSubscriptionCall.Callback<WatchChatListSubscription.Data> {
                override fun onFailure(e: ApolloException) {
                    callback(null, e.toString())
                }

                override fun onResponse(response: Response<WatchChatListSubscription.Data>) {
                    response.data?.watchChatList?.let {
                        callback(Chat.fromWatchChatList(it), null)
                    }
                    response.errors?.let { callback(null, joinErrors(it)) }
                }

                override fun onConnected() {}

                override fun onTerminated() {}

                override fun onCompleted() {}
            })
    }

    fun getMessages(
        chatId: String,
        callback: (messages: ArrayList<Message>?, error: String?) -> Unit
    ) {
        apolloClient
            .query(MessagesQuery(chatId))
            .requestHeaders(authHeaders().build())
            .enqueue(object : ApolloCall.Callback<MessagesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(null, e.toString())
                }

                override fun onResponse(response: Response<MessagesQuery.Data>) {
                    response.data?.messages?.let { callback(Message.fromMessages(it), null) }
                    response.errors?.let { callback(null, joinErrors(it)) }
                }
            })
    }

    fun watchMessages(chatId: String, callback: (msg: Message?, error: String?) -> Unit) {
        apolloClientSubs
            .subscribe(WatchNewMessagesInChatSubscription(token, chatId))
            .execute(object :
                ApolloSubscriptionCall.Callback<WatchNewMessagesInChatSubscription.Data> {
                override fun onFailure(e: ApolloException) {
                    callback(null, e.toString())
                }

                override fun onResponse(response: Response<WatchNewMessagesInChatSubscription.Data>) {
                    response.data?.watchNewMessagesInChat?.let {
                        callback(Message.fromWatchMessageInChat(it), null)
                    }
                    response.errors?.let { callback(null, joinErrors(it)) }
                }

                override fun onConnected() {}

                override fun onTerminated() {}

                override fun onCompleted() {}
            })
    }

    fun sendMessage(chatId: String, text: String, callback: (error: String?) -> Unit) {
        val preparedText = text.trim()
        if (preparedText.isEmpty()) return
        apolloClient
            .mutate(SendMessageMutation(MessageInput(preparedText, chatId)))
            .requestHeaders(authHeaders().build())
            .enqueue(object : ApolloCall.Callback<SendMessageMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(e.toString())
                }

                override fun onResponse(response: Response<SendMessageMutation.Data>) {
                    response.errors?.let { callback(joinErrors(it)) }
                }
            })
    }

    fun createChat(username: String, callback: (error: String?) -> Unit) {
        val prepared = username.trim()
        if (prepared.isEmpty()) return
        apolloClient
            .mutate(CreatePersonalChatMutation(prepared))
            .requestHeaders(authHeaders().build())
            .enqueue(object : ApolloCall.Callback<CreatePersonalChatMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    callback(e.toString())
                }

                override fun onResponse(response: Response<CreatePersonalChatMutation.Data>) {
                    response.errors?.let { callback(joinErrors(it)) }
                }
            })
    }

    private fun joinErrors(errors: List<Error>?): String? {
        if (errors == null) return null
        val s = StringBuilder()
        errors.forEach {
            s.append(it.message)
            s.append("\n")
        }
        return s.trim().toString()
    }
}