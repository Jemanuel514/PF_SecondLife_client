package com.example.pf_secondlife_client.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pf_secondlife_client.data.session.sessionManagers.SessionManager
import com.example.pf_secondlife_client.ui.catalog.CatalogView
import com.example.pf_secondlife_client.ui.createPost.CreatePostFormView
import com.example.pf_secondlife_client.ui.editPost.EditPostFormView
import com.example.pf_secondlife_client.ui.payment.PaymentFormView
import com.example.pf_secondlife_client.ui.postDetail.PostDetailView
import com.example.pf_secondlife_client.ui.user.UserTabRoot
import org.koin.compose.koinInject

@Composable
private fun AppBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Routes.Catalog.route,
            onClick = { navController.navigate(Routes.Catalog.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            } },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Catalog") },
            label = { Text(text = "Catalog") }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Profile.route,
            onClick = { navController.navigate(Routes.Profile.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            } },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text(text = "Profile") }
        )
    }
}

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val sessionManager: SessionManager = koinInject()
    val userId by sessionManager.userId.collectAsState()
    var wasLoggedIn by remember { mutableStateOf(userId != null) }

    LaunchedEffect(userId) {
        if (wasLoggedIn && userId == null) {
            navController.navigate(Routes.Profile.route) {
                popUpTo(Routes.Catalog.route) { inclusive = false }
            }
        }
        wasLoggedIn = userId != null
    }

    val showBottomBar = currentRoute == Routes.Catalog.route || currentRoute == Routes.Profile.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Catalog.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(route = Routes.Catalog.route) {
                CatalogView(
                    onPostClick = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId) )}
                )
            }

            composable(
                route = Routes.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { entry ->
                val postId = entry.arguments!!.getString("postId")!!
                PostDetailView(
                    postId = postId,
                    onNavigateToLogin = {
                        navController.navigate(Routes.Profile.route) {
                            popUpTo(Routes.Catalog.route) { inclusive = false }
                        } },
                    onNavigateToPayment = { id -> navController.navigate(Routes.Payment.createRoute(id)) }
                )

            }

            composable(
                route = Routes.Payment.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { entry ->
                val postId = entry.arguments!!.getString("postId")!!
                PaymentFormView(
                    postId = postId,
                    onPaymentSuccess = { navController.popBackStack(Routes.Catalog.route, false) },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(Routes.Profile.route) {
                UserTabRoot(
                    onCreatePost = { navController.navigate(Routes.CreatePost.route) },
                    onEditPost = { postId -> navController.navigate(Routes.EditPost.createRoute(postId)) }
                )
            }

            composable(Routes.CreatePost.route) {
                CreatePostFormView(
                    onCreated = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EditPost.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { entry ->
                val postId = entry.arguments!!.getString("postId")!!
                EditPostFormView(
                    postId = postId,
                    onSaved = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}