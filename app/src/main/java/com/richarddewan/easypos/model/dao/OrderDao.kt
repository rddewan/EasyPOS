package com.richarddewan.easypos.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.richarddewan.easypos.model.entity.OrderEntity

@Dao
interface OrderDao {

    @Insert
    fun insert(orderEntity: OrderEntity)

    @Update
    fun update(orderEntity: List<OrderEntity>)

    @Delete
    fun delete(orderEntity: OrderEntity)

    @Query("DELETE FROM order_detail WHERE order_id = :order_id AND product_id = :product_id AND line_number = :line_id")
    fun deleteOrderLine(order_id: String, product_id: String, line_id: String)

    @Query("UPDATE order_detail SET qty = :qty WHERE order_id = :order_id AND product_id = :product_id AND line_number = :line_id")
    fun updateOrderQty(order_id: String, product_id: String, line_id: String, qty: String) : Int

    @Query("UPDATE order_detail SET order_status = :order_status WHERE order_id = :order_id")
    fun updateOrderStatus(order_id: String, order_status: String): Int

    @Query("SELECT * FROM order_detail WHERE order_id = :order_id")
    fun getCartDetail(order_id: String) : LiveData<List<OrderEntity>>

    @Query("SELECT * FROM order_detail GROUP BY order_id ORDER BY order_id DESC")
    fun getOrderHeader(): List<OrderEntity>

    @Query("SELECT * FROM ORDER_DETAIL WHERE order_id = :orderId ORDER BY line_number ASC")
    fun getOrderLine(orderId:String) :List<OrderEntity>

    @Query("SELECT COUNT(id) FROM order_detail WHERE order_id = :order_id")
    fun getCartCount(order_id: String) : LiveData<Int>

    @Query("SELECT * FROM order_detail WHERE id = :id")
    fun getOrderById(id: Int) : List<OrderEntity>
}