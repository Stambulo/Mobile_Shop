package com.stambulo.mobileshop.data.db

import androidx.room.*

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: EntityRoomProduct)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<EntityRoomProduct>

    @Query("SELECT id FROM products")
    suspend fun getIdListFromDb(): List<Int>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): EntityRoomProduct

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteById(productId: Int)
}
