package com.richarddewan.easypos.order.header

class OrderHeaderProperty {

    var orderId:String? = null
    var order_status:String? = null

    constructor(orderId:String,order_status:String){
        this.orderId = orderId
        this.order_status = order_status
    }
}