package com.imagination.canvaspractice.data.local.database.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.imagination.canvaspractice.data.local.database.converters.ColorConverter
import com.imagination.canvaspractice.data.local.database.converters.OffsetConverter
import com.imagination.canvaspractice.data.local.database.converters.ShapeTypeConverter
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.ShapeType

/**
 * Room entity representing a Shape element
 */
@Entity(
    tableName = "shapes",
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
data class ShapeEntity(
    @PrimaryKey
    val id: String,
    val boardId: Long,
    val type: String, // Stored as String, converted using ShapeTypeConverter
    val startPosition: String, // Stored as String, converted using OffsetConverter
    val endPosition: String, // Stored as String, converted using OffsetConverter
    val color: Long, // Stored as Long, converted using ColorConverter
    val strokeWidth: Float,
    val isFilled: Boolean = false,
    val groupId: String? = null
) {
    companion object {
        fun fromDomain(
            id: String,
            boardId: Long,
            type: ShapeType,
            startPosition: Offset,
            endPosition: Offset,
            color: Color,
            strokeWidth: Float,
            isFilled: Boolean = false,
            groupId: String? = null
        ): ShapeEntity {
            val colorConverter = ColorConverter()
            val offsetConverter = OffsetConverter()
            val typeConverter = ShapeTypeConverter()
            return ShapeEntity(
                id = id,
                boardId = boardId,
                type = typeConverter.fromShapeType(type),
                startPosition = offsetConverter.fromOffset(startPosition),
                endPosition = offsetConverter.fromOffset(endPosition),
                color = colorConverter.fromColor(color),
                strokeWidth = strokeWidth,
                isFilled = isFilled,
                groupId = groupId
            )
        }

        fun toDomain(shapeEntity: ShapeEntity): ShapeData {
            val colorConverter = ColorConverter()
            val offsetConverter = OffsetConverter()
            val typeConverter = ShapeTypeConverter()
            return ShapeData(
                id = shapeEntity.id,
                type = typeConverter.toShapeType(shapeEntity.type),
                startPosition = offsetConverter.toOffset(shapeEntity.startPosition),
                endPosition = offsetConverter.toOffset(shapeEntity.endPosition),
                color = colorConverter.toColor(shapeEntity.color),
                strokeWidth = shapeEntity.strokeWidth,
                isFilled = shapeEntity.isFilled,
                groupId = shapeEntity.groupId
            )
        }
    }
}
