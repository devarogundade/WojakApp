package dev.arogundade.wojak.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "metadata")
data class Metadata(
    val bitcointalk_url: String?,
    val block_explorer_url: String?,
    val blog_url: String?,
    val cryptocontrol_coin_id: String,
    val description: String?,
    val discord_url: String?,
    val facebook_url: String?,
    val github_url: String?,
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val linkedin_url: String?,
    val logo_url: String,
    val medium_url: String?,
    val name: String,
    val original_symbol: String?,
    val reddit_url: String?,
    val replaced_by: String?,
    val telegram_url: String?,
    val twitter_url: String?,
    val used_for_pricing: Boolean?,
    val website_url: String?,
    val whitepaper_url: String?,
    val youtube_url: String?
) : Serializable