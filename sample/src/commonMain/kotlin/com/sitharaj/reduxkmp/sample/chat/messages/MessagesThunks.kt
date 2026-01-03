package com.sitharaj.reduxkmp.sample.chat.messages

import com.sitharaj.reduxkmp.core.Dispatcher
import com.sitharaj.reduxkmp.core.GetState
import com.sitharaj.reduxkmp.middleware.ThunkAction
import com.sitharaj.reduxkmp.sample.chat.ChatState
import com.sitharaj.reduxkmp.sample.chat.Message
import com.sitharaj.reduxkmp.sample.chat.MessageStatus
import com.sitharaj.reduxkmp.toolkit.AsyncThunk
import com.sitharaj.reduxkmp.toolkit.ThunkAPI
import com.sitharaj.reduxkmp.toolkit.createAsyncThunk
import kotlinx.coroutines.delay

/**
 * Send Message Thunk - Simulates sending a message with async operation
 */
class SendMessageThunk(
    private val content: String,
    private val senderId: String
) : ThunkAction<ChatState> {
    
    override suspend fun execute(dispatch: Dispatcher, getState: GetState<ChatState>) {
        // Create optimistic message
        val messageId = "msg_${currentTimeMillis()}"
        val message = Message(
            id = messageId,
            content = content,
            senderId = senderId,
            timestamp = currentTimeMillis(),
            status = MessageStatus.SENDING
        )
        
        // Optimistically add message
        dispatch(MessagesAction.AddMessage(message))
        dispatch(MessagesAction.StartSending(messageId))
        
        try {
            // Simulate network delay
            delay(800)
            
            // Update to sent status
            dispatch(MessagesAction.SetMessageStatus(messageId, MessageStatus.SENT))
            dispatch(MessagesAction.SendingComplete(messageId))
            
            // Simulate delivery after a bit
            delay(500)
            dispatch(MessagesAction.SetMessageStatus(messageId, MessageStatus.DELIVERED))
            
        } catch (e: Exception) {
            dispatch(MessagesAction.SetMessageStatus(messageId, MessageStatus.FAILED))
            dispatch(MessagesAction.SendingComplete(messageId))
            dispatch(MessagesAction.SetError("Failed to send message: ${e.message}"))
        }
    }
}

/**
 * Fetch Chat History Thunk - Loads initial messages
 */
class FetchChatHistoryThunk : ThunkAction<ChatState> {
    
    override suspend fun execute(dispatch: Dispatcher, getState: GetState<ChatState>) {
        dispatch(MessagesAction.SetLoading(true))
        dispatch(MessagesAction.SetError(null))
        
        try {
            // Simulate API call
            delay(1000)
            
            // Generate sample messages
            val sampleMessages = generateSampleMessages()
            dispatch(MessagesAction.AddMessages(sampleMessages))
            dispatch(MessagesAction.SetLoading(false))
            
        } catch (e: Exception) {
            dispatch(MessagesAction.SetLoading(false))
            dispatch(MessagesAction.SetError("Failed to load messages: ${e.message}"))
        }
    }
    
    private fun generateSampleMessages(): List<Message> {
        val baseTime = currentTimeMillis() - 3600000 // 1 hour ago
        
        return listOf(
            Message(
                id = "msg_1",
                content = "Hey! Welcome to Redux KMP Chat! 👋",
                senderId = "user_alice",
                timestamp = baseTime,
                isRead = true,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_2",
                content = "This demo showcases all Redux Toolkit features",
                senderId = "user_alice",
                timestamp = baseTime + 60000,
                isRead = true,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_3",
                content = "That's awesome! What features are included?",
                senderId = "current_user",
                timestamp = baseTime + 120000,
                isRead = true,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_4",
                content = "EntityAdapter for normalized state, createAsyncThunk for async, Selectors for derived data, and ListenerMiddleware for side effects! 🚀",
                senderId = "user_alice",
                timestamp = baseTime + 180000,
                isRead = true,
                status = MessageStatus.READ
            ),
            Message(
                id = "msg_5",
                content = "Try sending a message below!",
                senderId = "user_bob",
                timestamp = baseTime + 240000,
                isRead = false,
                status = MessageStatus.DELIVERED
            )
        )
    }
}

// Helper for getting current time (platform-specific)
expect fun currentTimeMillis(): Long
