package com.richarddewan.easypos.view.order

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.tscdll.TscWifiActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.richarddewan.easypos.R
import com.richarddewan.easypos.model.entity.OrderEntity
import com.richarddewan.easypos.view.config.OrderStatus
import com.richarddewan.easypos.view.order.header.OrderHeaderRecycleViewHeader
import com.richarddewan.easypos.view.order.header.interfaces.OrderHeaderClickListener
import com.richarddewan.easypos.view.order.line.OrderLineRecycleViewAdaptor
import com.richarddewan.easypos.view.order.line.interfaces.OrderLineClickListener
import com.richarddewan.easypos.viewmodel.OrderDetailViewModel
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.custom_cart_view.view.*
import kotlinx.android.synthetic.main.custom_dialog_qty.view.*

class OrderDetail : AppCompatActivity(), OrderHeaderClickListener , OrderLineClickListener{

    val TAG = "OrderDetail"
    private var toolbar: Toolbar? = null
    private var recycleViewOrderHeader:RecyclerView? = null
    private var mAdaptorOrderHeader: OrderHeaderRecycleViewHeader? = null
    private var mListOrderHeader: ArrayList<OrderEntity> = ArrayList()
    private var mLayoutManagerOrderHeader: StaggeredGridLayoutManager? = null
    // line
    private var recyclerViewLine:RecyclerView? = null
    private var mAdaptorLine:OrderLineRecycleViewAdaptor? = null
    private var mLayoutManagerLine:RecyclerView.LayoutManager? = null
    private var mListOrderLine : ArrayList<OrderEntity> = ArrayList()

    private var searchView:SearchView? = null
    private var sharedPreferences: SharedPreferences? = null
    private var dialog: MaterialDialog? = null
    private var print_server_ip:String? = null
    private val TscEthernetDll: TscWifiActivity = TscWifiActivity()
    private var orderDetailViewModel: OrderDetailViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        toolbar = findViewById(R.id.menu_bar) as Toolbar
        toolbar?.title = ""
        setSupportActionBar(toolbar)

        //
        orderDetailViewModel = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        //
        getSharedPref()
        //get order header
        getOrderHeader()
        //live view
        recycleViewLine()
        //
        btn_menu_back.setOnClickListener{
            this.finish()
        }
        //load ads
        //MobileAds.initialize(this) {}
        val mAdView = findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }

    fun getSharedPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        print_server_ip = sharedPreferences?.getString("print_server_address", "")
    }

    fun getOrderHeader(){
        orderDetailViewModel?.getOrderHeader()?.observe(this, object : Observer<List<OrderEntity>> {
            override fun onChanged(t: List<OrderEntity>?) {
                mListOrderHeader = t as ArrayList<OrderEntity>
                //set recycle view
                recycleViewHeader()
            }

        })
    }

    fun recycleViewHeader(){
        //find recycle view
        recycleViewOrderHeader = findViewById(R.id.recycleView_order_header);
        val orientation = this.getResources().getConfiguration().orientation

        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            //set layout manager
            mLayoutManagerOrderHeader = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }
        else {
            //set layout manager
            mLayoutManagerOrderHeader = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        }
        //set recycle view layout manager
        recycleViewOrderHeader?.layoutManager = mLayoutManagerOrderHeader
        //set adaptor
        mAdaptorOrderHeader =
            OrderHeaderRecycleViewHeader(
                mListOrderHeader,
                this
            )
        //set recyle view adaptor
        recycleViewOrderHeader?.adapter = mAdaptorOrderHeader
        //set recycle view click listener
        mAdaptorOrderHeader?.setClickListener(this)

    }

    fun recycleViewLine(){
        //find recycle view
        recyclerViewLine = findViewById(R.id.recycleView_order_line)
        //layout manager
        mLayoutManagerLine = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        //set recycle view layout manager
        recyclerViewLine?.layoutManager = mLayoutManagerLine
        //set adaptor
        mAdaptorLine = OrderLineRecycleViewAdaptor(mListOrderLine)
        //recycle view data adaptor
        recyclerViewLine?.adapter = mAdaptorLine
        //click listener
        mAdaptorLine?.setOnLineItemClickListener(this)

    }

    fun getOrderLine(orderId:String){
        orderDetailViewModel?.getOrderLine(orderId)?.observe(this, object : Observer<List<OrderEntity>>{
            override fun onChanged(t: List<OrderEntity>?) {
                mListOrderLine = t as ArrayList<OrderEntity>
                //set recycle view
                setOrderDetail()
            }
        })
    }

    fun setOrderDetail() {
        //set adaptor
        mAdaptorLine = OrderLineRecycleViewAdaptor(mListOrderLine)
        //recycle view data adaptor
        recyclerViewLine?.adapter = mAdaptorLine
        //click listener
        mAdaptorLine?.setOnLineItemClickListener(this)

    }

    fun editCustomViewDialog(position: Int, dialog: MaterialDialog): View {
        val customView = dialog.getCustomView()
        val data = mListOrderLine[position]
        customView.lbName?.text = data.item_name
        customView.etxtQty.setText(data.qty.toString())
        customView.etxtQty.requestFocus()
        customView.etxtQty.selectAll()

        return customView
    }

    fun deleteOrderLine(position: Int) {
        val data = mListOrderLine.get(position)
        orderDetailViewModel?.deleteOrderLine(data.order_id!!,data.product_id!!,data.line_number!!)

        //get order detail
        val orderId = data.order_id
        getOrderLine(orderId!!)
    }

    fun updateOrderToDb(position: Int, qty: String) {
        val data = mListOrderLine[position]
        val orderEntity = OrderEntity(
            data.order_id!!,
            data.line_number!!,
            data.product_id!!,
            data.item_id!!,
            data.item_name!!,
            data.barcode!!,
            qty,
            data.product_image!!,
            data.order_status!!
        )
        orderDetailViewModel?.updateOrder(orderEntity)

        //get order detail
        getOrderLine(data.order_id!!)

    }

    override fun onClick(view: View?, position: Int, isLongClick: Boolean) {
        if (isLongClick){
            Log.e(TAG," long click")
        }
        else {
            Log.e(TAG,"short click")
            val orderId  = mListOrderHeader.get(position).order_id
            getOrderLine(orderId!!)
        }
    }

    override fun onLineEditClick(view: View, position: Int) {
        val selectedOrderStatus =  mListOrderLine.get(position).order_status
        val orderStatus = OrderStatus.CLOSED
        if (selectedOrderStatus == orderStatus.toString()){
            MaterialDialog(this).show {
                cornerRadius(16f)
                icon(R.drawable.ic_user)
                title(R.string.delete_dialog_title)
                message(R.string.order_conformed_edit_message)
                cancelable(false)
                positiveButton(R.string.agree) {
                    dismiss()
                }
            }
        }
        else {
            val dialog = MaterialDialog(this)
                .customView(R.layout.custom_dialog_edit_qty)
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
    }

    override fun onLineDeleteClcik(view: View, position: Int) {
        val selectedOrderStatus =  mListOrderLine.get(position).order_status
        val orderStatus = OrderStatus.CLOSED
        if (selectedOrderStatus == orderStatus.toString()){
            MaterialDialog(this).show {
                cornerRadius(16f)
                icon(R.drawable.ic_user)
                title(R.string.delete_dialog_title)
                message(R.string.order_conformed_delete_message)
                cancelable(false)
                positiveButton(R.string.agree) {
                    dismiss()
                }
            }
        }
        else {
            MaterialDialog(this).show {
                cornerRadius(16f)
                icon(R.drawable.stop)
                title(R.string.delete_dialog_title)
                message (R.string.delete_dialog_message)
                positiveButton (R.string.yes) {
                    deleteOrderLine(position)
                }
                negativeButton(R.string.disagree){
                    dismiss()
                }
            }
        }

    }

    override fun onLineItemClick(view: View?, position: Int, isLongClick: Boolean) {
        if (isLongClick){
            Log.e(TAG," long click")
        }
        else {
            Log.e(TAG,"short click")
        }
    }

    fun print(view: View) {
        if (mListOrderLine.size > 0) {
            MaterialDialog(this).show {
                icon(R.drawable.ic_user)
                title(R.string.print_dialog_title)
                message(R.string.print_dialog_message)
                cancelable(false)
                cornerRadius(16f)
                positiveButton(R.string.yes) {
                    dismiss()
                    //print to printer
                    PrintNetwork().execute()
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

    private inner class PrintNetwork : AsyncTask<Void, String, String>() {
        var status = ""
        override fun onPreExecute() {
            super.onPreExecute()
            showPrintDialog()
        }

        override fun doInBackground(vararg p0: Void?): String {
            for (i in 0..mListOrderLine.size - 1) {
                try {
                    val data = mListOrderLine.get(i)
                    val order_id = data.order_id
                    val barcode = data.barcode
                    val item_name = data.item_name
                    val qty = data.qty
                    val line_number = data.line_number

                    TscEthernetDll.openport(print_server_ip, 9100, 0)
                    TscEthernetDll.setup(80,40,10,10,0,0,0)
                    //String status = TscEthernetDll.printerstatus(300);
                    TscEthernetDll.clearbuffer();
                    //TscEthernetDll.sendcommand("SIZE 90 mm, 40 mm\r\n");
                    //TscEthernetDll.sendcommand("GAP 2 mm, 0 mm\r\n");//Gap media
                    //TscEthernetDll.sendcommand("BLINE 2 mm, 0 mm\r\n");//blackmark media
                    //TscEthernetDll.sendcommand("SPEED 4\r\n")
                    //TscEthernetDll.sendcommand("DENSITY 12\r\n")
                    //TscEthernetDll.sendcommand("CODEPAGE UTF-8\r\n")
                    //TscEthernetDll.sendcommand("SET TEAR ON\r\n")
                    //TscEthernetDll.sendcommand("SET COUNTER @1 1\r\n")
                    //TscEthernetDll.sendcommand("@1 = \"0001\"\r\n")
                    //TscEthernetDll.sendcommand("TEXT 100,300,\"ROMAN.TTF\",0,12,12,@1\r\n")
                    //TscEthernetDll.sendcommand("TEXT 100,400,\"ROMAN.TTF\",0,12,12,\"TEST FONT\"\r\n")
                    TscEthernetDll.printerfont(100, 100, "3", 0, 1, 1, "OrderId:$order_id  Line:$line_number")
                    TscEthernetDll.printerfont(100, 130, "3", 0, 1, 1, "$item_name")
                    TscEthernetDll.printerfont(100, 160, "3", 0, 1, 1, "Qty :  $qty")
                    TscEthernetDll.barcode(100, 190, "128", 100, 1, 0, 3, 3, barcode)
                    TscEthernetDll.printlabel(1, 1)
                    TscEthernetDll.closeport(1000)//5sec

                } catch (er: Exception) {
                    Log.e(TAG, er.message.toString())
                }
                //set status to success
                status = "SUCCESS"

            }

            return status
        }

        override fun onPostExecute(result: String?) {
            //super.onPostExecute(result)
            //update order status
            if (result == "SUCCESS") {
                showPrintDialog()

            }
        }
    }

    fun showPrintDialog() {
        if (dialog == null) {
            dialog = MaterialDialog(this)
                .customView(R.layout.progress)
            dialog!!.cancelable(false)
            dialog!!.show()

        } else {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
                dialog = null
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_detail,menu)
        searchView = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView
        searchView?.queryHint = "search"
        searchView!!.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = searchOrder(mListOrderHeader,newText!!)
                mAdaptorOrderHeader?.setFilter(filteredList)
                return true
            }

        }

        )

        return super.onCreateOptionsMenu(menu)
    }

    fun searchOrder(lists:List<OrderEntity>,query:String) : ArrayList<OrderEntity>{
        val filteredList = ArrayList<OrderEntity>()

        for (list:OrderEntity in lists){
            val text  = list.order_id?.toLowerCase() + " " + list.order_status?.toLowerCase()
            if (text.contains(query.toLowerCase())){
                filteredList.add(list)
            }
        }

        return filteredList
    }

}
