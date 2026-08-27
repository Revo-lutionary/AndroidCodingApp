package com.revolutionary.codelearn.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ide_tabs")
data class IdeTabEntity(
    @PrimaryKey val id: String,
    val title: String,
    val languageId: String,
    val code: String,
    val position: Int,
)
