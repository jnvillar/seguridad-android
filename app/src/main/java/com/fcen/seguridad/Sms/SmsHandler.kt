package com.fcen.seguridad.Sms

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.fcen.seguridad.MainActivity

public class SmsHandler{
    fun getAllSms(mainActivity: Activity): List<Sms> {
        val lstSms = ArrayList<Sms>()
        val message = Uri.parse("content://sms/")
        val cr = mainActivity.getContentResolver()

        val c = cr.query(message, null, null, null, null)
        mainActivity.startManagingCursor(c)
        val totalSMS = c!!.getCount()

        if (c!!.moveToFirst()) {
            for (i in 0 until totalSMS) {

                val objSms = Sms()
                objSms.setId(c!!.getString(c!!.getColumnIndexOrThrow("_id")))
                objSms.setAddress(
                    c!!.getString(
                        c!!
                            .getColumnIndexOrThrow("address")
                    )
                )
                objSms.setMsg(c!!.getString(c!!.getColumnIndexOrThrow("body")))
                objSms.setReadState(c!!.getString(c!!.getColumnIndex("read")))
                objSms.setTime(c!!.getString(c!!.getColumnIndexOrThrow("date")))
                if (c!!.getString(c!!.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox")
                } else {
                    objSms.setFolderName("sent")
                }

                lstSms.add(objSms)
                c!!.moveToNext()
            }
        }
        else {
            Log.d("NAVIGATION", "no sms found")
        }
        c!!.close()
        return lstSms
    }
}

