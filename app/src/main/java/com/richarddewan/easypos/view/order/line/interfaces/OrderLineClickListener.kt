package com.richarddewan.easypos.view.order.line.interfaces

import android.view.View

interface OrderLineClickListener {
    fun onLineItemClick(view:View?,position:Int,isLongClick:Boolean)
    fun onLineEditClick(view:View, position: Int)
    fun onLineDeleteClcik(view:View,position: Int)
}