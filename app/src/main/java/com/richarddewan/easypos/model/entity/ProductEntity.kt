package com.richarddewan.easypos.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
class ProductEntity {

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
    @ColumnInfo(name = "product_id")
    var product_id:String? = null
    @ColumnInfo(name = "item_id")
    var item_id:String? = null
    @ColumnInfo(name = "item_name")
    var item_name:String? = null
    @ColumnInfo(name = "barcode")
    var barcode:String? = null
    @ColumnInfo(name = "image")
    var image:String? = null

    constructor(product_id:String,item_id:String,item_name:String,barcode:String,image:String){
        this.product_id = product_id
        this.item_id = item_id
        this.item_name = item_name
        this.barcode = barcode
        this.image = image
    }


}