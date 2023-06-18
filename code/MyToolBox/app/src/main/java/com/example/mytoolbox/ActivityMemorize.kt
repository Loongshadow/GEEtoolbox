package com.example.mytoolbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class ActivityMemorize : AppCompatActivity() {
    val frgMemorizeHome = FragmentMemorizeHome()
    val frgShowword = FragmentShowword()
    var whichBook:String = "" //to link FragmentShowword and FragmentMemorizeHome|select which book show words in it
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memorize)
        supportFragmentManager.beginTransaction().add(R.id.MemorizeFragmentContainer,frgMemorizeHome).commit()
    }
    fun changeFrg(frg: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.MemorizeFragmentContainer,frg).commit()
    }
}