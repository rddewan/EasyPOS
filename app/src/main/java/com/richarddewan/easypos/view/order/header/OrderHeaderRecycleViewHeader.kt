package com.richarddewan.easypos.view.order.header

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.richarddewan.easypos.R
import com.richarddewan.easypos.model.entity.OrderEntity
import com.richarddewan.easypos.view.order.header.interfaces.OrderHeaderClickListener

class OrderHeaderRecycleViewHeader(var mDataList: ArrayList<OrderEntity>,var mContext:Context) : RecyclerView.Adapter<OrderHeaderRecycleViewHeader.ViewHolder>() {
    private var orderHeaderClickListener:OrderHeaderClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_order_detail_header,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderId?.text = mDataList.get(position).order_id
        holder.order_status?.text = mDataList.get(position).order_status
        if (mDataList.get(position).order_status.equals("CLOSED")){
            holder.order_status?.setTextColor(mContext.resources.getColor(R.color.smashed_pumpkin))
        }
        else {
            holder.order_status?.setTextColor(mContext.resources.getColor(R.color.bondi_blue))
        }

        holder.onClickListener(orderHeaderClickListener!!)
    }

    fun setClickListener(orOrderHeaderClickListener: OrderHeaderClickListener){
        this.orderHeaderClickListener = orOrderHeaderClickListener
    }

    fun setFilter(list:ArrayList<OrderEntity>){
        this.mDataList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view),View.OnClickListener,View.OnLongClickListener{
        private var orderHeaderClickListener:OrderHeaderClickListener? = null
        var orderId:TextView? = null
        var order_status:TextView? = null
        init {

            orderId = view.findViewById(R.id.txtOrderId)
            order_status = view.findViewById(R.id.txtOrderStatus)

            view.setOnClickListener(this)
            view.setOnLongClickListener(this)

        }

        fun onClickListener(orderHeaderClickListener: OrderHeaderClickListener){
            this.orderHeaderClickListener =  orderHeaderClickListener
        }

        override fun onClick(p0: View?) {
            orderHeaderClickListener!!.onClick(p0,adapterPosition,false)
        }

        override fun onLongClick(p0: View?): Boolean {
            orderHeaderClickListener!!.onClick(p0,adapterPosition,true)
            return false
        }

    }
}