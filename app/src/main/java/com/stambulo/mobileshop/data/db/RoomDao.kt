package com.stambulo.mobileshop.data.db

import androidx.room.*

@Dao
abstract class RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertProduct(product: EntityRoomProduct)

    @Query("SELECT * FROM products")
    abstract suspend fun getAllProducts(): List<EntityRoomProduct>

    @Query("SELECT id FROM products")
    abstract suspend fun getIdFromDb(): List<Int>

    @Query("DELETE FROM products")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM products WHERE id = :productId")
    abstract suspend fun deleteById(productId: Int)
}
