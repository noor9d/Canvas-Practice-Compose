package com.imagination.canvaspractice.data.mapper

import androidx.compose.ui.geometry.Offset
import com.imagination.canvaspractice.data.local.database.entity.BoardEntity
import com.imagination.canvaspractice.presentation.dashboard.model.Board

/**
 * Mapper to convert between BoardEntity (Room) and Board (Domain/Presentation)
 */
object BoardMapper {
    fun BoardEntity.toDomain(): Board {
        return Board(
            id = id.toInt(),
            title = title,
            thumbnailUrl = null,
            scale = scale,
            panOffset = Offset(panOffsetX, panOffsetY)
        )
    }

    fun Board.toEntity(): BoardEntity {
        return BoardEntity(
            id = id.toLong(),
            title = title,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            scale = scale,
            panOffsetX = panOffset.x,
            panOffsetY = panOffset.y
        )
    }

    fun List<BoardEntity>.toDomain(): List<Board> {
        return map { it.toDomain() }
    }
}
