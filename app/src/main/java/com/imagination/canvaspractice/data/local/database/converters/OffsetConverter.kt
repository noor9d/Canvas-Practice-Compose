package com.imagination.canvaspractice.data.local.database.converters

import androidx.compose.ui.geometry.Offset
import androidx.room.TypeConverter

/**
 * Type converter for Offset to store it as String in Room database
 */
class OffsetConverter {
    @TypeConverter
    fun fromOffset(offset: Offset): String {
        return "${offset.x},${offset.y}"
    }

    @TypeConverter
    fun toOffset(value: String): Offset {
        val parts = value.split(",")
        return Offset(parts[0].toFloat(), parts[1].toFloat())
    }
}
