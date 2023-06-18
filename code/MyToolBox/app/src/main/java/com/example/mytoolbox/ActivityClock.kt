package com.example.mytoolbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class ActivityClock : AppCompatActivity() {
    val frgClockHome = FragmentClockHome()
    val frgClockWorking = FragmentClockWorking()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
        supportFragmentManager.beginTransaction().add(R.id.ClockFragmentContainer,frgClockHome).commit()
    }
    fun changeFrg(frg: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.ClockFragmentContainer,frg).commit()
    }
}