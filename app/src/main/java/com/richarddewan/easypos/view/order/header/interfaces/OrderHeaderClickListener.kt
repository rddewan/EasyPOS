package com.richarddewan.easypos.view.order.header.interfaces

import android.view.View

interface OrderHeaderClickListener {
    fun onClick(view:View?,position:Int,isLongClick:Boolean)
}