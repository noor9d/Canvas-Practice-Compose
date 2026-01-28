package com.imagination.canvaspractice.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imagination.canvaspractice.data.local.database.entity.BoardEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Board operations
 */
@Dao
interface BoardDao {
    @Query("SELECT * FROM boards ORDER BY updatedAt DESC")
    fun getAllBoards(): Flow<List<BoardEntity>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    suspend fun getBoardById(boardId: Long): BoardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity): Long

    @Update
    suspend fun updateBoard(board: BoardEntity)

    @Query("DELETE FROM boards WHERE id = :boardId")
    suspend fun deleteBoard(boardId: Long)

    @Query("UPDATE boards SET updatedAt = :timestamp WHERE id = :boardId")
    suspend fun updateBoardTimestamp(boardId: Long, timestamp: Long = System.currentTimeMillis())
}
