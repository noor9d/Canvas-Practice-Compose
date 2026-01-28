package com.imagination.canvaspractice.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imagination.canvaspractice.data.local.database.entity.PathEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Path operations
 */
@Dao
interface PathDao {
    @Query("SELECT * FROM paths WHERE boardId = :boardId")
    fun getPathsByBoardId(boardId: Long): Flow<List<PathEntity>>

    @Query("SELECT * FROM paths WHERE boardId = :boardId")
    suspend fun getPathsByBoardIdSync(boardId: Long): List<PathEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPath(path: PathEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaths(paths: List<PathEntity>)

    @Query("DELETE FROM paths WHERE boardId = :boardId")
    suspend fun deletePathsByBoardId(boardId: Long)

    @Query("DELETE FROM paths WHERE id = :pathId")
    suspend fun deletePath(pathId: String)
}
