package com.example.pf_secondlife_client.ui.user.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil.compose.AsyncImage
import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.domain.valuesSets.PostState
import com.example.pf_secondlife_client.ui.imageManagement.ImageUrlBuilder
import com.example.pf_secondlife_client.ui.components.ArticleTypeFilterRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))

@Composable
private fun ProfileHeader(username: String, email: String) {
    Column {
        Text(text = username, style = MaterialTheme.typography.headlineSmall)
        Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfilePostItem(
    post: GetPostResponse,
    onToggleVisibility: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageUrlBuilder.build(post.thumbnailPath),
                    contentDescription = post.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "$${post.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = formatDate(post.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (post.state == PostState.SOLD) {
                        Text(
                            text = "SOLD",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (post.state != PostState.SOLD) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (post.state == PostState.HIDDEN) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (post.state == PostState.HIDDEN) "Show" else "Hide",
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileView(
    userId: String,
    onCreatePost: () -> Unit,
    onEditPost: (postId: String) -> Unit,
    viewModel: ProfileViewModel = koinViewModel(
        key = "profile-$userId",
        parameters = { parametersOf(userId) }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionError by viewModel.actionError.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadProfile()
    }

    actionError?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::consumeActionError,
            confirmButton = {
                TextButton(onClick = viewModel::consumeActionError) { Text(text = "Continue") }
            },
            title = { Text(text = "Error") },
            text = { Text(text = message) },
        )
    }

    when (val state = uiState) {
        is ProfileModel.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is ProfileModel.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = state.message)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.loadProfile() }) { Text(text = "Retry") }
            }
        }

        is ProfileModel.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { ProfileHeader(username = state.user.username, email = state.user.email) }

                item {
                    Button(
                        onClick = onCreatePost,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New post")
                    }
                }

                item {
                    OutlinedButton(
                        onClick = viewModel::logout,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout")
                    }
                }

                item {
                    ArticleTypeFilterRow(
                        selectedFilter = state.statsFilter,
                        onFilterSelected = viewModel::setStatsFilter,
                    )
                }

                item { StatsDashboard(stats = state.getStats()) }

                item {
                    Text(text = "My posts", style = MaterialTheme.typography.titleLarge)
                }

                items(state.posts) { post ->
                    ProfilePostItem(
                        post = post,
                        onToggleVisibility = { viewModel.toggleVisibility(post.id, post.state) },
                        onEdit = { onEditPost(post.id) },
                        onDelete = { viewModel.deletePost(post.id) },
                    )
                }
            }
        }
    }
}



