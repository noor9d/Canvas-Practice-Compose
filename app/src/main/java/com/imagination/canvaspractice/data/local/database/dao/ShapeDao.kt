package com.imagination.canvaspractice.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imagination.canvaspractice.data.local.database.entity.ShapeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Shape operations
 */
@Dao
interface ShapeDao {
    @Query("SELECT * FROM shapes WHERE boardId = :boardId")
    fun getShapesByBoardId(boardId: Long): Flow<List<ShapeEntity>>

    @Query("SELECT * FROM shapes WHERE boardId = :boardId")
    suspend fun getShapesByBoardIdSync(boardId: Long): List<ShapeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShape(shape: ShapeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShapes(shapes: List<ShapeEntity>)

    @Query("DELETE FROM shapes WHERE boardId = :boardId")
    suspend fun deleteShapesByBoardId(boardId: Long)

    @Query("DELETE FROM shapes WHERE id = :shapeId")
    suspend fun deleteShape(shapeId: String)
}
