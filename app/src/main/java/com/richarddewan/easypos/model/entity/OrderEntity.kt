package com.richarddewan.easypos.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_detail")
class OrderEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "order_id")
    var order_id: String? = null
    @ColumnInfo(name = "line_number")
    var line_number: String? = null
    @ColumnInfo(name = "product_id")
    var product_id: String? = null
    @ColumnInfo(name = "item_id")
    var item_id: String? = null
    @ColumnInfo(name = "item_name")
    var item_name: String? = null
    @ColumnInfo(name = "barcode")
    var barcode: String? = null
    @ColumnInfo(name = "qty")
    var qty: String? = null
    @ColumnInfo(name = "product_image")
    var product_image: String? = null
    @ColumnInfo(name = "order_status")
    var order_status: String? = null

    constructor(
        order_id: String,
        line_number: String,
        product_id: String,
        item_id: String,
        item_name: String,
        barcode: String,
        qty: String,
        product_image: String,
        order_status: String
    ) {
        this.order_id = order_id
        this.line_number = line_number
        this.product_id = product_id
        this.item_id = item_id
        this.item_name = item_name
        this.barcode = barcode
        this.qty = qty
        this.product_image = product_image
        this.order_status = order_status
    }
}