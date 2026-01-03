package `in`.sitharaj.reduxkmp.sample.chat

import `in`.sitharaj.reduxkmp.core.State as ReduxState

/**
 * Chat App Models - Data classes for the chat application
 */

/**
 * Represents a chat message
 */
data class Message(
    val id: String,
    val content: String,
    val senderId: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ, FAILED
}

/**
 * Represents a chat user
 */
data class User(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val isOnline: Boolean = false,
    val isTyping: Boolean = false
)

/**
 * Represents a chat conversation
 */
data class Conversation(
    val id: String,
    val participantIds: List<String>,
    val lastMessageId: String? = null,
    val unreadCount: Int = 0
)
