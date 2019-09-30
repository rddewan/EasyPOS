package com.richarddewan.easypos.database

class DbContract {

    private constructor()

    abstract class ProductDetail{
        companion object {
            const val key_id = "id"
            const val key_product_id = "product_id"
            const val key_item_id = "item_id"
            const val key_item_name = "item_name"
            const val key_barcode = "barcode"
            const val key_image = "image"

            const val TABLE_NAME = "products"
        }
    }

    abstract class OrderDetail {
        companion object {
            const val key_id = "id"
            const val key_order_id = "order_id"
            const val key_product_id = "product_id"
            const val key_line_number = "line_number"
            const val key_item_id = "item_id"
            const val key_item_name = "item_name"
            const val key_qty = "qty"
            const val key_barcode = "barcode"
            const val key_image = "image"
            const val key_order_status = "order_status"

            const val TABLE_NAME = "order_detail"
        }
    }
}