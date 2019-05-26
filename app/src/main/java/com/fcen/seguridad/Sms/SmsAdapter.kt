package com.fcen.seguridad.Sms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcen.seguridad.R

class SmsAdapter : RecyclerView.Adapter<SmsAdapter.ViewHolder>() {

    var sms: List<Sms> = ArrayList()
    lateinit var context:Context

    fun SmsAdapter(sms : List<Sms>, context: Context){
        this.sms = sms
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sms.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.sms_list, parent, false))
    }

    override fun getItemCount(): Int {
        return sms.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val address = view.findViewById(R.id.address) as TextView
        val msg = view.findViewById(R.id.msg) as TextView

        fun bind(sms:Sms, context: Context){
            address.text = sms.address
            msg.text = sms.msg
        }
    }
}