package com.example.mytoolbox

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMemorizeHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentMemorizeHome : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var booksData = mutableListOf<String>()
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
        val view =  inflater.inflate(R.layout.fragment_memorize_home, container, false)
        // 获取Button控件的实例
        val iv_newbook = view.findViewById<ImageView>(R.id.ivNewBook)
        val listView = view.findViewById<ListView>(R.id.lv_books)
        //初始化 单词书列表
        initBooks(){
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,booksData)
            listView.adapter = adapter
        }
        // 设置Button的点击事件
        //添加新单词书
        iv_newbook.setOnClickListener {
            // 创建AlertDialog.Builder对象
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("新建单词书")
            val input = EditText(requireContext())
            builder.setView(input)
            builder
                .setPositiveButton("确定") { dialog, which ->
                    // 点击确定后执行的操作
                    // 查询书名是否已经存在
                    var BookExists:Boolean = false
                    val cursor = MainActivity.dbHelper.readableDatabase.rawQuery("SELECT * FROM Books", null)
                    if (cursor.moveToFirst()) {
                        do {
                            val item = cursor.getString(cursor.getColumnIndex("name"))
                            if(input.text.toString()==item){
                                BookExists = true
                                break
                            }
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                    if(BookExists == false) {
                        ////获得当前表格项目数
                        val cursor2 = MainActivity.dbHelper.readableDatabase.rawQuery(
                            "SELECT COUNT(*) FROM Books",
                            null
                        )
                        var count = 0
                        if (cursor2.moveToFirst()) {
                            count = cursor2.getInt(0)
                        }
                        cursor2.close()
                        ////插入数据库 新书
                        val values = ContentValues()
                        values.put("id", count + 1)
                        values.put("name", input.text.toString())
                        MainActivity.dbHelper.writableDatabase.insert("Books", null, values)
                        initBooks(){
                            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,booksData)
                            listView.adapter = adapter
                        }
                    }else{
                        Toast.makeText(requireContext(), "Books Exist Already!", Toast.LENGTH_SHORT).show()
                        builder.create().dismiss()
                    }
                }
                .setNegativeButton("取消") { dialog, which ->
                    // 点击取消后执行的操作
                }
            builder.show()
        }
        listView.setOnItemClickListener { adapterView, view, position, l ->
            (activity as ActivityMemorize).whichBook = adapterView.adapter.getItem(position).toString()
            (activity as ActivityMemorize).changeFrg((activity as ActivityMemorize).frgShowword)
        }
        listView.setOnItemLongClickListener { adapterView, view, i, l ->
            // 创建PopupMenu对象并设置弹出位置
            val popupMenu = PopupMenu(requireContext(), view)
            // 添加选项列表项
            popupMenu.menu.add("删除单词本")
            popupMenu.menu.add("导入单词")
            popupMenu.menu.add("取消")
            // 设置选项列表项的点击事件
            popupMenu.setOnMenuItemClickListener { menuItem ->
                // 在这里处理选项列表项的点击事件
                when (menuItem.title) {
                    "删除单词本" -> {
                        // 在这里处理删除单词本的操作
                        val whereArgs = arrayOf(adapterView.adapter.getItem(i).toString())
                        MainActivity.dbHelper.writableDatabase.delete("Books","name = ?",whereArgs)
                        MainActivity.dbHelper.writableDatabase.delete("Words","book = ?",whereArgs)
                        initBooks(){
                            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,booksData)
                            listView.adapter = adapter
                        }
                        true
                    }
                    "导入单词" -> {
                        // 在这里处理导入单词的操作
                        // 创建AlertDialog.Builder对象
                        val builder = AlertDialog.Builder(requireContext())
                        // 创建EditText组件
                        val editText = EditText(requireContext())
                        editText.hint = "输入文本"
                        // 将EditText组件添加到弹窗中
                        builder.setView(editText)
                        // 设置弹窗按钮
                        builder.setPositiveButton("保存") { dialog, which ->
                            // 在这里处理保存逻辑，获取EditText中的文本
                            val text = editText.text.toString()
                            // 将文本按照换行符进行分行
                            val lines = text.split("\n")
                            // 遍历分行后的文本
                            for (line in lines) {
                                val words = line.split(" ")
                                // 遍历分词后的单词
                                if(words.size==2){
                                    val values = ContentValues()
                                    values.put("word", words[0])
                                    values.put("mean",words[1])
                                    values.put("status","遗忘")
                                    values.put("book",adapterView.adapter.getItem(i).toString())
                                    MainActivity.dbHelper.writableDatabase.insert("Words", null, values)
                                }
                            }

                        }
                        builder.setNegativeButton("取消") { dialog, which ->
                            // 在这里处理取消逻辑
                        }
                        // 显示弹窗
                        builder.show()
                        true
                    }
                    else -> return@setOnMenuItemClickListener true
                }
            }
            // 显示选项列表
            popupMenu.show()
            return@setOnItemLongClickListener true
        }
//        bt_book1.setOnClickListener {
//            (activity as ActivityMemorize).changeFrg((activity as ActivityMemorize).frgShowword)
//        }
//        bt_book1.setOnLongClickListener {
//            // 创建PopupMenu对象并设置弹出位置
//            val popupMenu = PopupMenu(requireContext(), bt_book1)
//            // 添加选项列表项
//            popupMenu.menu.add("删除单词本")
//            popupMenu.menu.add("导入单词")
//            popupMenu.menu.add("取消")
//            // 设置选项列表项的点击事件
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                // 在这里处理选项列表项的点击事件
//                return@setOnMenuItemClickListener true
//            }
//            // 显示选项列表
//            popupMenu.show()
//            return@setOnLongClickListener true
//        }
        return view
    }
    @SuppressLint("Range")
    private fun initBooks(callback: () -> Unit){
        booksData.clear()
        val cursor = MainActivity.dbHelper.readableDatabase.rawQuery("SELECT * FROM Books", null)
        if (cursor.moveToFirst()) {
            do {
                val item = cursor.getString(cursor.getColumnIndex("name"))
                booksData.add(item)
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
         * @return A new instance of fragment FragmentMemorizeHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentMemorizeHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}