package com.example.mytoolbox

//import android.R
import MyDatabaseHelper
import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    //val dbHelper = MyDatabaseHelper(this,"Box.db",1)
    var fun_visibility = arrayOf(0, 0, 0)
    val frgHome = FragmentHome()
    val frgMine = FragmentMine()
    val frgSetting = FragmentSetting()
    val frgAboutus = FragmentAboutus()
    val frgCallus = FragmentCallus()
    val frgFunManage = FragmentFunManager()
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //-------------------------------------
        //SQL
        dbHelper = MyDatabaseHelper(this,"Box.db",1)
        val db = dbHelper.writableDatabase
        val fuName = arrayOf("fun1","fun2","fun3")
        val index = arrayOf(0,1,2)
        for(i in index) {
            val selection = "funName = ?"
            val selectionArgs = arrayOf(fuName[i])
            val cursor = db.query("FunStatus", null, selection, selectionArgs, null, null, null)
            if (cursor.moveToFirst()) {
                // 行已存在，不进行操作
                fun_visibility[i] = cursor.getInt(cursor.getColumnIndex("status"))
                Log.d("funStatus",fun_visibility[i].toString())
            } else {
                // 行不存在，执行插入操作
                val values = ContentValues().apply {
                    put("funName", fuName[i])
                    put("status", 1)
                }
                val id = db.insert("FunStatus", null, values)
            }
            cursor.close()
        }
        //-------------------------------------
        val myBtHome = findViewById<Button>(R.id.BtHome)
        val myBtMine = findViewById<Button>(R.id.BtMine)
        supportFragmentManager.beginTransaction().add(R.id.HomeFragmentContainer,frgHome).commit()
        myBtHome.setOnClickListener{
            changeFrg(frgHome)
        }
        myBtMine.setOnClickListener{
            changeFrg(frgMine)
        }
    }
    fun changeFrg(frg:Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.HomeFragmentContainer,frg).commit()
    }
    companion object {
        lateinit var dbHelper: MyDatabaseHelper
    }
}