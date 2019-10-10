package com.richarddewan.easypos.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.richarddewan.easypos.model.dao.OrderDao
import com.richarddewan.easypos.model.database.AppDatabase
import com.richarddewan.easypos.model.entity.OrderEntity

class OrderRepository {
     private var orderDao: OrderDao? = null


    constructor(application: Application){
        val appDatabase = AppDatabase.getInstance(application)
        orderDao = appDatabase.orderDao()

    }

    fun addOrder(orderEntity: OrderEntity){
        InsertOrderTask(orderDao!!).execute(orderEntity)
    }

    fun updateOrder(orderEntity: OrderEntity){
        UpdateOrderTask(orderDao!!).execute(orderEntity)
    }

    fun deleteOrder(orderEntity: OrderEntity){
        DeleteOrderTask(orderDao!!).execute(orderEntity)
    }

    fun deleteOrderLine(order_id: String, product_id: String, line_id: String){
        orderDao!!.deleteOrderLine(order_id, product_id, line_id)
    }

    fun getOrderById(order_id: String) : LiveData<List<OrderEntity>> {
        return orderDao!!.getCartDetail(order_id)
    }

    fun getCartCount(order_id: String) : LiveData<Int>{
        return orderDao!!.getCartCount(order_id)
    }


    private inner class InsertOrderTask(private var orderDao: OrderDao) : AsyncTask<OrderEntity,Void,Void>(){

        override fun doInBackground(vararg p0: OrderEntity?): Void?{
            this.orderDao.insert(p0[0]!!)
            return null
        }
    }

    private inner class UpdateOrderTask(private var orderDao: OrderDao) : AsyncTask<OrderEntity,Void,Void>(){

        override fun doInBackground(vararg p0: OrderEntity?): Void? {
            orderDao.updateOrderQty(p0[0]?.order_id!!,p0[0]?.product_id!!,p0[0]?.line_number!!,p0[0]?.qty!!)
            return null
        }
    }

    private inner class DeleteOrderTask(private var orderDao: OrderDao) : AsyncTask<OrderEntity,Void,Void>(){

        override fun doInBackground(vararg p0: OrderEntity?): Void? {
            this.orderDao.delete(p0[0]!!)
            return null
        }

    }
}
