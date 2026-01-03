package `in`.sitharaj.reduxkmp.sample.chat.messages

import `in`.sitharaj.reduxkmp.core.Action
import `in`.sitharaj.reduxkmp.core.State as ReduxState
import `in`.sitharaj.reduxkmp.sample.chat.Message
import `in`.sitharaj.reduxkmp.sample.chat.MessageStatus
import `in`.sitharaj.reduxkmp.toolkit.EntityAdapter
import `in`.sitharaj.reduxkmp.toolkit.EntityState
import `in`.sitharaj.reduxkmp.toolkit.createEntityAdapter

/**
 * Messages Entity Adapter - Normalized message storage
 */
val messagesAdapter: EntityAdapter<Message> = createEntityAdapter { it.id }

/**
 * Messages State
 */
data class MessagesState(
    val messages: EntityState<Message> = messagesAdapter.getInitialState(),
    val loading: Boolean = false,
    val error: String? = null,
    val sendingMessageIds: Set<String> = emptySet()
) : ReduxState

/**
 * Messages Actions
 */
sealed interface MessagesAction : Action {
    // CRUD Actions
    data class AddMessage(val message: Message) : MessagesAction
    data class AddMessages(val messages: List<Message>) : MessagesAction
    data class UpdateMessage(val id: String, val changes: (Message) -> Message) : MessagesAction
    data class RemoveMessage(val id: String) : MessagesAction
    data object ClearMessages : MessagesAction
    
    // Status Actions
    data class SetLoading(val loading: Boolean) : MessagesAction
    data class SetError(val error: String?) : MessagesAction
    data class MarkAsRead(val messageId: String) : MessagesAction
    data class SetMessageStatus(val messageId: String, val status: MessageStatus) : MessagesAction
    
    // Sending Actions
    data class StartSending(val messageId: String) : MessagesAction
    data class SendingComplete(val messageId: String) : MessagesAction
}

/**
 * Messages Reducer
 */
fun messagesReducer(state: MessagesState, action: Action): MessagesState {
    return when (action) {
        is MessagesAction.AddMessage -> state.copy(
            messages = messagesAdapter.addOne(state.messages, action.message)
        )
        is MessagesAction.AddMessages -> state.copy(
            messages = messagesAdapter.addMany(state.messages, action.messages)
        )
        is MessagesAction.UpdateMessage -> state.copy(
            messages = messagesAdapter.updateOne(state.messages, action.id, action.changes)
        )
        is MessagesAction.RemoveMessage -> state.copy(
            messages = messagesAdapter.removeOne(state.messages, action.id)
        )
        is MessagesAction.ClearMessages -> state.copy(
            messages = messagesAdapter.getInitialState()
        )
        is MessagesAction.SetLoading -> state.copy(loading = action.loading)
        is MessagesAction.SetError -> state.copy(error = action.error)
        is MessagesAction.MarkAsRead -> state.copy(
            messages = messagesAdapter.updateOne(state.messages, action.messageId) { 
                it.copy(isRead = true, status = MessageStatus.READ) 
            }
        )
        is MessagesAction.SetMessageStatus -> state.copy(
            messages = messagesAdapter.updateOne(state.messages, action.messageId) {
                it.copy(status = action.status)
            }
        )
        is MessagesAction.StartSending -> state.copy(
            sendingMessageIds = state.sendingMessageIds + action.messageId
        )
        is MessagesAction.SendingComplete -> state.copy(
            sendingMessageIds = state.sendingMessageIds - action.messageId
        )
        else -> state
    }
}
