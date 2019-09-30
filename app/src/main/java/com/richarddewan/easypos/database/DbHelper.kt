package com.richarddewan.easypos.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.richarddewan.easypos.order.OrderProperty
import com.richarddewan.easypos.order.header.OrderHeaderProperty
import com.richarddewan.easypos.order.line.OrderLineProperty
import com.richarddewan.easypos.product.ProductProperty
import java.lang.Exception

class DbHelper : SQLiteOpenHelper {
    private val TAG = "DbHelper"

    companion object {
        const val DATABASE_NAME = "MyPOS"
        const val DATABASE_VERSION = 1
        private const val TAG = "DbHelper"
    }

    private var context: Context? = null

    constructor(context: Context) : super(context, DATABASE_NAME, null, DATABASE_VERSION) {
        this.context = context

    }

    /*
    insert order detail
     */
    fun insertOrderDetail(
        line_number: Int,
        order_id: String,
        product_id: String,
        item_id: String,
        item_name: String,
        qty: String,
        barcode: String,
        order_status: String
    ) {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(DbContract.OrderDetail.key_order_id, order_id)
            contentValues.put(DbContract.OrderDetail.key_line_number, line_number)
            contentValues.put(DbContract.OrderDetail.key_product_id, product_id)
            contentValues.put(DbContract.OrderDetail.key_item_id, item_id)
            contentValues.put(DbContract.OrderDetail.key_item_name, item_name)
            contentValues.put(DbContract.OrderDetail.key_barcode, barcode)
            contentValues.put(DbContract.OrderDetail.key_qty, qty)
            contentValues.put(DbContract.OrderDetail.key_order_status, order_status)

            db.insert(DbContract.OrderDetail.TABLE_NAME, null, contentValues)
            db.close()
            Log.e(TAG, "new row added to : ${DbContract.OrderDetail.TABLE_NAME}")
        } catch (er: SQLException) {
            Log.e(TAG, er.message.toString())
        }

    }

    /*
    update order
     */
    fun updateOrderDetail(order_id: String, product_id: String, line_id: String, qty: String) {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(DbContract.OrderDetail.key_qty, qty)

            val whereClause: String = DbContract.OrderDetail.key_order_id + " =? AND " +
                    DbContract.OrderDetail.key_product_id + " =? AND " +
                    DbContract.OrderDetail.key_line_number + " =?"

            val whereArgs = arrayOf(order_id, product_id, line_id)

            db.update(DbContract.OrderDetail.TABLE_NAME, contentValues, whereClause, whereArgs)
            Log.e(TAG, "record updated : ${DbContract.OrderDetail.TABLE_NAME}")

        } catch (er: Exception) {
            Log.e(TAG, er.message.toString())
        }

    }

    fun updateOrderStatus(order_id: String, order_status: String): Boolean {
        var status = false
        try {

            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(DbContract.OrderDetail.key_order_status, order_status)

            val whereClause: String = DbContract.OrderDetail.key_order_id + " =?"
            val whereArgs = arrayOf(order_id)

            db.update(DbContract.OrderDetail.TABLE_NAME, contentValues, whereClause, whereArgs)
            Log.e(TAG, "record updated : ${DbContract.OrderDetail.TABLE_NAME}")
            status = true

        } catch (er: SQLException) {
            Log.e(TAG, er.message.toString())
        }
        return status
    }

    fun deleteOrderLine(order_id: String, line_id: String) {
        try {
            val db = this.writableDatabase

            val whereClause: String = DbContract.OrderDetail.key_order_id + " =? AND " +
                    DbContract.OrderDetail.key_line_number + " =?"
            val whereArgs = arrayOf(order_id, line_id)

            db.delete(DbContract.OrderDetail.TABLE_NAME, whereClause, whereArgs)
            Log.e(
                TAG,
                "record deleted OrderNumber: $order_id LineNumber: $line_id from ${DbContract.OrderDetail.TABLE_NAME}"
            )
        } catch (er: SQLException) {
            Log.e(TAG, er.message.toString())
        }
    }

    fun insertProductDetail(
        product_id: String,
        item_id: String,
        item_name: String,
        barcode: String,
        image: String
    ) {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(DbContract.ProductDetail.key_product_id, product_id)
            contentValues.put(DbContract.ProductDetail.key_item_id, item_id)
            contentValues.put(DbContract.ProductDetail.key_item_name, item_name)
            contentValues.put(DbContract.ProductDetail.key_barcode, barcode)
            contentValues.put(DbContract.ProductDetail.key_image, image)

            db.insert(DbContract.ProductDetail.TABLE_NAME, null, contentValues)
            db.close()
            Log.e(TAG, "new record inserted to : ${DbContract.ProductDetail.TABLE_NAME}")

        } catch (er: SQLException) {
            Log.e(TAG, er.message.toString())
        }

    }

    /*
    return product detail
     */
    fun getProductDetail(): ArrayList<ProductProperty> {
        val mList = ArrayList<ProductProperty>()
        try {
            val db = this.readableDatabase
            val query: String = "SELECT * FROM " + DbContract.ProductDetail.TABLE_NAME +
                    " ORDER BY " + DbContract.ProductDetail.key_item_name + " ASC"
            val cursor: Cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val data = ProductProperty(
                        cursor.getString(cursor.getColumnIndex(DbContract.ProductDetail.key_product_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.ProductDetail.key_item_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.ProductDetail.key_item_name)),
                        cursor.getString(cursor.getColumnIndex(DbContract.ProductDetail.key_barcode)),
                        cursor.getString(cursor.getColumnIndex(DbContract.ProductDetail.key_image))
                    )
                    mList.add(data)

                } while (cursor.moveToNext())

            }
            cursor.close()
            db.close()

        } catch (er: SQLException) {
            Log.e(TAG, er.message.toString())
        }
        return mList
    }

    /*
    return cart item
     */
    fun getCartDetail(order_id: String): ArrayList<OrderProperty> {
        val mList = ArrayList<OrderProperty>()
        try {
            val db = this.readableDatabase
            val query: String = "SELECT * FROM " + DbContract.OrderDetail.TABLE_NAME +
                    " WHERE " + DbContract.OrderDetail.key_order_id + "= '$order_id'"
            val cursor: Cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val data = OrderProperty(
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_order_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_line_number)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_product_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_item_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_item_name)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_barcode)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_qty)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_order_status))
                    )
                    mList.add(data)

                } while (cursor.moveToNext())

            }
            cursor.close()
            db.close()

        } catch (er: SQLException) {
            Log.e(TAG, er.message.toString())
        }
        return mList
    }

    /*
    return order header
     */
    fun getOrderHeader(): ArrayList<OrderHeaderProperty> {
        val db = this.readableDatabase
        val mList = ArrayList<OrderHeaderProperty>()
        try {
            val query = "SELECT " + DbContract.OrderDetail.key_order_id + "," +
                    DbContract.OrderDetail.key_order_status +
                    " FROM " + DbContract.OrderDetail.TABLE_NAME +
                    " GROUP BY " + DbContract.OrderDetail.key_order_id +
                    " ORDER BY " + DbContract.OrderDetail.key_order_id + " DESC"

            val cursor: Cursor = db.rawQuery(query, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val mProperty = OrderHeaderProperty(
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_order_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_order_status))
                    )

                    mList.add(mProperty)

                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        }
        catch (er:SQLException){
            Log.e(TAG,er.message.toString())
        }

        return mList

    }
    /*
    return order line
     */
    fun getOrderLine(orderId:String) : ArrayList<OrderLineProperty>{
        var mList = ArrayList<OrderLineProperty>()
        var db = this.readableDatabase
        try {
            val query = "SELECT * FROM " + DbContract.OrderDetail.TABLE_NAME +
                    " WHERE " + DbContract.OrderDetail.key_order_id + "= '$orderId'"
                    " ORDER BY " + DbContract.OrderDetail.key_line_number + " ASC"

            val cursor: Cursor = db.rawQuery(query, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val mProperty = OrderLineProperty(
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_order_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_line_number)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_product_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_item_id)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_item_name)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_barcode)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_qty)),
                        cursor.getString(cursor.getColumnIndex(DbContract.OrderDetail.key_order_status))
                    )

                    mList.add(mProperty)

                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()

        }
        catch (er:SQLException){
            Log.e(TAG,er.message.toString())
        }


        return mList
    }

    fun getCartCount(salesId:String) : String{
        var cartCount = "0"
        val db = this.readableDatabase

        val query = "SELECT COUNT(${DbContract.OrderDetail.key_id}) " +
                "FROM " + DbContract.OrderDetail.TABLE_NAME +
                " WHERE " + DbContract.OrderDetail.key_order_id + "='$salesId'"
        val cursor:Cursor = db.rawQuery(query,null)
        if (cursor.count > 0){
            cursor.moveToFirst()
            cartCount = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return  cartCount
    }


    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(DbCreateTable.ORDER_DETAIL_QUERY)
        Log.e(TAG, "Order Table Created")
        p0?.execSQL(DbCreateTable.PRODUCT_DETAIL_QUERY)
        Log.e(TAG, "Product Table Created")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS ${DbContract.OrderDetail.TABLE_NAME}")
        p0?.execSQL("DROP TABLE IF EXISTS ${DbContract.ProductDetail.TABLE_NAME}")
    }

}