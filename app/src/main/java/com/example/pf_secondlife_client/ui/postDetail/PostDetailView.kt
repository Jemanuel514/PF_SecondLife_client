package com.example.pf_secondlife_client.ui.postDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.ui.ImageUrlBuilder
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PostDetailContent(
    post: GetPostResponse,
    onBuyClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageUrlBuilder.build(post.thumbnailPath),
            contentDescription = post.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp)),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = post.title, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$${post.price}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        DetailRow(label = "Type", value = post.type.name)
        DetailRow(label = "Brand", value = post.brand)
        DetailRow(label = "Size", value = post.size)
        DetailRow(label = "Color", value = post.color)
        post.observations?.let { observations ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Observations", style = MaterialTheme.typography.titleSmall)
            Text(text = observations, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Seller: ${post.author.username}", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "Posted ${formatTimestamp(post.timestamp)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBuyClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Buy")
        }
    }
}

@Composable
fun PostDetailView(
    postId: String,
    onNavigateToLogin: () -> Unit,
    onNavigateToPayment: (postId: String) -> Unit,
    viewModel: PostDetailViewModel = koinViewModel(parameters = { parametersOf(postId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val purchaseCheckResult by viewModel.purchaseCheckResult.collectAsState()

    purchaseCheckResult?.let { result ->
        when (result) {
            is PurchaseCheckResult.RequiresLogin -> {
                LaunchedEffect(result) {
                    viewModel.consumePurchaseCheckResult()
                    onNavigateToLogin()
                }
            }
            is PurchaseCheckResult.Allowed -> {
                LaunchedEffect(result) {
                    viewModel.consumePurchaseCheckResult()
                    onNavigateToPayment(result.postId)
                }
            }
            is PurchaseCheckResult.NotAvailable -> {
                AlertDialog(
                    onDismissRequest = viewModel::consumePurchaseCheckResult,
                    confirmButton = {
                        TextButton(onClick = viewModel::consumePurchaseCheckResult) { Text(text = "Continue") }
                    },
                    title = { Text(text = "Not available") },
                    text = { Text(text = "This post is no longer available.") },
                )
            }
            is PurchaseCheckResult.IsOwner -> {
                AlertDialog(
                    onDismissRequest = viewModel::consumePurchaseCheckResult,
                    confirmButton = {
                        TextButton(onClick = viewModel::consumePurchaseCheckResult) { Text(text = "Continue") }
                    },
                    title = { Text(text = "Not allowed action") },
                    text = { Text(text = "Can't buy your own posts.") },
                )
            }
            is PurchaseCheckResult.Error -> {
                AlertDialog(
                    onDismissRequest = viewModel::consumePurchaseCheckResult,
                    confirmButton = {
                        TextButton(onClick = viewModel::consumePurchaseCheckResult) { Text(text = "Continue") }
                    },
                    title = { Text(text = "Error") },
                    text = { Text(text = result.message) },
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is PostDetailModel.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is PostDetailModel.Success -> {
                PostDetailContent(
                    post = state.post,
                    onBuyClick = viewModel::onBuyClicked,
                )
            }
            is PostDetailModel.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = state.message)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadPost() }) { Text(text = "Reload") }
                }
            }
        }
    }
}