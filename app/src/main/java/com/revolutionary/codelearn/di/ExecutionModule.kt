package com.revolutionary.codelearn.di

import com.revolutionary.codelearn.core.execution.ExecutionEngine
import com.revolutionary.codelearn.engine.lua.LuaExecutionEngine
import com.revolutionary.codelearn.execution.python.PythonExecutionEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ExecutionModule {

    @Binds
    @IntoSet
    abstract fun bindLuaEngine(engine: LuaExecutionEngine): ExecutionEngine

    @Binds
    @IntoSet
    abstract fun bindPythonEngine(engine: PythonExecutionEngine): ExecutionEngine
}
