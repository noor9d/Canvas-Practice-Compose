package com.imagination.canvaspractice.data.local.database.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.imagination.canvaspractice.data.local.database.converters.ColorConverter
import com.imagination.canvaspractice.data.local.database.converters.OffsetConverter
import com.imagination.canvaspractice.domain.model.TextData

/**
 * Room entity representing a Text element
 */
@Entity(
    tableName = "texts",
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
data class TextEntity(
    @PrimaryKey
    val id: String,
    val boardId: Long,
    val text: String,
    val position: String, // Stored as String, converted using OffsetConverter
    val color: Long, // Stored as Long, converted using ColorConverter
    val fontSize: Float,
    val groupId: String? = null
) {
    companion object {
        fun fromDomain(
            id: String,
            boardId: Long,
            text: String,
            position: Offset,
            color: Color,
            fontSize: Float,
            groupId: String? = null
        ): TextEntity {
            val colorConverter = ColorConverter()
            val offsetConverter = OffsetConverter()
            return TextEntity(
                id = id,
                boardId = boardId,
                text = text,
                position = offsetConverter.fromOffset(position),
                color = colorConverter.fromColor(color),
                fontSize = fontSize,
                groupId = groupId
            )
        }

        fun toDomain(textEntity: TextEntity): TextData {
            val colorConverter = ColorConverter()
            val offsetConverter = OffsetConverter()
            return TextData(
                id = textEntity.id,
                text = textEntity.text,
                position = offsetConverter.toOffset(textEntity.position),
                color = colorConverter.toColor(textEntity.color),
                fontSize = textEntity.fontSize,
                groupId = textEntity.groupId
            )
        }
    }
}
