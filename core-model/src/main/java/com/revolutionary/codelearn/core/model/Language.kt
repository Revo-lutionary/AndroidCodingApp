package com.revolutionary.codelearn.core.model

enum class Language(val id: String, val displayName: String) {
    PYTHON("python", "Python"),
    LUA("lua", "Lua(u)"),
    CPP("cpp", "C++");

    companion object {
        fun fromId(id: String): Language =
            entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("Unknown language id: $id")
    }
}
