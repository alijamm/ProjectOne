package com.learn.adapters

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.learn.R
import com.learn.models.Radio
import kotlinx.android.synthetic.main.recycler_view_main_list.view.*

class RecyclerViewMainAdapter(layoutResId : Int, itemList : MutableList<Radio>) : BaseQuickAdapter<Radio,BaseViewHolder>(layoutResId,itemList)  {

    override fun convert(helper: BaseViewHolder?, item: Radio?) {
        helper?.setText(R.id.title,item?.name)
        item?.logo?.let { helper?.setImageResource(R.id.logo, it) }

    }


}