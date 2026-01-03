package com.sitharaj.reduxkmp.sample.chat

import com.sitharaj.reduxkmp.core.Action
import com.sitharaj.reduxkmp.core.Reducer
import com.sitharaj.reduxkmp.core.State as ReduxState
import com.sitharaj.reduxkmp.core.createSelector
import com.sitharaj.reduxkmp.sample.chat.messages.MessagesState
import com.sitharaj.reduxkmp.sample.chat.messages.messagesAdapter
import com.sitharaj.reduxkmp.sample.chat.messages.messagesReducer
import com.sitharaj.reduxkmp.sample.chat.users.UsersState
import com.sitharaj.reduxkmp.sample.chat.users.usersAdapter
import com.sitharaj.reduxkmp.sample.chat.users.usersReducer

/**
 * Root Chat State - Combines all feature states
 */
data class ChatState(
    val messages: MessagesState = MessagesState(),
    val users: UsersState = UsersState(),
    val ui: UIState = UIState()
) : ReduxState

/**
 * UI State - Transient UI state
 */
data class UIState(
    val inputText: String = "",
    val isInputFocused: Boolean = false,
    val showEmojiPicker: Boolean = false
) : ReduxState

/**
 * UI Actions
 */
sealed interface UIAction : Action {
    data class SetInputText(val text: String) : UIAction
    data class SetInputFocused(val focused: Boolean) : UIAction
    data class SetShowEmojiPicker(val show: Boolean) : UIAction
}

/**
 * UI Reducer
 */
fun uiReducer(state: UIState, action: Action): UIState {
    return when (action) {
        is UIAction.SetInputText -> state.copy(inputText = action.text)
        is UIAction.SetInputFocused -> state.copy(isInputFocused = action.focused)
        is UIAction.SetShowEmojiPicker -> state.copy(showEmojiPicker = action.show)
        else -> state
    }
}

/**
 * Root Reducer - Combines all reducers
 */
val chatReducer: Reducer<ChatState> = { state, action ->
    ChatState(
        messages = messagesReducer(state.messages, action),
        users = usersReducer(state.users, action),
        ui = uiReducer(state.ui, action)
    )
}

// ============================================
// Selectors - Memoized derived state
// ============================================

/**
 * Select all messages sorted by timestamp
 */
val selectAllMessages = { state: ChatState ->
    messagesAdapter.selectAll(state.messages.messages)
        .sortedBy { it.timestamp }
}

/**
 * Select unread message count
 */
val selectUnreadCount = createSelector(
    selectAllMessages
) { messages ->
    messages.count { !it.isRead }
}

/**
 * Select user by ID
 */
fun selectUserById(state: ChatState, userId: String): User? {
    return usersAdapter.selectById(state.users.users, userId)
}

/**
 * Select all users
 */
val selectAllUsers = { state: ChatState ->
    usersAdapter.selectAll(state.users.users)
}

/**
 * Select typing users (excluding current user)
 */
val selectTypingUsers = { state: ChatState ->
    val users = selectAllUsers(state)
    val typingIds = state.users.typingUserIds
    val currentUserId = state.users.currentUserId
    users.filter { it.id in typingIds && it.id != currentUserId }
}

/**
 * Select if any user is typing
 */
val selectIsAnyoneTyping = { state: ChatState ->
    selectTypingUsers(state).isNotEmpty()
}

/**
 * Select messages with user info for display
 */
data class MessageWithUser(
    val message: Message,
    val user: User?,
    val isCurrentUser: Boolean
)

val selectMessagesWithUsers = { state: ChatState ->
    val messages = selectAllMessages(state)
    val currentUserId = state.users.currentUserId
    
    messages.map { message ->
        MessageWithUser(
            message = message,
            user = selectUserById(state, message.senderId),
            isCurrentUser = message.senderId == currentUserId
        )
    }
}
