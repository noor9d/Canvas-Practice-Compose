package com.imagination.canvaspractice.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imagination.canvaspractice.data.local.database.converters.ColorConverter
import com.imagination.canvaspractice.data.local.database.converters.OffsetConverter
import com.imagination.canvaspractice.data.local.database.converters.OffsetListConverter
import com.imagination.canvaspractice.data.local.database.converters.ShapeTypeConverter
import com.imagination.canvaspractice.data.local.database.dao.BoardDao
import com.imagination.canvaspractice.data.local.database.dao.PathDao
import com.imagination.canvaspractice.data.local.database.dao.ShapeDao
import com.imagination.canvaspractice.data.local.database.dao.TextDao
import com.imagination.canvaspractice.data.local.database.entity.BoardEntity
import com.imagination.canvaspractice.data.local.database.entity.PathEntity
import com.imagination.canvaspractice.data.local.database.entity.ShapeEntity
import com.imagination.canvaspractice.data.local.database.entity.TextEntity

/**
 * Room database for Canvas Practice app
 */
@Database(
    entities = [
        BoardEntity::class,
        PathEntity::class,
        TextEntity::class,
        ShapeEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    ColorConverter::class,
    OffsetConverter::class,
    OffsetListConverter::class,
    ShapeTypeConverter::class
)
abstract class CanvasDatabase : RoomDatabase() {
    abstract fun boardDao(): BoardDao
    abstract fun pathDao(): PathDao
    abstract fun textDao(): TextDao
    abstract fun shapeDao(): ShapeDao
}
