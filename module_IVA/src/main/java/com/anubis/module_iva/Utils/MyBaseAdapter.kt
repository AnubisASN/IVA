package com.anubis.module_iva.Utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


import com.anubis.module_iva.R

class MyBaseAdapter(context: Context, private val mDatas: List<dataChatMessage>?) : BaseAdapter() {
    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return mDatas!!.size
    }

    override fun getItem(position: Int): Any {
        return mDatas!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = mDatas!![position]
        return if (chatMessage.type == Type.ME) 0 else 1
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val chatMessage = mDatas!![position]
        var viewHolder: ViewHolder? = null
        if (convertView == null) {
            if (getItemViewType(position) == 0) {
                convertView = mInflater.inflate(R.layout.chat_me, parent,
                        false)
                viewHolder = ViewHolder()
//                viewHolder.mDate = convertView!!
//                        .findViewById(R.id.chat_me_tvTime) as TextView
                viewHolder.mMsg = convertView
                        .findViewById(R.id.chat_me_tvMsg) as TextView
            } else {
                convertView = mInflater.inflate(R.layout.chat_iva, parent,
                        false)
                viewHolder = ViewHolder()
//                viewHolder.mDate = convertView!!
//                        .findViewById(R.id.chat_iva_tvTime) as TextView
                viewHolder.mMsg = convertView
                        .findViewById(R.id.chat_iva_tvMsg) as TextView
            }
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
//        val df = SimpleDateFormat("MM-dd HH:mm")
//        val Time = df.format(chatMessage.date)
//        eLog("ʱ�� ${eGetNumber(Time)-onTime}")
//        if( eGetNumber(Time)-onTime>=1){
//            viewHolder.mDate!!.text = Time
//        }
//        eLog("Time :${eGetNumber(Time)}  onTime: $onTime")
        viewHolder.mMsg!!.text = chatMessage.msg
//        onTime = eGetNumber(Time)
        return convertView!!
    }

    private inner class ViewHolder {
//        internal var mDate: TextView? = null
        internal var mMsg: TextView? = null
    }

}
