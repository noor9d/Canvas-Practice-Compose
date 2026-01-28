package com.imagination.canvaspractice.data.local.database.converters

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter

/**
 * Type converter for Color to store it as Long in Room database
 */
class ColorConverter {
    @TypeConverter
    fun fromColor(color: Color): Long {
        return color.value.toLong()
    }

    @TypeConverter
    fun toColor(value: Long): Color {
        return Color(value.toULong())
    }
}
