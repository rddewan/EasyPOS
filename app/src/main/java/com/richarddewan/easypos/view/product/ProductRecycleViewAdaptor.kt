package com.richarddewan.easypos.view.download.product

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikepenz.iconics.Iconics.applicationContext
import com.richarddewan.easypos.R
import com.richarddewan.easypos.model.entity.ProductEntity
import com.richarddewan.easypos.view.download.product.interfaces.ProductClickListener
import com.richarddewan.easypos.view.utils.AnimationUtils



class ProductRecycleViewAdaptor(private var mDataList: List<ProductEntity>) : RecyclerView.Adapter<ProductRecycleViewAdaptor.ViewHolder>() {
    private var productClickListener:ProductClickListener? = null
    var view:View? = null
    private var mPreviousPosition = 0
    private val FADE_DURATION = 1000 //FADE_DURATION in milliseconds

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.custom_product_detail_view,parent,false)
        return ViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return  mDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.product_id?.text = mDataList.get(position).product_id
        holder.item_id?.text = mDataList.get(position).item_id
        holder.item_name?.text = mDataList.get(position).item_name
        holder.barcode?.text = mDataList.get(position).barcode
        //load image from url to image view
        Glide
            .with(applicationContext)
            .load(mDataList.get(position).image)
            .centerCrop()
            .placeholder(R.drawable.ic_shopify_grey600_36dp)
            .into(holder.productImage!!)

        /*Picasso.get().load(mDataList.get(position).image)
            .resize(56, 56)
            .centerCrop()
            .into(holder.productImage)*/


        holder.setProductClickListener(productClickListener!!)

        holder.btnCart?.setOnClickListener {
            productClickListener?.onBtnCartClick(it,position)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {

            if (position > mPreviousPosition) {
                AnimationUtils.animateSunblind(holder, true)
            } else {
                AnimationUtils.animateSunblind(holder, false)
            }
        } else {
            // Set the view to fade in
            setFadeAnimation(holder.itemView)
            // call Animation function
            //setAnimation(holder.itemView, position);
        }
        mPreviousPosition = position

    }

    fun setProductClickListener(productClickListener: ProductClickListener){
        this.productClickListener = productClickListener
    }

    fun setFilter(list:List<ProductEntity>){
        this.mDataList = list
        notifyDataSetChanged()
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = FADE_DURATION.toLong()
        view.startAnimation(anim)
    }


    inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view), View.OnClickListener,View.OnLongClickListener{

        var product_id:TextView? = null
        var item_id:TextView? = null
        var item_name:TextView? = null
        var barcode:TextView? = null
        var btnCart:ImageView? = null
        var productImage:ImageView? = null
        private var productClickListener:ProductClickListener? = null


        init {
            product_id = view.findViewById(R.id.txtProductId)
            item_id = view.findViewById(R.id.txtItemId)
            item_name = view.findViewById(R.id.txtItemName)
            barcode = view.findViewById(R.id.txtBarcode)
            btnCart = view.findViewById(R.id.btnCart)
            productImage = view.findViewById(R.id.productImage)

            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        fun setProductClickListener(productClickListener: ProductClickListener){
            this.productClickListener = productClickListener
        }

        override fun onClick(p0: View?) {
            productClickListener?.onProductClick(p0,adapterPosition,false)
        }

        override fun onLongClick(p0: View?): Boolean {
            productClickListener?.onProductClick(p0,adapterPosition,true)
            return true
        }


    }
}