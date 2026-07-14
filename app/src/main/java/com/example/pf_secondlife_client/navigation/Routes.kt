package com.example.pf_secondlife_client.navigation

sealed class Routes(val route: String) {
    object Catalog: Routes("catalog")
    object Profile: Routes("profile")
    object CreatePost: Routes("create_post")
    object PostDetail: Routes("post_detail/{postId}") {
        fun createRoute(postId: String): String {
            return "post_detail/$postId"
        }
    }
    object Payment: Routes("payment/{postId}") {
        fun createRoute(postId: String): String {
            return "payment/$postId"
        }
    }
    object EditPost: Routes("edit_post/{postId}") {
        fun createRoute(postId: String): String {
            return "edit_post/$postId"
        }
    }
}