package com.example.mytoolbox

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentFunManager.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentFunManager : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fun_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val switchExample1 = view.findViewById<Switch>(R.id.switch_example1)
        val switchExample2 = view.findViewById<Switch>(R.id.switch_example2)
        val switchExample3 = view.findViewById<Switch>(R.id.switch_example3)
        switchExample1.isChecked = if((activity as MainActivity).fun_visibility[0]==1)  true else false
        switchExample2.isChecked = if((activity as MainActivity).fun_visibility[1]==1)  true else false
        switchExample3.isChecked = if((activity as MainActivity).fun_visibility[2]==1)  true else false
        switchExample1.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            {
                (activity as MainActivity).fun_visibility[0] = 1

            }else{
                (activity as MainActivity).fun_visibility[0] = 0
            }
            val values = ContentValues()
            values.put("status",(activity as MainActivity).fun_visibility[0])
            MainActivity.dbHelper.writableDatabase.update("FunStatus",values,"funName = ?",
                arrayOf("fun1")
            )
        }
        switchExample2.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                (activity as MainActivity).fun_visibility[1] = 1
            else
                (activity as MainActivity).fun_visibility[1] = 0
            val values = ContentValues()
            values.put("status",(activity as MainActivity).fun_visibility[1])
            MainActivity.dbHelper.writableDatabase.update("FunStatus",values,"funName = ?",
                arrayOf("fun2")
            )

        }
        switchExample3.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                (activity as MainActivity).fun_visibility[2] = 1
            else
                (activity as MainActivity).fun_visibility[2] = 0
            val values = ContentValues()
            values.put("status",(activity as MainActivity).fun_visibility[2])
            MainActivity.dbHelper.writableDatabase.update("FunStatus",values,"funName = ?",
                arrayOf("fun3")
            )
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentFunManager.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentFunManager().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}