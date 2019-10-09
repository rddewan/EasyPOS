package com.richarddewan.easypos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.richarddewan.easypos.model.entity.ProductEntity
import com.richarddewan.easypos.repository.ProductRepository

class SyncDataFromServerViewModel : AndroidViewModel {
    private var productRepository: ProductRepository? = null

    constructor(application: Application) : super(application){
        this.productRepository = ProductRepository(application)
    }

    fun addProduct(productEntity: ProductEntity){
        productRepository?.insertProduct(productEntity)
    }

    fun deleteProductTable(){
        productRepository?.deleteProductTable()
    }
}