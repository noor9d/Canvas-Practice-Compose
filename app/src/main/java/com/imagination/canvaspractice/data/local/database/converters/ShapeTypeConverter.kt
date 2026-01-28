package com.imagination.canvaspractice.data.local.database.converters

import androidx.room.TypeConverter
import com.imagination.canvaspractice.domain.model.ShapeType

/**
 * Type converter for ShapeType enum
 */
class ShapeTypeConverter {
    @TypeConverter
    fun fromShapeType(type: ShapeType): String {
        return type.name
    }

    @TypeConverter
    fun toShapeType(value: String): ShapeType {
        return ShapeType.valueOf(value)
    }
}
