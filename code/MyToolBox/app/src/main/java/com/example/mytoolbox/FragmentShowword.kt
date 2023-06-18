package com.example.mytoolbox

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentShowword.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentShowword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var listView: ListView
    var wordDataCn = mutableListOf<String>()
    var wordData = mutableListOf<String>()
    var wordDataShow =  mutableListOf<String>()
    var wordcnisshow = mutableListOf<Boolean>()
    var wordStatus = mutableListOf<String>()

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
        val view =  inflater.inflate(R.layout.fragment_showword, container, false)
        //退出时切换回上一个fragment
        val llback = view.findViewById<LinearLayout>(R.id.ll_back)
        listView = view.findViewById(R.id.lv_words)
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,wordDataShow)
        initWords {
            listView.adapter = adapter
        }

        llback.setOnClickListener {
            (activity as ActivityMemorize).changeFrg((activity as ActivityMemorize).frgMemorizeHome)
        }

        listView.setOnItemClickListener { adapterView, view, position, l ->
            if(wordcnisshow[position]==false) {
                wordcnisshow[position]=true
                wordDataShow[position] = wordData[position] + "\t\t\t" + wordDataCn[position] + "\n" + wordStatus[position]
            }else{
                wordcnisshow[position]=false
                wordDataShow[position] = wordData[position] + "\n" + wordStatus[position]
            }
            listView.adapter = adapter
        }

        listView.setOnItemLongClickListener { adapterView, view, position, l ->
            //val popupMenu = PopupMenu(requireContext(), listView.get(position))
            //val popupMenu = PopupMenu(requireContext(), listView.getChildAt(position))
            // 创建PopupWindow对象
            var popupMenu:PopupMenu?=null
            if(position<=18)
                popupMenu = PopupMenu(requireContext(),listView.get(position))
            else
                popupMenu = PopupMenu(requireContext(),listView.get(9))
            // 添加选项列表项
            popupMenu!!.menu.add("记得")
            popupMenu!!.menu.add("遗忘")
            popupMenu!!.menu.add("熟悉")
            //popupMenu.menu.add("删除")
            // 设置选项列表项的点击事件
            popupMenu.setOnMenuItemClickListener { menuItem ->
                // 在这里处理选项列表项的点击事件
                var getStatus=""
                when (menuItem.title) {
                    "记得" -> {
                        getStatus = "记得"
                    }
                    "遗忘" -> {
                        getStatus = "遗忘"
                    }
                    "熟悉" -> {
                        getStatus = "熟悉"
                    }
                }
                val values = ContentValues()
                values.put("status",getStatus)
                val selection = "word = ?"
                val selectionArgs = arrayOf(wordData[position])
                MainActivity.dbHelper.writableDatabase.update("Words",values,selection,selectionArgs)
                wordStatus[position] = getStatus
                if(wordcnisshow[position]==true) {
                    wordDataShow[position] = wordData[position] + "\t\t\t" + wordDataCn[position] + "\n" + wordStatus[position]
                }else{
                    wordDataShow[position] = wordData[position] + "\n" + wordStatus[position]
                }
                listView.adapter = adapter
                return@setOnMenuItemClickListener true
            }
            // 显示选项列表
            popupMenu.show()

            return@setOnItemLongClickListener true
        }


        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    @SuppressLint("Range")
    private fun initWords(callback: () -> Unit){
        wordData.clear()
        wordDataShow.clear()
        wordDataCn.clear()
        wordcnisshow.clear()
        val cursor = MainActivity.dbHelper.readableDatabase.rawQuery("SELECT * FROM Words", null)
        if (cursor.moveToFirst()) {
            do {
                val bookName = cursor.getString(cursor.getColumnIndex("book"))
                if(bookName == (activity as ActivityMemorize).whichBook){
                    wordData.add(cursor.getString(cursor.getColumnIndex("word")))
                    wordDataCn.add(cursor.getString(cursor.getColumnIndex("mean")))
                    wordStatus.add(cursor.getString(cursor.getColumnIndex("status")))
                    wordcnisshow.add(false)
                    wordDataShow.add(cursor.getString(cursor.getColumnIndex("word"))+"\n"+cursor.getString(cursor.getColumnIndex("status")))
                }
            } while (cursor.moveToNext())

        }
        cursor.close()
        callback()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentShowword.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentShowword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
/*
apple 苹果
banana 香蕉
carrot 胡萝卜
dog 狗
elephant 大象
fish 鱼
guitar 吉他
hamburger 汉堡包
ice-cream 冰淇淋
jacket 外套
kangaroo 袋鼠
lemon 柠檬
monkey 猴子
notebook 笔记本
orange 橙子
piano 钢琴
queen 女王
rose 玫瑰
sunglasses 太阳镜
turtle 海龟
 */