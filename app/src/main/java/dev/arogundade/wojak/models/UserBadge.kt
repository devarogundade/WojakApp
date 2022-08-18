package dev.arogundade.wojak.models

data class UserBadge(
    val rank: Int,
    val title: String,
    val icon: Int,
    val range: IntRange
)