package com.imagination.canvaspractice.di

import android.content.Context
import androidx.room.Room
import com.imagination.canvaspractice.data.local.database.CanvasDatabase
import com.imagination.canvaspractice.data.local.database.dao.BoardDao
import com.imagination.canvaspractice.data.local.database.dao.PathDao
import com.imagination.canvaspractice.data.local.database.dao.ShapeDao
import com.imagination.canvaspractice.data.local.database.dao.TextDao
import com.imagination.canvaspractice.data.repository.BoardRepositoryImpl
import com.imagination.canvaspractice.domain.repository.BoardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CanvasDatabase {
        return Room.databaseBuilder(
            context,
            CanvasDatabase::class.java,
            "canvas_database"
        )
        .fallbackToDestructiveMigration() // For development - will recreate DB on schema change
        .build()
    }

    @Provides
    fun provideBoardDao(database: CanvasDatabase) = database.boardDao()

    @Provides
    fun providePathDao(database: CanvasDatabase) = database.pathDao()

    @Provides
    fun provideTextDao(database: CanvasDatabase) = database.textDao()

    @Provides
    fun provideShapeDao(database: CanvasDatabase) = database.shapeDao()

    @Provides
    fun provideBoardRepository(
        boardDao: BoardDao,
        pathDao: PathDao,
        textDao: TextDao,
        shapeDao: ShapeDao
    ): BoardRepository {
        return BoardRepositoryImpl(
            boardDao = boardDao,
            pathDao = pathDao,
            textDao = textDao,
            shapeDao = shapeDao
        )
    }
}
