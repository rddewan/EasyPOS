package com.richarddewan.easypos.order.interfaces

import android.view.View

interface CartItemClickListener {
    fun onCartClick(view: View?,position:Int,isLongClick:Boolean)
    fun onEditClick(view: View, position:Int)
    fun onDeleteClick(view:View,position: Int)
}