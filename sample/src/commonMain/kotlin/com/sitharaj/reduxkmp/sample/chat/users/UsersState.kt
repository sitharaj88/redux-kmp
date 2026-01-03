package com.sitharaj.reduxkmp.sample.chat.users

import com.sitharaj.reduxkmp.core.Action
import com.sitharaj.reduxkmp.core.State as ReduxState
import com.sitharaj.reduxkmp.sample.chat.User
import com.sitharaj.reduxkmp.toolkit.EntityAdapter
import com.sitharaj.reduxkmp.toolkit.EntityState
import com.sitharaj.reduxkmp.toolkit.createEntityAdapter

/**
 * Users Entity Adapter - Normalized user storage
 */
val usersAdapter: EntityAdapter<User> = createEntityAdapter { it.id }

/**
 * Users State
 */
data class UsersState(
    val users: EntityState<User> = usersAdapter.getInitialState(),
    val currentUserId: String = "current_user",
    val typingUserIds: Set<String> = emptySet()
) : ReduxState

/**
 * Users Actions
 */
sealed interface UsersAction : Action {
    data class AddUser(val user: User) : UsersAction
    data class AddUsers(val users: List<User>) : UsersAction
    data class UpdateUser(val id: String, val changes: (User) -> User) : UsersAction
    data class SetOnline(val userId: String, val isOnline: Boolean) : UsersAction
    data class SetTyping(val userId: String, val isTyping: Boolean) : UsersAction
    data class AddTypingUser(val userId: String) : UsersAction
    data class RemoveTypingUser(val userId: String) : UsersAction
}

/**
 * Users Reducer
 */
fun usersReducer(state: UsersState, action: Action): UsersState {
    return when (action) {
        is UsersAction.AddUser -> state.copy(
            users = usersAdapter.addOne(state.users, action.user)
        )
        is UsersAction.AddUsers -> state.copy(
            users = usersAdapter.addMany(state.users, action.users)
        )
        is UsersAction.UpdateUser -> state.copy(
            users = usersAdapter.updateOne(state.users, action.id, action.changes)
        )
        is UsersAction.SetOnline -> state.copy(
            users = usersAdapter.updateOne(state.users, action.userId) {
                it.copy(isOnline = action.isOnline)
            }
        )
        is UsersAction.SetTyping -> state.copy(
            users = usersAdapter.updateOne(state.users, action.userId) {
                it.copy(isTyping = action.isTyping)
            }
        )
        is UsersAction.AddTypingUser -> state.copy(
            typingUserIds = state.typingUserIds + action.userId
        )
        is UsersAction.RemoveTypingUser -> state.copy(
            typingUserIds = state.typingUserIds - action.userId
        )
        else -> state
    }
}

/**
 * Initial sample users
 */
fun getInitialUsers(): List<User> = listOf(
    User(
        id = "current_user",
        name = "You",
        avatarUrl = "👤",
        isOnline = true
    ),
    User(
        id = "user_alice",
        name = "Alice",
        avatarUrl = "👩‍💻",
        isOnline = true
    ),
    User(
        id = "user_bob",
        name = "Bob",
        avatarUrl = "👨‍🎨",
        isOnline = true
    )
)
