package com.richarddewan.easypos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.richarddewan.easypos.model.database.AppDatabase
import com.richarddewan.easypos.model.entity.OrderEntity
import com.richarddewan.easypos.model.entity.ProductEntity
import com.richarddewan.easypos.repository.OrderRepository
import com.richarddewan.easypos.repository.ProductRepository

class MainActivityViewModel : AndroidViewModel {
    private var productRepository: ProductRepository? = null
    private var orderRepository: OrderRepository? = null


    constructor(application: Application) : super(application){
        this.productRepository = ProductRepository(application)
        this.orderRepository = OrderRepository(application)
    }

    fun getAllProduct(): LiveData<List<ProductEntity>> {
        return productRepository!!.getAllProduct()
    }

    fun  addOrder(orderEntity: OrderEntity) {
        orderRepository!!.addOrder(orderEntity)
    }

    fun getOrderDetailById(order_id: String) : LiveData<List<OrderEntity>>{
        return orderRepository!!.getOrderById(order_id)
    }

    fun getCartCount(order_id: String) : LiveData<Int>{
        return orderRepository!!.getCartCount(order_id)
    }

    fun  updateOrder(orderEntity: OrderEntity){
        orderRepository!!.updateOrder(orderEntity)
    }

    fun deleteOrder(orderEntity: OrderEntity){
        orderRepository!!.deleteOrder(orderEntity)
    }



}