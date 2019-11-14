package com.jarvis.zhihudemo.avtivity

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.jarvis.zhihudemo.R
import com.jarvis.zhihudemo.base.BaseActivity
import com.jarvis.zhihudemo.widgets.layoutmanager.CustomLinearLayoutManager
import com.jarvis.zhihudemo.widgets.layoutmanager.StackLayoutManager
import com.jarvis.zhihudemo.widgets.layoutmanager.TrapezoidLayoutManager

/**
 * @author yyf
 * @since 11-12-2019
 */
class CustomLayoutManager2Activity : BaseActivity() {

    private var horRes = arrayListOf(R.drawable.h5, R.drawable.h6, R.drawable.h7, R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4, R.drawable.h5, R.drawable.h6, R.drawable.h7, R.drawable.h5, R.drawable.h6, R.drawable.h7, R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4, R.drawable.h5, R.drawable.h6, R.drawable.h7)
    private var verRes = arrayListOf(R.drawable.v5, R.drawable.v6, R.drawable.v7, R.drawable.v1, R.drawable.v2, R.drawable.v3, R.drawable.v4, R.drawable.v5, R.drawable.v6, R.drawable.v7)

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_layout_manager)
        recyclerView = findViewById(R.id.rv)
        recyclerView.run {
            adapter = Adapter(horRes)
//            layoutManager = CustomLinearLayoutManager()
            layoutManager = TrapezoidLayoutManager()
//            layoutManager = StackLayoutManager()
        }
    }

    inner class Adapter(data: List<Int>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        private var datas: List<Int> = data

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return datas.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val realPosition = position % datas.size
            Glide.with(holder.iv.context).load(datas[realPosition]).into(holder.iv)
            holder.tv.text = datas[realPosition].toString()

        }

        inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            var iv: ImageView = itemView.findViewById(R.id.item_iv)
            var tv: TextView = itemView.findViewById(R.id.item_tv)
        }
    }


    fun dp2px(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.resources.displayMetrics)
    }

}