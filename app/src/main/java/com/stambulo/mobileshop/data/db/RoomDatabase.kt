package com.stambulo.mobileshop.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [EntityRoomProduct::class], version = 1)
@TypeConverters(Converters::class)
abstract class RoomDatabase: RoomDatabase() {

    abstract val dao: RoomDao
}
