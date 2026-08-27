package com.revolutionary.codelearn.core.model

data class Track(
    val id: String,
    val language: Language,
    val title: String,
    val nodes: List<RoadmapNode>,
)
