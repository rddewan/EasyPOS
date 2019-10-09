package com.richarddewan.easypos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.richarddewan.easypos.model.database.AppDatabase
import com.richarddewan.easypos.model.entity.ProductEntity
import com.richarddewan.easypos.repository.ProductRepository

class MainActivityViewModel : AndroidViewModel {
    private var productRepository: ProductRepository? = null

    constructor(application: Application) : super(application){
        this.productRepository = ProductRepository(application)
    }

    fun getAllProduct(): LiveData<List<ProductEntity>> {
        return productRepository!!.getAllProduct()
    }
}