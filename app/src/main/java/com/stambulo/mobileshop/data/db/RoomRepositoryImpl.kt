package com.stambulo.mobileshop.data.db

class RoomRepositoryImpl (private val dao: RoomDao) {

    suspend fun getDataFromDB(): List<EntityRoomProduct>{
        return dao.getAllProducts()
    }

    suspend fun getProductById(id: Int): EntityRoomProduct{
        return dao.getProductById(id)
    }

    suspend fun getIdListFromDb(): List<Int>{
        return dao.getIdListFromDb()
    }

    suspend fun insertData(data: EntityRoomProduct){
        dao.insertProduct(data)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
}
