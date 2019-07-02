package com.fcen.seguridad.Sms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcen.seguridad.R


class SmsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        val allSms = SmsHandler().getAllSms(this)
        val mAdapter = SmsAdapter()
        val mRecyclerView :RecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.SmsAdapter(allSms, this)
        mRecyclerView.adapter = mAdapter
    }
}