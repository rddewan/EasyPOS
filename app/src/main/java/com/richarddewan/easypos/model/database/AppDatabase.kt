package com.richarddewan.easypos.model.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.richarddewan.easypos.model.dao.OrderDao
import com.richarddewan.easypos.model.dao.ProductDao
import com.richarddewan.easypos.model.entity.OrderEntity
import com.richarddewan.easypos.model.entity.ProductEntity

@Database(entities = [OrderEntity::class,ProductEntity::class],version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun orderDao(): OrderDao
    abstract fun productDao() : ProductDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase{
            if (INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"pos_db")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .addCallback(AppDatabaseCallBack())
                        .build()
                }
            }
            return INSTANCE!!
        }

    }

    private class AppDatabaseCallBack() : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            InsertInitialProductDataTask(INSTANCE!!).execute()
        }

    }

    private val callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            InsertInitialProductDataTask(INSTANCE!!).execute()

        }

    }

    class InsertInitialProductDataTask() : AsyncTask<Void,Void,Void>(){
        private var productDao: ProductDao? = null

        constructor(appDatabase: AppDatabase) : this() {
            productDao = appDatabase.productDao()
        }


        override fun doInBackground(vararg p0: Void?): Void? {
            for (i in 1..10){
                val productEntity = ProductEntity(
                    "Product $i",
                    "Item $i",
                    "Name $i",
                    "012345678$i",
                    ""
                )
                productDao?.insert(productEntity)
            }

            return null

        }

    }


}