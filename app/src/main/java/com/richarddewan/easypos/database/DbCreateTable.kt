package com.richarddewan.easypos.database

class DbCreateTable {

    private constructor()

    companion object {
        const val PRODUCT_DETAIL_QUERY = "CREATE TABLE IF NOT EXISTS " + DbContract.ProductDetail.TABLE_NAME +
                "(" +
                DbContract.ProductDetail.key_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.ProductDetail.key_product_id + " TEXT," +
                DbContract.ProductDetail.key_item_id + " TEXT," +
                DbContract.ProductDetail.key_item_name + " TEXT," +
                DbContract.ProductDetail.key_barcode + " TEXT)"

        const val ORDER_DETAIL_QUERY = "CREATE TABLE IF NOT EXISTS " + DbContract.OrderDetail.TABLE_NAME +
                "(" +
                DbContract.OrderDetail.key_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.OrderDetail.key_order_id + " TEXT," +
                DbContract.OrderDetail.key_line_number + " INTEGER," +
                DbContract.OrderDetail.key_product_id + " TEXT," +
                DbContract.OrderDetail.key_item_id + " TEXT," +
                DbContract.OrderDetail.key_item_name + " TEXT," +
                DbContract.OrderDetail.key_barcode + " TEXT," +
                DbContract.OrderDetail.key_qty + " TEXT," +
                DbContract.OrderDetail.key_order_status + " TEXT)"

    }

}