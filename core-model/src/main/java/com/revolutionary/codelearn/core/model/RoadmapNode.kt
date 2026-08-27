package com.revolutionary.codelearn.core.model

enum class NodeType { LESSON, QUIZ }

data class RoadmapNode(
    val id: String,
    val type: NodeType,
    val title: String,
)
