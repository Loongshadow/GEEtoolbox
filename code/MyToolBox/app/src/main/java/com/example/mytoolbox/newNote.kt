package com.example.mytoolbox

import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class newNote : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)//设置无原始标题栏
        setContentView(R.layout.activity_new_note)
        //收回modify
        val modify=intent.getStringExtra("modify");
        val lastText=findViewById<EditText>(R.id.text)
        if(modify!=null)
        {
            lastText.setText(modify)
        }

        //回退按钮
        var button_back=findViewById<Button>(R.id.back);
        button_back.setOnClickListener {
            val intent = Intent(this, notebook2::class.java);
            //文本和时间
            val text=findViewById<EditText>(R.id.text).text.toString()
            val writeTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            intent.putExtra("noteText",text);
            intent.putExtra("writeTime",writeTime)
            startActivity(intent)

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }
    }
}