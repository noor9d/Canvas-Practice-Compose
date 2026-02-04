package com.imagination.canvaspractice.data.local.database.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.imagination.canvaspractice.data.local.database.converters.ColorConverter
import com.imagination.canvaspractice.data.local.database.converters.OffsetListConverter
import com.imagination.canvaspractice.domain.model.PathData

/**
 * Room entity representing a Path (pen drawing)
 */
@Entity(
    tableName = "paths",
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["id"],
            childColumns = ["boardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["boardId"])]
)
data class PathEntity(
    @PrimaryKey
    val id: String,
    val boardId: Long,
    val color: Long, // Stored as Long, converted using ColorConverter
    val strokeWidth: Float,
    val path: String, // Stored as String, converted using OffsetListConverter
    val groupId: String? = null
) {
    companion object {
        fun fromDomain(
            id: String,
            boardId: Long,
            path: List<Offset>,
            color: Color,
            strokeWidth: Float,
            groupId: String? = null
        ): PathEntity {
            val colorConverter = ColorConverter()
            val offsetConverter = OffsetListConverter()
            return PathEntity(
                id = id,
                boardId = boardId,
                color = colorConverter.fromColor(color),
                strokeWidth = strokeWidth,
                path = offsetConverter.fromOffsetList(path),
                groupId = groupId
            )
        }

        fun toDomain(pathEntity: PathEntity): PathData {
            val colorConverter = ColorConverter()
            val offsetConverter = OffsetListConverter()
            return PathData(
                id = pathEntity.id,
                color = colorConverter.toColor(pathEntity.color),
                strokeWidth = pathEntity.strokeWidth,
                path = offsetConverter.toOffsetList(pathEntity.path),
                groupId = pathEntity.groupId
            )
        }
    }
}
