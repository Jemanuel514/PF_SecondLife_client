package com.example.pf_secondlife_client.ui.catalog

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.pf_secondlife_client.ui.imageManagement.ImageUrlBuilder
import com.example.pf_secondlife_client.ui.components.ArticleTypeFilterRow
import org.koin.androidx.compose.koinViewModel

@Composable
private fun PostListItem(
    post: GetPostResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface (
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageUrlBuilder.build(post.thumbnailPath),
                contentDescription = post.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${post.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun CatalogView(
    onPostClick: (postId: String) -> Unit,
    viewModel: CatalogViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadPosts()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is CatalogModel.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is CatalogModel.Success -> {
                ArticleTypeFilterRow(
                    selectedFilter = state.selectedFilter,
                    onFilterSelected = viewModel::setFilter,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredPosts) { post ->
                        PostListItem(
                            post,
                            onClick = { onPostClick(post.id) }
                        )
                    }
                }
            }

            is CatalogModel.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = state.message)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadPosts() }) {
                        Text(text = "Reload")
                    }
                }
            }
        }
    }
}