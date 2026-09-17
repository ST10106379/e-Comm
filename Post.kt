package com.example.e_comm

data class Post(
    var id: String = "",
    val authorName: String = "",
    val authorUid: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var likeCount: Int = 0,
    var commentCount: Int = 0
)

data class Comment(
    var id: String = "",
    val authorName: String = "",
    val authorUid: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
