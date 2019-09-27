package com.richarddewan.easypos.product

class ProductProperty {

    var product_id:String? = null
    var item_id:String? = null
    var item_name:String? = null
    var barcode:String? = null

    constructor(product_id:String,item_id:String,item_name:String,barcode:String){
        this.product_id = product_id
        this.item_id = item_id
        this.item_name = item_name
        this.barcode = barcode
    }

}