package com.revolutionary.codelearn.di

import android.content.Context
import androidx.room.Room
import com.revolutionary.codelearn.core.curriculum.CurriculumRepository
import com.revolutionary.codelearn.core.data.AppDatabase
import com.revolutionary.codelearn.core.data.IdeTabDao
import com.revolutionary.codelearn.core.data.IdeTabRepository
import com.revolutionary.codelearn.core.data.LessonProgressDao
import com.revolutionary.codelearn.core.data.ProgressRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCurriculumRepository(@ApplicationContext context: Context): CurriculumRepository =
        CurriculumRepository(context)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            // Pre-release app, no shipped user data to preserve across schema changes.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideLessonProgressDao(database: AppDatabase): LessonProgressDao =
        database.lessonProgressDao()

    @Provides
    @Singleton
    fun provideProgressRepository(dao: LessonProgressDao): ProgressRepository =
        ProgressRepository(dao)

    @Provides
    fun provideIdeTabDao(database: AppDatabase): IdeTabDao =
        database.ideTabDao()

    @Provides
    @Singleton
    fun provideIdeTabRepository(dao: IdeTabDao): IdeTabRepository =
        IdeTabRepository(dao)
}
