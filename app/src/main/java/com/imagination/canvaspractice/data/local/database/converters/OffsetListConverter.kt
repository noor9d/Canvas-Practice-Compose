package com.imagination.canvaspractice.data.local.database.converters

import androidx.compose.ui.geometry.Offset
import androidx.room.TypeConverter

/**
 * Type converter for List<Offset> to store it as String in Room database
 */
class OffsetListConverter {
    @TypeConverter
    fun fromOffsetList(offsets: List<Offset>): String {
        return offsets.joinToString("|") { "${it.x},${it.y}" }
    }

    @TypeConverter
    fun toOffsetList(value: String): List<Offset> {
        if (value.isEmpty()) return emptyList()
        return value.split("|").map { part ->
            val coords = part.split(",")
            Offset(coords[0].toFloat(), coords[1].toFloat())
        }
    }
}
