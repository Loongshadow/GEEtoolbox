package com.example.mytoolbox

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.nio.BufferUnderflowException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentClockWorking.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentClockWorking : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var isworking:Boolean = false
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var tvCountdown_min: TextView
    private lateinit var tvCountdown_sec: TextView
    private var text:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_clock_working, container, false)
        val bt_stop = view.findViewById<Button>(R.id.btStop)
        val bt_back = view.findViewById<Button>(R.id.btBack)
        var minute:String =""
        var second:String =""
        //countDown function
        tvCountdown_min = view.findViewById(R.id.tv_countdown_minute)
        tvCountdown_sec = view.findViewById(R.id.tv_countdown_second)
        tvCountdown_min.setText("00")
        tvCountdown_sec.setText("10")
        bt_stop.setText("开始")
        bt_back.setOnClickListener {
            tvCountdown_min.setText("00")
            tvCountdown_sec.setText("10")
            if(isworking) countdownTimer.cancel()
            isworking = false
            (activity as ActivityClock).changeFrg((activity as ActivityClock).frgClockHome)
        }
        bt_stop.setOnClickListener setOnClickListenerreturn@{
            val btStatus = bt_stop.text.toString()
            if(btStatus == "开始") {
                minute = tvCountdown_min.text.toString()
                second = tvCountdown_sec.text.toString()
            }
            if(isworking==false){
                var numberRight = true
                for(c in minute+second){
                    if(!isValidNumber(c))
                        numberRight =false
                }
                if(!numberRight){
                    Toast.makeText(requireContext(), "Input Error!!!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListenerreturn
                }
                isworking=true
                var toseconds = tvCountdown_min.text.toString().toLong()*1000*60
                toseconds += tvCountdown_sec.text.toString().toLong()*1000
                countdownTimer = object: CountDownTimer(toseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // 更新UI显示倒计时的时间
                        val timeLeft = millisUntilFinished / 1000
                        val minutes = timeLeft / 60
                        val seconds = timeLeft % 60
                        tvCountdown_min.text = minutes.toString()
                        tvCountdown_sec.text = seconds.toString()
                        //String.format("%02d:%02d", minutes, seconds)
                    }

                    override fun onFinish() {
                        // 倒计时结束时执行的动作
                        //tvCountdown.text = "倒计时结束！"
                        bt_back.setText("返回")
                        bt_stop.setText("开始")
                        // 倒计时结束 更新倒计时记录
                        ////获得当前表格项目数
                        val cursor = MainActivity.dbHelper.readableDatabase.rawQuery("SELECT COUNT(*) FROM TimeRecord", null)
                        var count = 0
                        if (cursor.moveToFirst()) {
                            count = cursor.getInt(0)
                        }
                        cursor.close()
                        ////获得当前日期
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val now = Date()
                        val dateText = dateFormat.format(now)
                        ////获取备注
                        text=""
                        var comment:String =""
                        alertEdit(){
                            Log.d("comment",text)
                            comment = text
                            val values = ContentValues()
                            values.put("id", count+1)
                            values.put("date", dateText)
                            values.put("length",minute+"分"+second+"秒")
                            values.put("comment",comment)
                            MainActivity.dbHelper.writableDatabase.insert("TimeRecord",null,values)
                        }
                        isworking = false
                    }
                }
                // 启动倒计时
                countdownTimer.start()
                bt_stop.setText("停止")
            }else{
                isworking=false
                countdownTimer.cancel()
                bt_stop.setText("继续")
            }
        }

         //定义倒计时对象，倒计时10秒，每隔1秒更新一次UI
//        countdownTimer = object : CountDownTimer(1500000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                // 更新UI显示倒计时的时间
//                val timeLeft = millisUntilFinished / 1000
//                val minutes = timeLeft / 60
//                val seconds = timeLeft % 60
//                tvCountdown.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                // 倒计时结束时执行的动作
//                tvCountdown.text = "倒计时结束！"
//            }
//        }
//
//        // 启动倒计时
//        countdownTimer.start()

        return view
    }

    //弹出 编辑对话窗
    private var editText:EditText?=null
    private fun alertEdit(callback: () -> Unit) {
        editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("添加备注")
            .setIcon(android.R.drawable.sym_def_app_icon)
            .setView(editText)
            .setPositiveButton("确定") { dialogInterface: DialogInterface?, i: Int ->
                text = editText?.text.toString()
                Log.d("comment",text)
                callback()
                //Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    private fun isValidNumber(str1: Char): Boolean {
        val regex = """^\d$""".toRegex()
        var str = ""
        str+=str1
        return regex.matches(str)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentClockWorking.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentClockWorking().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var countdownTimer: CountDownTimer
//    private lateinit var tvCountdown: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        tvCountdown = findViewById(R.id.tv_countdown)
//
//        // 定义倒计时对象，倒计时10秒，每隔1秒更新一次UI
//        countdownTimer = object : CountDownTimer(10000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                // 更新UI显示倒计时的时间
//                val timeLeft = millisUntilFinished / 1000
//                tvCountdown.text = timeLeft.toString()
//            }
//
//            override fun onFinish() {
//                // 倒计时结束时执行的动作
//                tvCountdown.text = "倒计时结束！"
//            }
//        }
//
//        // 启动倒计时
//        countdownTimer.start()
//    }
//}