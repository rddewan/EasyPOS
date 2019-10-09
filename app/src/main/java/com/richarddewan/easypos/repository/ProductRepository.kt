package com.richarddewan.easypos.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.richarddewan.easypos.model.dao.ProductDao
import com.richarddewan.easypos.model.database.AppDatabase
import com.richarddewan.easypos.model.entity.ProductEntity
import java.util.concurrent.Executor

class ProductRepository {
    private var productDao: ProductDao? = null
    private var productEntity: List<ProductEntity>? = null

    constructor(application: Application){
        val appDatabase = AppDatabase.getInstance(application)
        productDao = appDatabase.productDao()
    }

    fun insertProduct(productEntity: ProductEntity){
        InsertProductTask(productDao!!).execute(productEntity)
    }

    fun updateProduct(productEntity: ProductEntity){
        UpdateProductTask(productDao!!).execute(productEntity)

    }

    fun deleteProduct(productEntity: ProductEntity){
        DeleteProductTask(productDao!!).execute(productEntity)
    }

    fun deleteProductTable(){
        DeleteProductTableTask(productDao!!).execute()
    }

    fun getAllProduct() : LiveData<List<ProductEntity>>{
        return productDao!!.getAllProduct()
    }

    private inner class InsertProductTask(productDao: ProductDao) :
        AsyncTask<ProductEntity, Void, Void>() {
        private var productDao = productDao

        override fun doInBackground(vararg p0: ProductEntity?): Void? {
            productDao.insert(p0[0]!!)
            return null
        }

    }

    private inner class UpdateProductTask(productDao: ProductDao) : AsyncTask<ProductEntity,Void,Void>(){
        private var productDao = productDao
        override fun doInBackground(vararg p0: ProductEntity?): Void? {
            productDao.update(p0[0]!!)
            return null

        }

    }

    private inner class DeleteProductTask(productDao: ProductDao) : AsyncTask<ProductEntity,Void,Void>(){
        private var productDao = productDao
        override fun doInBackground(vararg p0: ProductEntity?): Void? {
            productDao.delete(p0[0]!!)
            return null

        }

    }

    private inner class DeleteProductTableTask(productDao: ProductDao) : AsyncTask<Void,Void,Void>(){
        private var productDao = productDao

        override fun doInBackground(vararg p0: Void?): Void? {
            productDao.deleteProductTable()
            return null
        }
    }


}