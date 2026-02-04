package com.imagination.canvaspractice.data.repository

import com.imagination.canvaspractice.data.local.database.dao.BoardDao
import com.imagination.canvaspractice.data.local.database.dao.PathDao
import com.imagination.canvaspractice.data.local.database.dao.ShapeDao
import com.imagination.canvaspractice.data.local.database.dao.TextDao
import com.imagination.canvaspractice.data.local.database.entity.BoardEntity
import com.imagination.canvaspractice.data.local.database.entity.PathEntity
import com.imagination.canvaspractice.data.local.database.entity.ShapeEntity
import com.imagination.canvaspractice.data.local.database.entity.TextEntity
import com.imagination.canvaspractice.domain.model.PathData
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.TextData
import com.imagination.canvaspractice.domain.repository.BoardRepository as BoardRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of BoardRepository interface
 * Handles data operations using Room database
 */
class BoardRepositoryImpl @Inject constructor(
    private val boardDao: BoardDao,
    private val pathDao: PathDao,
    private val textDao: TextDao,
    private val shapeDao: ShapeDao
) : BoardRepositoryInterface {
    override fun getAllBoards(): Flow<List<BoardEntity>> = boardDao.getAllBoards()

    override suspend fun getBoardById(boardId: Long): BoardEntity? = boardDao.getBoardById(boardId)

    override suspend fun insertBoard(board: BoardEntity): Long {
        val boardId = boardDao.insertBoard(board)
        return if (boardId == 0L) board.id else boardId
    }

    override suspend fun updateBoard(board: BoardEntity) = boardDao.updateBoard(board)

    override suspend fun deleteBoard(boardId: Long) = boardDao.deleteBoard(boardId)

    override suspend fun updateBoardTimestamp(boardId: Long, timestamp: Long) =
        boardDao.updateBoardTimestamp(boardId, timestamp)

    // Path operations
    override fun getPathsByBoardId(boardId: Long): Flow<List<PathData>> =
        pathDao.getPathsByBoardId(boardId).map { paths ->
            paths.map { PathEntity.toDomain(it) }
        }

    override suspend fun insertPath(path: PathData, boardId: Long) {
        val pathEntity = PathEntity.fromDomain(
            id = path.id,
            boardId = boardId,
            path = path.path,
            color = path.color,
            strokeWidth = path.strokeWidth,
            groupId = path.groupId
        )
        pathDao.insertPath(pathEntity)
        updateBoardTimestamp(boardId)
    }

    override suspend fun insertPaths(paths: List<PathData>, boardId: Long) {
        val pathEntities = paths.map { path ->
            PathEntity.fromDomain(
                id = path.id,
                boardId = boardId,
                path = path.path,
                color = path.color,
                strokeWidth = path.strokeWidth,
                groupId = path.groupId
            )
        }
        pathDao.insertPaths(pathEntities)
        updateBoardTimestamp(boardId)
    }

    override suspend fun updatePath(path: PathData, boardId: Long) {
        insertPath(path, boardId)
    }

    override suspend fun deletePath(pathId: String) = pathDao.deletePath(pathId)

    override suspend fun deletePathsByBoardId(boardId: Long) = pathDao.deletePathsByBoardId(boardId)

    // Text operations
    override fun getTextsByBoardId(boardId: Long): Flow<List<TextData>> =
        textDao.getTextsByBoardId(boardId).map { texts ->
            texts.map { TextEntity.toDomain(it) }
        }

    override suspend fun insertText(text: TextData, boardId: Long) {
        val textEntity = TextEntity.fromDomain(
            id = text.id,
            boardId = boardId,
            text = text.text,
            position = text.position,
            color = text.color,
            fontSize = text.fontSize,
            groupId = text.groupId
        )
        textDao.insertText(textEntity)
        updateBoardTimestamp(boardId)
    }

    override suspend fun insertTexts(texts: List<TextData>, boardId: Long) {
        val textEntities = texts.map { text ->
            TextEntity.fromDomain(
                id = text.id,
                boardId = boardId,
                text = text.text,
                position = text.position,
                color = text.color,
                fontSize = text.fontSize,
                groupId = text.groupId
            )
        }
        textDao.insertTexts(textEntities)
        updateBoardTimestamp(boardId)
    }

    override suspend fun updateText(text: TextData, boardId: Long) {
        insertText(text, boardId)
    }

    override suspend fun deleteText(textId: String) = textDao.deleteText(textId)

    override suspend fun deleteTextsByBoardId(boardId: Long) = textDao.deleteTextsByBoardId(boardId)

    // Shape operations
    override fun getShapesByBoardId(boardId: Long): Flow<List<ShapeData>> =
        shapeDao.getShapesByBoardId(boardId).map { shapes ->
            shapes.map { ShapeEntity.toDomain(it) }
        }

    override suspend fun insertShape(shape: ShapeData, boardId: Long) {
        val shapeEntity = ShapeEntity.fromDomain(
            id = shape.id,
            boardId = boardId,
            type = shape.type,
            startPosition = shape.startPosition,
            endPosition = shape.endPosition,
            color = shape.color,
            strokeWidth = shape.strokeWidth,
            isFilled = shape.isFilled,
            groupId = shape.groupId
        )
        shapeDao.insertShape(shapeEntity)
        updateBoardTimestamp(boardId)
    }

    override suspend fun insertShapes(shapes: List<ShapeData>, boardId: Long) {
        val shapeEntities = shapes.map { shape ->
            ShapeEntity.fromDomain(
                id = shape.id,
                boardId = boardId,
                type = shape.type,
                startPosition = shape.startPosition,
                endPosition = shape.endPosition,
                color = shape.color,
                strokeWidth = shape.strokeWidth,
                isFilled = shape.isFilled,
                groupId = shape.groupId
            )
        }
        shapeDao.insertShapes(shapeEntities)
        updateBoardTimestamp(boardId)
    }

    override suspend fun updateShape(shape: ShapeData, boardId: Long) {
        insertShape(shape, boardId)
    }

    override suspend fun deleteShape(shapeId: String) = shapeDao.deleteShape(shapeId)

    override suspend fun deleteShapesByBoardId(boardId: Long) = shapeDao.deleteShapesByBoardId(boardId)

    // Load complete board data
    override suspend fun loadBoardData(boardId: Long): Triple<List<PathData>, List<TextData>, List<ShapeData>> {
        val paths = pathDao.getPathsByBoardIdSync(boardId).map { PathEntity.toDomain(it) }
        val texts = textDao.getTextsByBoardIdSync(boardId).map { TextEntity.toDomain(it) }
        val shapes = shapeDao.getShapesByBoardIdSync(boardId).map { ShapeEntity.toDomain(it) }
        return Triple(paths, texts, shapes)
    }
}
