package com.example.mytoolbox

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentClockHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentClockHome : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var listView: ListView
    var historyData = mutableListOf<String>()
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
        val view = inflater.inflate(R.layout.fragment_clock_home, container, false)
        listView = view.findViewById(R.id.lv_history)
        val ll_new = view.findViewById<LinearLayout>(R.id.ll_newclock)
        ll_new.setOnClickListener {
            (activity as ActivityClock).changeFrg((activity as ActivityClock).frgClockWorking)
        }
        return view
    }
    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //show history
        //read SQL/not pass test yet
        historyData.clear()
        val cursor = MainActivity.dbHelper.readableDatabase.rawQuery("SELECT * FROM TimeRecord", null)
        if (cursor.moveToFirst()) {
            do {
                var item:String
                item = cursor.getString(cursor.getColumnIndex("id"))
                item+=". "
                item += cursor.getString(cursor.getColumnIndex("date"))
                item+="\t\t\t"
                item+=cursor.getString(cursor.getColumnIndex("length"))
                item+="\n"
                item+=cursor.getString(cursor.getColumnIndex("comment"))
                historyData.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,historyData)
        listView.adapter = adapter
        /*
         wordDataShow.clear()
            wordDataShow.addAll(wordData)
            listView.adapter = adapter
            wordcnisshow.fill(false)
            (activity as ActivityMemorize).changeFrg((activity as ActivityMemorize).frgMemorizeHome)
         */
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentClockHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentClockHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}