package com.stambulo.mobileshop.data.db

import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(private val db: RoomDatabase) {

    suspend fun getDataFromDB(): List<EntityRoomProduct>{
        return db.dao.getAllProducts()
    }

    suspend fun getIdFromDb(): List<Int>{
        return db.dao.getIdFromDb()
    }

    suspend fun insertData(data: EntityRoomProduct){
        db.dao.insertProduct(data)
    }

    suspend fun deleteById(id: Int) {
        db.dao.deleteById(id)
    }
}
