package com.richarddewan.easypos.view.download.product.interfaces

import android.view.View


interface ProductClickListener {
    fun onProductClick(view: View?,position: Int,isLongClick:Boolean)
    fun onBtnCartClick(view: View,position: Int)
}