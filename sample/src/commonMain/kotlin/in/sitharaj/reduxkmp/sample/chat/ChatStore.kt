package `in`.sitharaj.reduxkmp.sample.chat

import `in`.sitharaj.reduxkmp.core.Store
import `in`.sitharaj.reduxkmp.core.createStore
import `in`.sitharaj.reduxkmp.middleware.LoggingMiddleware
import `in`.sitharaj.reduxkmp.middleware.ThunkMiddleware
import `in`.sitharaj.reduxkmp.middleware.ListenerMiddleware
import `in`.sitharaj.reduxkmp.middleware.createListenerMiddleware
import `in`.sitharaj.reduxkmp.sample.chat.messages.MessagesAction
import `in`.sitharaj.reduxkmp.sample.chat.users.UsersAction
import `in`.sitharaj.reduxkmp.sample.chat.users.getInitialUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Chat Store - Configured with all middleware
 */
object ChatStoreProvider {
    
    private var _store: Store<ChatState>? = null
    
    /**
     * Listener Middleware for side effects
     */
    private val listenerMiddleware = createListenerMiddleware<ChatState>()
    
    /**
     * Get or create the store
     */
    fun getStore(): Store<ChatState> {
        return _store ?: createChatStore().also { _store = it }
    }
    
    private fun createChatStore(): Store<ChatState> {
        // Setup listener middleware effects
        setupListeners()
        
        return createStore(
            initialState = ChatState(),
            reducer = chatReducer,
            scope = CoroutineScope(Dispatchers.Main)
        ) {
            addMiddleware(ThunkMiddleware())
            addMiddleware(listenerMiddleware.middleware)
            // addMiddleware(LoggingMiddleware(tag = "ChatApp"))
        }
    }
    
    private fun setupListeners() {
        // Auto-mark messages as read when UI is focused
        listenerMiddleware.addListener<UIAction.SetInputFocused> { action, api ->
            if (action.focused) {
                // Mark all unread messages as read
                val messages = selectAllMessages(api.getState())
                messages.filter { !it.isRead }.forEach { message ->
                    api.dispatch(MessagesAction.MarkAsRead(message.id))
                }
            }
        }
        
        // Simulate typing indicator when input changes
        listenerMiddleware.addListener<UIAction.SetInputText> { action, api ->
            if (action.text.isNotEmpty()) {
                // Simulate other users seeing typing indicator
                // In a real app, this would send to server
            }
        }
        
        // Simulate receiving a reply after sending a message
        listenerMiddleware.addListener<MessagesAction.SetMessageStatus> { action, api ->
            if (action.status == MessageStatus.DELIVERED) {
                // Simulate auto-reply after a delay
                api.fork {
                    delay(2000)
                    
                    val replies = listOf(
                        "That's interesting! 🤔",
                        "I see what you mean!",
                        "Great point! 👍",
                        "Let me think about that...",
                        "Redux KMP is amazing! 🚀"
                    )
                    
                    val replyMessage = Message(
                        id = "msg_reply_${`in`.sitharaj.reduxkmp.sample.chat.messages.currentTimeMillis()}",
                        content = replies.random(),
                        senderId = listOf("user_alice", "user_bob").random(),
                        timestamp = `in`.sitharaj.reduxkmp.sample.chat.messages.currentTimeMillis(),
                        isRead = false,
                        status = MessageStatus.DELIVERED
                    )
                    
                    api.dispatch(MessagesAction.AddMessage(replyMessage))
                }
            }
        }
    }
    
    /**
     * Initialize the store with sample data
     */
    fun initialize(store: Store<ChatState>) {
        // Add initial users
        store.dispatch(UsersAction.AddUsers(getInitialUsers()))
    }
}
