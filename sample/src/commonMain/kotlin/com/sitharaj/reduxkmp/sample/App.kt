package com.sitharaj.reduxkmp.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sitharaj.reduxkmp.sample.chat.ChatState
import com.sitharaj.reduxkmp.sample.chat.ChatStoreProvider
import com.sitharaj.reduxkmp.sample.chat.MessageStatus
import com.sitharaj.reduxkmp.sample.chat.MessageWithUser
import com.sitharaj.reduxkmp.sample.chat.UIAction
import com.sitharaj.reduxkmp.sample.chat.messages.FetchChatHistoryThunk
import com.sitharaj.reduxkmp.sample.chat.messages.SendMessageThunk
import com.sitharaj.reduxkmp.sample.chat.messages.currentTimeMillis
import com.sitharaj.reduxkmp.sample.chat.selectAllUsers
import com.sitharaj.reduxkmp.sample.chat.selectMessagesWithUsers
import com.sitharaj.reduxkmp.sample.chat.selectTypingUsers
import com.sitharaj.reduxkmp.sample.chat.selectUnreadCount
import kotlinx.coroutines.launch

// ============================================
// Color Palette
// ============================================
private val PrimaryPurple = Color(0xFF7C4DFF)
private val PrimaryPurpleLight = Color(0xFFB388FF)
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkCard = Color(0xFF2D2D2D)
private val MessageBubbleSent = Color(0xFF7C4DFF)
private val MessageBubbleReceived = Color(0xFF2D2D2D)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFB0B0B0)
private val OnlineGreen = Color(0xFF4CAF50)

@Composable
fun App() {
    val store = remember { ChatStoreProvider.getStore() }
    val state by store.state.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Initialize store on first composition
    LaunchedEffect(Unit) {
        ChatStoreProvider.initialize(store)
        store.dispatch(FetchChatHistoryThunk())
    }
    
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = PrimaryPurple,
            surface = DarkSurface,
            background = DarkBackground
        )
    ) {
        ChatScreen(
            state = state,
            onSendMessage = { content ->
                scope.launch {
                    store.dispatch(SendMessageThunk(content, state.users.currentUserId))
                    store.dispatch(UIAction.SetInputText(""))
                }
            },
            onInputChange = { text ->
                store.dispatch(UIAction.SetInputText(text))
            },
            onInputFocused = { focused ->
                store.dispatch(UIAction.SetInputFocused(focused))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatState,
    onSendMessage: (String) -> Unit,
    onInputChange: (String) -> Unit,
    onInputFocused: (Boolean) -> Unit
) {
    val messagesWithUsers = selectMessagesWithUsers(state)
    val unreadCount = selectUnreadCount(state)
    val typingUsers = selectTypingUsers(state)
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messagesWithUsers.size) {
        if (messagesWithUsers.isNotEmpty()) {
            listState.animateScrollToItem(messagesWithUsers.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            ChatTopBar(
                title = "Redux KMP Chat",
                subtitle = when {
                    typingUsers.isNotEmpty() -> "${typingUsers.first().name} is typing..."
                    else -> "${selectAllUsers(state).count { it.isOnline }} online"
                },
                unreadCount = unreadCount
            )
        },
        bottomBar = {
            ChatInput(
                value = state.ui.inputText,
                onValueChange = onInputChange,
                onSend = {
                    if (state.ui.inputText.isNotBlank()) {
                        onSendMessage(state.ui.inputText)
                    }
                },
                isSending = state.messages.sendingMessageIds.isNotEmpty()
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Feature Banner
            FeatureBanner()
            
            // Messages List
            if (state.messages.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryPurple)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading messages...", color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(messagesWithUsers, key = { it.message.id }) { item ->
                        MessageBubble(
                            messageWithUser = item,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    title: String,
    subtitle: String,
    unreadCount: Int
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = Color(0xFFFF5252)
                        ) {
                            Text("$unreadCount")
                        }
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkSurface,
            titleContentColor = TextPrimary
        ),
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = TextSecondary
                )
            }
        }
    )
}

@Composable
fun FeatureBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryPurple.copy(alpha = 0.3f),
                            Color(0xFF00BCD4).copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Redux KMP Features Demo",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FeatureChip("EntityAdapter")
                    FeatureChip("Selectors")
                    FeatureChip("AsyncThunk")
                }
            }
        }
    }
}

@Composable
fun FeatureChip(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard.copy(alpha = 0.8f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = PrimaryPurpleLight
        )
    }
}

@Composable
fun MessageBubble(
    messageWithUser: MessageWithUser,
    modifier: Modifier = Modifier
) {
    val isCurrentUser = messageWithUser.isCurrentUser
    val message = messageWithUser.message
    val user = messageWithUser.user
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isCurrentUser) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.avatarUrl ?: "?",
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            if (!isCurrentUser && user != null) {
                Text(
                    text = user.name,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 2.dp)
                )
            }
            
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                ),
                color = if (isCurrentUser) MessageBubbleSent else MessageBubbleReceived
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.content,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(message.timestamp),
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        if (isCurrentUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            MessageStatusIcon(message.status)
                        }
                    }
                }
            }
        }
        
        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun MessageStatusIcon(status: MessageStatus) {
    val icon = when (status) {
        MessageStatus.SENDING -> Icons.Default.Schedule
        MessageStatus.SENT -> Icons.Default.Check
        MessageStatus.DELIVERED -> Icons.Default.DoneAll
        MessageStatus.READ -> Icons.Default.DoneAll
        MessageStatus.FAILED -> Icons.Default.Error
    }
    
    val tint = when (status) {
        MessageStatus.READ -> Color(0xFF4FC3F7)
        MessageStatus.FAILED -> Color(0xFFFF5252)
        else -> TextSecondary
    }
    
    Icon(
        imageVector = icon,
        contentDescription = status.name,
        tint = tint,
        modifier = Modifier.size(14.dp)
    )
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean
) {
    Surface(
        color = DarkSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.EmojiEmotions,
                    contentDescription = "Emoji",
                    tint = TextSecondary
                )
            }
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...", color = TextSecondary) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    cursorColor = PrimaryPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FloatingActionButton(
                onClick = onSend,
                containerColor = if (value.isNotBlank()) PrimaryPurple else DarkCard,
                contentColor = if (value.isNotBlank()) Color.White else TextSecondary,
                modifier = Modifier.size(48.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}




private fun formatTime(timestamp: Long): String {
    val now = currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}
