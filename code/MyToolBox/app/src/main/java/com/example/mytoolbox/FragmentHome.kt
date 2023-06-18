package com.example.mytoolbox

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import java.time.LocalDate
import java.time.temporal.ChronoUnit

//import com.example.notebook2.notebook2


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHome.newInstance] factory method to
 *
 *  create an instance of this fragment.
 */
class FragmentHome : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val llFun1 = view.findViewById<LinearLayout>(R.id.ll_fun1)
        val llFun2 = view.findViewById<LinearLayout>(R.id.ll_fun2)
        val llFun3 = view.findViewById<LinearLayout>(R.id.ll_fun3)
        if((activity as MainActivity).fun_visibility[0]==0) llFun1.visibility = View.GONE
        else llFun1.visibility = View.VISIBLE
        if((activity as MainActivity).fun_visibility[1]==0) llFun2.visibility = View.GONE
        else llFun2.visibility = View.VISIBLE
        if((activity as MainActivity).fun_visibility[2]==0) llFun3.visibility = View.GONE
        else llFun3.visibility = View.VISIBLE
        val llPlus = view.findViewById<LinearLayout>(R.id.ll_plus)
        llFun1.setOnClickListener {
            val intent = Intent(activity, ActivityClock::class.java)
            startActivity(intent)
        }
        llFun2.setOnClickListener {
            val intent = Intent(activity,ActivityMemorize::class.java)
            startActivity(intent)
        }
        llFun3.setOnClickListener {
            val intent = Intent(activity, notebook2::class.java)
            startActivity(intent)
        }
        llPlus.setOnClickListener {
            (activity as MainActivity).changeFrg((activity as MainActivity).frgFunManage)
        }
        return view
    }
    //for textSwitcher
    private fun createTextView(text: String): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.gravity = Gravity.CENTER
        textView.textSize = 12f
        textView.setTextColor(Color.BLACK)
        return textView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textSwitcher: TextSwitcher = view.findViewById(R.id.textSwitcher)
        textSwitcher.setFactory {
            // 创建一个 TextView 用于显示文本
            val textView = TextView(requireContext())
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            textView.setTextColor(Color.BLACK)
            textView
        }
        textSwitcher.setInAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left))
        textSwitcher.setOutAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_out_right))
        //考研剩余天数
        val examDate = LocalDate.of(2023, 12, 23)
        val currentDate = LocalDate.now()
        Log.d("show date",currentDate.toString())
        val daysLeft = ChronoUnit.DAYS.between(currentDate, examDate)
        view.findViewById<TextView>(R.id.tvCountDown).setText(daysLeft.toString())
        // 添加需要轮播的文本内容
        val texts = arrayOf(
            "徐徐回望，曾属于彼此的晚上", "红红仍是你，赠我的心中艳阳", "如流傻泪，祈望可体恤兼见谅", "明晨离别你，路也许孤单得漫长",
            "一瞬间，太多东西要讲", "可惜即将在各一方，只好深深把这刻尽凝望","来日纵使千千阕歌,飘于远方我路上","来日纵使千千晚星,亮过今晚月亮",
            "都比不起这宵美丽,亦绝不可使我更欣赏","Ah 因你今晚共我唱")

        for (text in texts) {
            textSwitcher.setText(text)
        }

        // 设置轮播时间间隔
        val interval = 5000L
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            var currentIndex = 0
            override fun run() {
                textSwitcher.setText(texts[currentIndex])
                currentIndex = (currentIndex + 1) % texts.size
                handler.postDelayed(this, interval)
            }
        }
        handler.postDelayed(runnable, interval)


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}