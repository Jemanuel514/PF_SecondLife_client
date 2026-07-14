package com.example.pf_secondlife_client.network.api.users

object UsersEndpoints {
    const val REGISTER = "/register"
    const val LOGIN = "/login"
    const val LOGOUT = "/logout"
    const val BASE = "/users"

    fun byId(userId: String) = "$BASE/$userId"
    fun postsByUserId(userId: String) = "$BASE/$userId/posts"
}