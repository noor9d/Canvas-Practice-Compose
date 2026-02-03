package com.imagination.canvaspractice.domain.repository

import com.imagination.canvaspractice.data.local.database.entity.BoardEntity
import com.imagination.canvaspractice.domain.model.PathData
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.TextData
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Board operations
 * Abstracts the data source implementation
 */
interface BoardRepository {
    /**
     * Get all boards as a Flow
     */
    fun getAllBoards(): Flow<List<BoardEntity>>

    /**
     * Get a board by its ID
     */
    suspend fun getBoardById(boardId: Long): BoardEntity?

    /**
     * Insert a new board and return its ID
     */
    suspend fun insertBoard(board: BoardEntity): Long

    /**
     * Update an existing board
     */
    suspend fun updateBoard(board: BoardEntity)

    /**
     * Delete a board by ID
     */
    suspend fun deleteBoard(boardId: Long)

    /**
     * Update board timestamp
     */
    suspend fun updateBoardTimestamp(boardId: Long, timestamp: Long = System.currentTimeMillis())

    // Path operations
    fun getPathsByBoardId(boardId: Long): Flow<List<PathData>>
    suspend fun insertPath(path: PathData, boardId: Long)
    suspend fun insertPaths(paths: List<PathData>, boardId: Long)
    suspend fun updatePath(path: PathData, boardId: Long)
    suspend fun deletePath(pathId: String)
    suspend fun deletePathsByBoardId(boardId: Long)

    // Text operations
    fun getTextsByBoardId(boardId: Long): Flow<List<TextData>>
    suspend fun insertText(text: TextData, boardId: Long)
    suspend fun insertTexts(texts: List<TextData>, boardId: Long)
    suspend fun updateText(text: TextData, boardId: Long)
    suspend fun deleteText(textId: String)
    suspend fun deleteTextsByBoardId(boardId: Long)

    // Shape operations
    fun getShapesByBoardId(boardId: Long): Flow<List<ShapeData>>
    suspend fun insertShape(shape: ShapeData, boardId: Long)
    suspend fun insertShapes(shapes: List<ShapeData>, boardId: Long)
    suspend fun updateShape(shape: ShapeData, boardId: Long)
    suspend fun deleteShape(shapeId: String)
    suspend fun deleteShapesByBoardId(boardId: Long)

    // Load complete board data
    suspend fun loadBoardData(boardId: Long): Triple<List<PathData>, List<TextData>, List<ShapeData>>
}
