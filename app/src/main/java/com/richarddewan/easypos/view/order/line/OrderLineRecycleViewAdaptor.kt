package com.richarddewan.easypos.view.order.line

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.richarddewan.easypos.R
import com.richarddewan.easypos.model.entity.OrderEntity
import com.richarddewan.easypos.view.order.line.interfaces.OrderLineClickListener

class OrderLineRecycleViewAdaptor(var mDataList: ArrayList<OrderEntity>): RecyclerView.Adapter<OrderLineRecycleViewAdaptor.ViewHolder>(){
    var orderLineOnClickListener:OrderLineClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_order_detail_line,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.product_id?.text = mDataList.get(position).product_id
        holder.item_id?.text = mDataList.get(position).item_id
        holder.item_name?.text = mDataList.get(position).item_name
        holder.barcode?.text = mDataList.get(position).barcode
        holder.qty?.text = mDataList.get(position).qty

        holder.setOnClickListener(orderLineOnClickListener!!)

        holder.imgEdit?.setOnClickListener{
            orderLineOnClickListener?.onLineEditClick(it,position)
        }
        holder.imgDelete?.setOnClickListener{
            orderLineOnClickListener?.onLineDeleteClcik(it,position)
        }
    }

    fun setOnLineItemClickListener(orderLineClickListener: OrderLineClickListener){
        this.orderLineOnClickListener = orderLineClickListener
    }


    inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view),View.OnClickListener,View.OnLongClickListener{

        var product_id: TextView? = null
        var item_id: TextView? = null
        var item_name: TextView? = null
        var barcode: TextView? = null
        var qty: TextView? = null
        var imgEdit: ImageView? = null
        var imgDelete: ImageView? = null
        var orderLineOnClickListener:OrderLineClickListener? = null

        init {
            product_id = view.findViewById(R.id.txtProductId)
            item_id = view.findViewById(R.id.txtItemId)
            item_name = view.findViewById(R.id.txtName)
            barcode = view.findViewById(R.id.txtBarcode)
            qty = view.findViewById(R.id.txtQty)
            imgEdit = view.findViewById(R.id.imgEdit)
            imgDelete = view.findViewById(R.id.imgDelete)

            view.setOnClickListener(this)
            view.setOnLongClickListener(this)

        }

        fun setOnClickListener(orderLineClickListener: OrderLineClickListener){
            this.orderLineOnClickListener = orderLineClickListener
        }

        override fun onClick(p0: View?) {
            orderLineOnClickListener?.onLineItemClick(p0,adapterPosition,false)
        }

        override fun onLongClick(p0: View?): Boolean {
            orderLineOnClickListener?.onLineItemClick(p0,adapterPosition,true)

            return true
        }

    }
}