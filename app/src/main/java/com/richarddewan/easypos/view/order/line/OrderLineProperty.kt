package com.richarddewan.easypos.view.order.line

class OrderLineProperty {
    var order_id:String? = null
    var line_number:String? = null
    var product_id:String? = null
    var item_id:String? = null
    var item_name:String? = null
    var barcode:String? = null
    var qty:String? = null
    var order_status:String? = null

    constructor(order_id:String,line_number:String, product_id:String,item_id:String,item_name:String,barcode:String,qty:String,order_status:String){
        this.order_id = order_id
        this.line_number = line_number
        this.product_id = product_id
        this.item_id = item_id
        this.item_name = item_name
        this.barcode = barcode
        this.qty = qty
        this.order_status = order_status
    }

}