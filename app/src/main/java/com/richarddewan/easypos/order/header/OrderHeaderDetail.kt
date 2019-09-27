package com.richarddewan.easypos.order.header

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.richarddewan.easypos.R
import com.richarddewan.easypos.config.OrderStatus
import com.richarddewan.easypos.database.DbHelper
import com.richarddewan.easypos.order.header.interfaces.OrderHeaderClickListener
import com.richarddewan.easypos.order.line.OrderLineRecycleViewAdaptor
import com.richarddewan.easypos.order.line.OrderLineProperty
import com.richarddewan.easypos.order.line.interfaces.OrderLineClickListener
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.custom_cart_view.view.*
import kotlinx.android.synthetic.main.custom_dialog_qty.view.*

class OrderHeaderDetail : AppCompatActivity(), OrderHeaderClickListener , OrderLineClickListener{

    val TAG = "OrderHeaderDetail"
    private var toolbar: Toolbar? = null
    private var dbHelper:DbHelper? = null
    private var recycleViewOrderHeader:RecyclerView? = null
    private var mAdaptorOrderHeader:OrderHeaderRecycleViewHeader? = null
    private var mListOrderHeader = ArrayList<OrderHeaderProperty>()
    private var mLayoutManagerOrderHeader: StaggeredGridLayoutManager? = null
    // line
    private var recyclerViewLine:RecyclerView? = null
    private var mAdaptorLine:OrderLineRecycleViewAdaptor? = null
    private var mLayoutManagerLine:RecyclerView.LayoutManager? = null
    private var mListOrderLine = ArrayList<OrderLineProperty>()

    private var searchView:SearchView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        toolbar = findViewById(R.id.menu_bar) as Toolbar
        toolbar?.title = ""
        setSupportActionBar(toolbar)

        //
        getOrderHeader()
        //
        recycleViewHeader()
        //
        btn_menu_back.setOnClickListener{
            this.finish()
        }
    }

    fun getOrderHeader(){
        //mListOrderHeader.clear()
        dbHelper = DbHelper(applicationContext)
        mListOrderHeader = dbHelper!!.getOrderHeader()
        dbHelper!!.close()
    }

    fun recycleViewHeader(){
        //find recycle view
        recycleViewOrderHeader = findViewById(R.id.recycleView_order_header);
        //set layout manager
        mLayoutManagerOrderHeader = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        //set recycle view layout manager
        recycleViewOrderHeader?.layoutManager = mLayoutManagerOrderHeader
        //set adaptor
        mAdaptorOrderHeader = OrderHeaderRecycleViewHeader(mListOrderHeader,this)
        //set recyle view adaptor
        recycleViewOrderHeader?.adapter = mAdaptorOrderHeader
        //set recycle view click listener
        mAdaptorOrderHeader?.setClickListener(this)

    }

    fun getOrderLine(orderId:String){
        dbHelper = DbHelper(applicationContext)
        mListOrderLine = dbHelper!!.getOrderLine(orderId)
        dbHelper!!.close()

        //set recycle view
        recycleViewLine()
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
        dbHelper = DbHelper(applicationContext)
        dbHelper?.deleteOrderLine(data.order_id!!, data.line_number!!)
        dbHelper?.close()

        //get order detail
        val orderId = mListOrderHeader.get(position).orderId
        getOrderLine(orderId!!)
    }

    fun updateOrderToDb(position: Int, qty: String) {
        val data = mListOrderLine[position]
        dbHelper = DbHelper(applicationContext)
        dbHelper?.updateOrderDetail(data.order_id!!, data.product_id!!, data.line_number!!, qty)
        dbHelper?.close()

        //get order detail
        getOrderLine(data.order_id!!)

    }

    override fun onClick(view: View?, position: Int, isLongClick: Boolean) {
        if (isLongClick){
            Log.e(TAG," long click")
        }
        else {
            Log.e(TAG,"short click")
            val orderId  = mListOrderHeader.get(position).orderId
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

    fun searchOrder(lists:List<OrderHeaderProperty>,query:String) : ArrayList<OrderHeaderProperty>{
        val filteredList = ArrayList<OrderHeaderProperty>()

        for (list:OrderHeaderProperty in lists){
            val text  = list.orderId?.toLowerCase() + " " + list.order_status?.toLowerCase()
            if (text?.contains(query.toLowerCase())){
                filteredList.add(list)
            }
        }

        return filteredList
    }

}
