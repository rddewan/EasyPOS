package com.richarddewan.easypos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.richarddewan.easypos.model.entity.OrderEntity
import com.richarddewan.easypos.repository.OrderRepository


class OrderDetailViewModel : AndroidViewModel{

    private  var orderRepository:OrderRepository? = null

    constructor(application: Application) : super(application) {
        this.orderRepository = OrderRepository(application)
    }

    fun getOrderHeader() : LiveData<List<OrderEntity>>{
       return  orderRepository!!.getOrderHeader()
    }

    fun getOrderLine(order_id: String): LiveData<List<OrderEntity>>{
        return orderRepository!!.getOrderLine(order_id)
    }

    fun  updateOrder(orderEntity: OrderEntity){
        orderRepository!!.updateOrder(orderEntity)
    }

    fun deleteOrder(orderEntity: OrderEntity){
        orderRepository!!.deleteOrder(orderEntity)
    }

    fun deleteOrderLine(order_id: String, product_id: String, line_id: String){
        orderRepository!!.deleteOrderLine(order_id,product_id,line_id)
    }

}