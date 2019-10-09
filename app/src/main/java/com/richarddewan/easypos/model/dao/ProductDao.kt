package com.richarddewan.easypos.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.richarddewan.easypos.model.entity.ProductEntity

@Dao
interface ProductDao {

    @Insert
    fun insert(productEntity: ProductEntity)

    @Update
    fun update(productEntity: ProductEntity)

    @Delete
    fun delete(productEntity: ProductEntity)

    /*@Query("SELECT * FROM products")
    fun getAllProduct() : List<ProductEntity>*/
    @Query("SELECT * FROM products ORDER BY item_name ASC")
    fun getAllProduct() :LiveData<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE product_id LIKE :productId")
    fun getProductById(productId:String) : List<ProductEntity>

    @Query("DELETE FROM products")
    fun deleteProductTable()
}