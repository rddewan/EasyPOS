package com.richarddewan.easypos.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.richarddewan.easypos.R
import com.richarddewan.easypos.order.interfaces.CartItemClickListener

class CartRecycleViewAdaptor(var mDataList: ArrayList<OrderProperty>) : RecyclerView.Adapter<CartRecycleViewAdaptor.ViewHolder>() {
    private var cartItemClickListener:CartItemClickListener? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_cart_view,parent,false)
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

        holder.setCartItemClickListener(cartItemClickListener!!)

        holder.imgEdit?.setOnClickListener{
            cartItemClickListener?.onEditClick(it,position)
        }
        holder.imgDelete?.setOnClickListener{
            cartItemClickListener?.onDeleteClick(it,position)
        }
    }

    fun setCartItemClickListener(cartItemClickListener: CartItemClickListener){
        this.cartItemClickListener = cartItemClickListener

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener,View.OnLongClickListener{
        var product_id: TextView? = null
        var item_id: TextView? = null
        var item_name: TextView? = null
        var barcode: TextView? = null
        var qty:TextView? = null
        var imgEdit: ImageView? = null
        var imgDelete: ImageView? = null
        private var cartItemClickListener:CartItemClickListener? = null

        init {
            product_id = view.findViewById(R.id.txtProductId)
            item_id = view.findViewById(R.id.txtItemId)
            item_name = view.findViewById(R.id.txtName)
            barcode = view.findViewById(R.id.txtBarcode)
            qty = view.findViewById(R.id.txtQty)
            imgEdit = view.findViewById(R.id.imgEdit)
            imgDelete = view.findViewById(R.id.imgDelete)

            view.setOnLongClickListener(this)
            view.setOnClickListener(this)
        }

        fun setCartItemClickListener(cartItemClickListener: CartItemClickListener){
            this.cartItemClickListener = cartItemClickListener
        }
        override fun onClick(p0: View?) {
            cartItemClickListener?.onCartClick(p0,adapterPosition,false)
        }

        override fun onLongClick(p0: View?): Boolean {
            cartItemClickListener?.onCartClick(p0,adapterPosition,true)
            return false
        }

    }

}