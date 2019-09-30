package com.richarddewan.easypos

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.karumi.dexter.Dexter
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.richarddewan.easypos.database.DbHelper
import com.richarddewan.easypos.download.SyncDataFromServer
import com.richarddewan.easypos.order.CartRecycleViewAdaptor
import com.richarddewan.easypos.order.OrderProperty
import com.richarddewan.easypos.order.header.OrderHeaderDetail
import com.richarddewan.easypos.order.interfaces.CartItemClickListener
import com.richarddewan.easypos.product.ProductProperty
import com.richarddewan.easypos.product.ProductRecycleViewAdaptor
import com.richarddewan.easypos.product.interfaces.ProductClickListener
import com.richarddewan.easypos.setting.SettingsActivity
import kotlinx.android.synthetic.main.custom_cart_view.view.*
import kotlinx.android.synthetic.main.custom_dialog_qty.view.*
import kotlinx.android.synthetic.main.custom_dialog_qty.view.lbItemName
import com.karumi.dexter.PermissionToken
import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File


class MainActivity : AppCompatActivity(), ProductClickListener, CartItemClickListener {

    val TAG: String = "MainActivity"
    private var mRecyclerView: RecyclerView? = null
    private var mRecyclerViewCart: RecyclerView? = null
    private var mAdaptor: ProductRecycleViewAdaptor? = null
    private var mAdaptorCart: CartRecycleViewAdaptor? = null
    //private var mLayoutManager:RecyclerView.LayoutManager? = null
    private var mList: ArrayList<ProductProperty> = ArrayList()
    private var mListCart: ArrayList<OrderProperty> = ArrayList()
    private var mLayoutManager: StaggeredGridLayoutManager? = null
    private var mLayoutManagerCart: RecyclerView.LayoutManager? = null
    private var cartMenuItem: MenuItem? = null
    private var cartCount: TextView? = null
    private var cartImageView: ImageView? = null
    private var searchView: SearchView? = null
    //
    private var dbHelper: DbHelper? = null;
    private var toolbar: Toolbar? = null
    private var ORDER_ID: String? = null
    private var ORDER_FORMAT: String? = null
    private var COMPANY_ID: String? = null
    private var ORDER_FORMAT_RUNNING_NUMBER: Int = 0
    private var ORDER_STATUS_OPEN = "OPEN"
    private var ORDER_STATUS_CLOSED = "CLOSED"
    private var LINE_NUMBER: Int = 0
    private var CART_COUNT:String? = null
    private var sharedPreferences: SharedPreferences? = null
    private val PERMISSION_CODE = 1234560
    private val APP_DIRECTORY = "/EasyPOS"
    private val IMPORT_DIRECTORY = "/EasyPOS/Import"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.menu_bar) as Toolbar
        setSupportActionBar(toolbar)
        //
        requestPermission()
        //
        getSharedPref()
        //
        drawer(savedInstanceState)
        //
        productRecycleView()
        //
        cartRecycleView()
        // sec delay
        Handler().postDelayed({
            //
            getCartCountFromDb()

        }, 1000)

    }

    fun getSharedPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        ORDER_FORMAT = sharedPreferences?.getString("order_number_format", "")
        COMPANY_ID = sharedPreferences?.getString("company_name","")
        if (ORDER_FORMAT.equals("")) {
            openSettingPrefActivity()
        } else {
            val runningNumber = sharedPreferences?.getString("order_running_number", "")
            if (runningNumber.equals("")) {
                ORDER_FORMAT_RUNNING_NUMBER = 1
            } else {
                ORDER_FORMAT_RUNNING_NUMBER = runningNumber?.toInt()!!.plus(1)
            }
            //set order id
            ORDER_ID = this.ORDER_FORMAT + this.ORDER_FORMAT_RUNNING_NUMBER
        }

    }

    fun openSettingPrefActivity() {
        MaterialDialog(this).show {
            icon(R.drawable.stop)
            title(R.string.num_seq_format_empty_title)
            message(R.string.num_seq_format_empty_msg)
            cancelable(false)
            cornerRadius(16f)
            positiveButton(R.string.yes) {
                //call delete function
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
            negativeButton(R.string.disagree) {
                dismiss()
            }
        }
    }

    fun productRecycleView() {
        for (i in 1..100){
            val data = ProductProperty("test$i","test$i","test_name$i","01234566$i","http://$i.jpg")
            dbHelper = DbHelper(applicationContext)
            dbHelper!!.insertProductDetail(data.product_id!!,data.item_id!!,data.item_name!!,data.barcode!!,data.image!!)
            dbHelper!!.close()

        }
        //get the list of product from database
        dbHelper = DbHelper(applicationContext)
        mList = dbHelper!!.getProductDetail()
        dbHelper!!.close()

        //recycle view
        mRecyclerView = findViewById(R.id.product_recycle_view)
        mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mRecyclerView?.layoutManager = mLayoutManager
        //set data to adaptor
        mAdaptor = ProductRecycleViewAdaptor(mList)
        //set recycle view adaptor
        mRecyclerView?.adapter = mAdaptor
        //set recycle view item click listener
        mAdaptor?.setProductClickListener(this)
    }

    fun cartRecycleView() {
        try {
            dbHelper = DbHelper(applicationContext)
            mListCart = dbHelper?.getCartDetail(ORDER_ID!!)!!
            dbHelper?.close()
        } catch (er: Exception) {
            Log.e(TAG, er.message.toString())
        }

        //recycle view
        mRecyclerViewCart = findViewById(R.id.cart_recycle_view)
        mLayoutManagerCart = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerViewCart?.layoutManager = mLayoutManagerCart
        //set data to adaptor
        mAdaptorCart = CartRecycleViewAdaptor(mListCart)
        //set recycle view adaptor
        mRecyclerViewCart?.adapter = mAdaptorCart
        //set recycle view item click listener
        mAdaptorCart?.setCartItemClickListener(this)



    }

    fun drawer(savedInstanceState: Bundle?) {
        // Create the AccountHeader
        val headerResult = AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.color.primaryColor)
            .withTextColor(resources.getColor(R.color.white))
            .addProfiles(
                ProfileDrawerItem().withName(COMPANY_ID).
                    withIcon(resources.getDrawable(R.drawable.ic_user, null))
            )
            .withSelectionListEnabledForSingleProfile(false)
            .withOnAccountHeaderListener(object : AccountHeader.OnAccountHeaderListener {
                override fun onProfileChanged(
                    view: View?,
                    profile: IProfile<*>,
                    current: Boolean
                ): Boolean {
                    return false
                }
            })
            .build()

        //if you want to update the items at a later time it is recommended to keep it in a variable
        val item1 = PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home)
            .withIcon(FontAwesome.Icon.faw_home)
        val item2 =
            SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_order_detail)
                .withIcon(
                    FontAwesome.Icon.faw_list
                )
        val item3 = SecondaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_settings)
            .withIcon(
                R.drawable.ic_settings_outline_grey600_36dp
            )
        val item4 =
            SecondaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_synchronize)
                .withIcon(
                   R.drawable.ic_sync_grey600_36dp
                )

        //create the drawer and remember the `Drawer` result object
        //val result = DrawerBuilder()
        DrawerBuilder()
            .withAccountHeader(headerResult)
            .withActivity(this)
            .withTranslucentStatusBar(true)
            .withActionBarDrawerToggle(true)
            .withToolbar(toolbar!!)
            .addDrawerItems(
                item1,
                DividerDrawerItem(),
                item2,
                item3,
                item4

            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    // do something with the clicked item :D
                    var intent: Intent? = null
                    when {
                        drawerItem.identifier == 2L -> {
                            intent = Intent(applicationContext, OrderHeaderDetail::class.java)
                        }
                        drawerItem.identifier == 3L -> {
                            intent = Intent(applicationContext, SettingsActivity::class.java)
                        }
                        drawerItem.identifier == 4L -> {
                            intent = Intent(applicationContext, SyncDataFromServer::class.java)
                        }
                    }

                    if (intent != null) {
                        startActivity(intent)
                    }

                    return false
                }
            })
            .withSavedInstance(savedInstanceState)
            .build()

    }

    fun showAddQtyDialog(position: Int) {
        val data = mList.get(position)
        //dialog
        val dialog = MaterialDialog(this)
            .customView(R.layout.custom_dialog_qty, scrollable = true)
        dialog.cancelable(false)
        dialog.cornerRadius(16f)
        val customView = dialog.getCustomView()
        customView.etxtQty.requestFocus()
        customView.etxtQty.selectAll()
        dialog.positiveButton(R.string.agree) {
            val qty = customView.etxtQty.text
            //add to database
            addOrderToDb(position, qty.toString())
        }
        dialog.negativeButton(R.string.disagree) {
            dialog.dismiss()
        }
        customView.lbItemName.text = data.item_name
        dialog.show()

    }

    fun addOrderToDb(position: Int, qty: String) {
        val data = mList.get(position)
        LINE_NUMBER += 1
        dbHelper = DbHelper(applicationContext)
        dbHelper?.insertOrderDetail(
            LINE_NUMBER,
            ORDER_ID!!,
            data.product_id!!,
            data.item_id!!,
            data.item_name!!,
            qty,
            data.barcode!!,
            ORDER_STATUS_OPEN
        )
        //get cart count
        CART_COUNT = dbHelper?.getCartCount(ORDER_ID!!)
        cartCount?.setText(CART_COUNT)

        dbHelper?.close()
        //
        getOrderDetail()
    }

    fun updateOrderToDb(position: Int, qty: String) {
        val data = mListCart[position]
        dbHelper = DbHelper(applicationContext)
        dbHelper?.updateOrderDetail(data.order_id!!, data.product_id!!, data.line_number!!, qty)
        dbHelper?.close()
        //get order detail
        getOrderDetail()

    }

    fun updateOrderStatus() {
        dbHelper = DbHelper(applicationContext)
        val status = dbHelper?.updateOrderStatus(ORDER_ID!!, ORDER_STATUS_CLOSED)
        dbHelper?.close()

        if (status!!) {
            updateSharedPreferencesRunningNumber()
            //get shared pref
            getSharedPref()
            //get order detail
            getOrderDetail()
            // reset line number
            LINE_NUMBER = 0
        } else {
            errorDialog()
        }
    }

    fun updateSharedPreferencesRunningNumber() {
        val editor = sharedPreferences?.edit()
        editor?.putString("order_running_number", ORDER_FORMAT_RUNNING_NUMBER.toString())
        editor?.apply()
    }

    fun deleteOrderLine(position: Int) {
        val data = mListCart.get(position)
        dbHelper = DbHelper(applicationContext)
        dbHelper?.deleteOrderLine(data.order_id!!, data.line_number!!)
        dbHelper?.close()
        //get order detail
        getOrderDetail()
    }

    fun getOrderDetail() {

        dbHelper = DbHelper(applicationContext)
        mListCart = dbHelper?.getCartDetail(ORDER_ID!!)!!
        CART_COUNT = dbHelper?.getCartCount(ORDER_ID!!)
        cartCount?.setText(CART_COUNT)
        dbHelper?.close()

        //set data to adaptor
        mAdaptorCart = CartRecycleViewAdaptor(mListCart)
        //set recycle view adaptor
        mRecyclerViewCart?.adapter = mAdaptorCart
        //set recycle view item click listener
        mAdaptorCart?.setCartItemClickListener(this)

    }

    fun getItemFromDb() {
        //get the list of product from database
        dbHelper = DbHelper(applicationContext)
        mList = dbHelper!!.getProductDetail()
        dbHelper!!.close()

        //recycle view
        mRecyclerView = findViewById(R.id.product_recycle_view)
        mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mRecyclerView?.layoutManager = mLayoutManager
        //set data to adaptor
        mAdaptor = ProductRecycleViewAdaptor(mList)
        //set recycle view adaptor
        mRecyclerView?.adapter = mAdaptor
        //set recycle view item click listener
        mAdaptor?.setProductClickListener(this)
    }

    fun getCartCountFromDb(){
        try {
            dbHelper = DbHelper(applicationContext)
            CART_COUNT = dbHelper?.getCartCount(ORDER_ID!!)
            cartCount?.setText(CART_COUNT)
            dbHelper?.close()
        }
        catch (er:Exception){
            Log.e(TAG,er.message.toString())
        }
    }

    fun editCustomViewDialog(position: Int, dialog: MaterialDialog): View {
        val customView = dialog.getCustomView()
        val data = mListCart[position]
        customView.lbName?.text = data.item_name
        customView.etxtQty.setText(data.qty.toString())
        customView.etxtQty.requestFocus()
        customView.etxtQty.selectAll()

        return customView
    }

    fun print(view: View) {
        if (mListCart.size > 0) {
            MaterialDialog(this).show {
                icon(R.drawable.ic_user)
                title(R.string.print_dialog_title)
                message(R.string.print_dialog_message)
                cancelable(false)
                cornerRadius(16f)
                positiveButton(R.string.yes) {

                    //update order status
                    updateOrderStatus()

                }
                negativeButton(R.string.disagree) {
                    dismiss()
                }
            }

        } else {
            val alertDialog = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            alertDialog.setMessage("There is no data to print. Please check your order detail")
            alertDialog.setPositiveButton("OK") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            alertDialog.show()
        }

    }

    fun errorDialog() {
        MaterialDialog(this).show {
            title(R.string.error_dialog_title)
            message(R.string.error_dialog_message)
            icon(R.drawable.ic_user)
            cancelable(false)
            cornerRadius(16f)
            positiveButton(R.string.agree) {
                dismiss()
            }

        }
    }

    override fun onProductClick(view: View?, position: Int, isLongClick: Boolean) {
        if (isLongClick) {
            Log.e(TAG, "long click $position")
        } else {
            Log.e(TAG, "short click $position")
        }
    }

    override fun onBtnCartClick(view: View, position: Int) {
        showAddQtyDialog(position)
    }

    override fun onCartClick(view: View?, position: Int, isLongClick: Boolean) {
        if (isLongClick) {
            Log.e(TAG, "long click $position")
        } else {
            Log.e(TAG, "short click $position")
        }
    }

    override fun onEditClick(view: View, position: Int) {
        Log.e(TAG, "onEditClick $position")
        val dialog = MaterialDialog(this)
            .customView(R.layout.custom_dialog_edit_qty, scrollable = true)
        dialog.cancelable(false)
        dialog.cornerRadius(16f)
        //set custom view
        val customView = editCustomViewDialog(position, dialog)

        //dialog button click
        dialog.positiveButton(R.string.agree) {
            val qty = customView.etxtQty.text.toString()
            updateOrderToDb(position, qty)
        }
        dialog.negativeButton(R.string.disagree) {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDeleteClick(view: View, position: Int) {
        Log.e(TAG, "Delete: $position")
        Log.e(TAG, "Size: ${mList.size}")
        MaterialDialog(this).show {
            icon(R.drawable.stop)
            title(R.string.delete_dialog_title)
            message(R.string.delete_dialog_message)
            cancelable(false)
            cornerRadius(16f)
            positiveButton(R.string.yes) {
                //call delete function
                deleteOrderLine(position)
            }
            negativeButton(R.string.disagree) {
                dismiss()
            }
        }
    }

    private fun createDir() {
        val app_dir: File = File(Environment.getExternalStorageDirectory(), APP_DIRECTORY)

        if (!app_dir.exists()) {
            val isCreated = app_dir.mkdir()
            if (isCreated) {
                Log.e(TAG, "$app_dir : Root directory created")
            } else {
                Log.e(TAG, "$app_dir : Root directory creation failed")
            }
        }

        val import_dir: File = File(Environment.getExternalStorageDirectory(), IMPORT_DIRECTORY)

        if (!import_dir.exists()) {
            val isCreated = import_dir.mkdir()
            if (isCreated) {
                Log.e(TAG, "$import_dir : Import directory created")
            } else {
                Log.e(TAG, "$import_dir : Import directory creation failed")
            }
        }

    }

    private fun requestPermission() {
        Dexter.withActivity(this)
            .withPermissions(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Log.e(TAG, "All permission are granted")
                        createDir()


                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()


                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()

                }
            }).withErrorListener { Log.e(TAG, "Error occurred! ") }.onSameThread()
            .check()

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS"
        ) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.cancel()
            }
        })
        builder.show()

    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    fun checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat
                    .checkSelfPermission(
                        this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
                return
            }
        }

        createDir()
    }

    /*
    request permission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createDir()
                } else {
                    Toast.makeText(this, "Sorry permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        cartMenuItem = menu.findItem(R.id.cart_count_menu_item)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        searchView?.queryHint = "search..."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val filteredList = searchProduct(mList, p0!!)
                mAdaptor?.setFilter(filteredList)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredList = searchProduct(mList, p0!!)
                mAdaptor?.setFilter(filteredList)
                return true
            }

        })

        val actionView = cartMenuItem!!.actionView
        cartCount = actionView.findViewById(R.id.cart_count)
        cartImageView = actionView.findViewById(R.id.cart_image_view)

        return super.onCreateOptionsMenu(menu)
    }

    fun searchProduct(lists: List<ProductProperty>, query: String): ArrayList<ProductProperty> {
        val filteredList = ArrayList<ProductProperty>()

        for (list: ProductProperty in lists) {
            val text = list.item_name?.toLowerCase() + " " + list.item_id + " " + list.barcode
            if (text.contains(query.toLowerCase())) {
                filteredList.add(list)
            }
        }

        return filteredList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item)
    }


    override fun onRestart() {
        super.onRestart()
        getSharedPref()
        getItemFromDb()
        getOrderDetail()

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        MaterialDialog(this).show {
            icon(R.drawable.stop)
            title(R.string.back_press_title)
            message(R.string.back_press_message)
            cancelable(false)
            positiveButton(R.string.yes) {
                finish()
            }
            negativeButton(R.string.disagree) {
                dismiss()
            }
        }
    }


}
