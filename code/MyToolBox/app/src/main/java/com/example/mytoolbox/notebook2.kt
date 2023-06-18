package com.example.mytoolbox

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class notebook2 : AppCompatActivity() {

    // 数据基本类型Note
    data class Note(val id: Long, val content: String ,val writeTime: String)
    // NoteDbHelper 类，用于创建和管理数据库
    class NoteDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
    {

        // 创建数据库表的 SQL 语句
        private companion object
        {
            private const val DATABASE_NAME = "notebook2.db"
            private const val DATABASE_VERSION = 1
            private const val CREATE_TABLE = "CREATE TABLE notes (" +
                    "id INTEGER PRIMARY KEY," +
                    "content TEXT," +
                    "writeTime TEXT"+  // 改为INTEGER类型，并设置默认值为当前时间戳
                    ");"
        }
        // 创建数据库表
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_TABLE)
        }
        // 处理数据库版本升级
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // 根据需要处理数据库升级...
        }
    }
    //在数据库中新增
    private fun addData(wt: String,noteText:String)
    {

        if(noteText!="null"&&noteText!=""&&wt!="null")
        {
            //转换格式
            // 把字符串转换为长整型
            val writeTime = wt

            // 获取可写的数据库实例
            //数据库变量
            val dbHelper = NoteDbHelper(this)
            val db = dbHelper.writableDatabase

            // 查询最大的id值

            val maxIdQuery = "SELECT MAX(id) FROM notes"
            val cursor = db.rawQuery(maxIdQuery, null)
            var maxId = -1L
            if (cursor.moveToFirst()) {
                maxId = cursor.getLong(0)
            }
            cursor.close()

            val values = ContentValues().apply {
                put("id", maxId + 1)
                put("content", noteText)
                put("writeTime", writeTime)
            }
            val newRow = db.insert("notes", null, values)
            // 关闭数据库
            db.close()

            // 检查是否成功插入数据
            if (newRow == -1L) {
                Toast.makeText(this, "日记写入失败", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "日记写入成功", Toast.LENGTH_SHORT).show()
            }

        }

    }
    //从数据库中删除
    private fun deleteData(writeTime: String)
    {
        val dbHelper = NoteDbHelper(this)
        val db = dbHelper.writableDatabase
        val tableName = "notes"
        val writeTimeColumnName = "writeTime"
        val selection = "$writeTimeColumnName = ?"
        val selectionArgs = arrayOf(writeTime.toString())
        db.delete(tableName, selection, selectionArgs)
    }
    //加载数据库
    private fun loadDatabase()
    {
        // 获取可写的数据库实例
        val dbHelper = NoteDbHelper(this)
        val db = dbHelper.writableDatabase
        // 查询 "notes" 表中的所有数据
        val cursor = db.query("notes", arrayOf("id",  "content", "writeTime"), null, null, null, null, null)
        //遍历结果集并将每一行放到一个列表中
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
            // 获取时间戳的字符串表示
            val writeTime= cursor.getString(cursor.getColumnIndexOrThrow("writeTime"))

            // 传给Note类的构造函数
            val note = Note(id, content, writeTime.toString())

            noteList.add(note)
        }
        // 关闭游标和数据库
        cursor.close()
        dbHelper.close()
    }



    //新增button界面
    private fun addNewButton1() {

        val intent = Intent(this, newNote::class.java);
        startActivity(intent) // 启动 Activity
        finish()
    }
    //对单个Button进行ui添加
    @SuppressLint("ResourceAsColor")
    private fun addNewButton2(note:Note) {

        // 创建Button
        val newButton= Button(this)

        // 将新newButton的背景色设置为白色
        val button=newButton
        button.isAllCaps=false
        //设置button监听器（对应的button）
        var content=this
        val dbHelper = NoteDbHelper(this)
        val db = dbHelper.writableDatabase
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            //选择删除
            fun se()
            {
                var parentView = button.parent
                deleteSe=true
                if(parentView is LinearLayout)
                {
                    val cView=parentView
                    val background = cView.getBackground()
                    if (background is ColorDrawable)
                    {
                        val color = background.getColor()
                        if (color == resources.getColor(R.color.white))
                        {
                            //加入待删除列表
                            val contentView=cView.getChildAt(0)
                            if(contentView is TextView)
                            {
                                contentList.add(contentView.text.toString())

                            }
                            val textview=cView.getChildAt(1)

                            if(textview is TextView)
                            {
                                timeList.add(textview.text.toString())

                            }
                            // 背景色是R.color.gray
                            cView.setBackgroundColor(resources.getColor(R.color.deep_gray))

                        } else
                        {
                            //从待删除列表取消
                            val textview=cView.getChildAt(1)
                            val contentView=cView.getChildAt(0)
                            if(contentView is TextView)
                            {
                                contentList.remove(contentView.text.toString())

                            }
                            if(textview is TextView)
                            {
                                timeList.remove(textview.text.toString())
                            }
                            // 背景色不是R.color.gray
                            cView.setBackgroundColor(resources.getColor(R.color.white))
                        }
                    }
                    val seNum=findViewById<TextView>(R.id.chooseNumber)
                    seNum.text="选择了"+timeList.size+"个"
                }
            }
            //单击
            override fun onSingleTapUp(e: MotionEvent): Boolean
            {
                var parentView = button.parent
                //跳转
                if(deleteSe==false)
                {
                    // 在这里添加单击事件的处理逻辑
                    val text = button.text.toString()
                    val intent = Intent(content, newNote::class.java)
                    intent.putExtra("modify", text)

                    //从数据库中删除
                    if(parentView is LinearLayout)
                    {
                        val childView = parentView.getChildAt(1)
                        if(childView is TextView)
                        {
                            val wT=childView.text
                            deleteData(wT.toString())
                        }
                    }


                    //跳转
                    startActivity(intent) // 启动 Activity
                    finish()

                }
                //选择
                else
                {
                    se()
                }
                return true
            }

            //长按
            override fun onLongPress(e: MotionEvent)
            {
                var parentView = button.parent

                if(deleteSe==false)
                {
                    // 在这里添加长按事件的处理逻辑
                    val last_l=findViewById<LinearLayout>(R.id.linearLayout2)
                    val last_5=findViewById<LinearLayout>(R.id.linearLayout5)
                    val new_l=findViewById<LinearLayout>(R.id.linearLayout4)
                    last_l.visibility=View.GONE
                    last_5.visibility=View.GONE
                    new_l.visibility=View.VISIBLE
                    //长按删除
                    deleteSe=true
                }
                //选择
                se()
                true
            }


        })
        buttonListener=View.OnTouchListener{ v, event ->
            gestureDetector.onTouchEvent(event)
        }
        newButton.setOnTouchListener(buttonListener)

        //最小最大
        newButton.minHeight=200
        newButton.maxHeight=800

        //size
        newButton.setTextSize(17.0F)
        newButton.text = note.content
        newButton.alpha=0.7f // 设置字体透明度
//            newButton.setTextColor(Color.GRAY)



        //创建textview
        val textView = TextView(this)
        textView.text=note.writeTime
        textView.gravity = Gravity.RIGHT

        //linearLayout
        val lL=LinearLayout(this)
        lL.orientation = LinearLayout.VERTICAL

        //布局
        // 设置新newButton=的布局参数，并将上左右间隔设置为30dp
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(15, 30, 15, 0)
        lL.layoutParams = layoutParams
        lL.setBackgroundColor(Color.WHITE)
        newButton.setBackgroundColor(Color.TRANSPARENT)
        textView.setBackgroundColor(Color.TRANSPARENT)
        lL.addView(newButton);
        lL.addView(textView);

        //加入到buttonList

        buttonList.add(newButton)

        //查看contain_left和container_right哪个的自域高度小
        val container_left = findViewById<LinearLayout>(R.id.container_left)
        val container_right = findViewById<LinearLayout>(R.id.container_right)
        var leftH=0
        var rightH=0
        container_left.measure(0, 0)
        leftH = container_left.measuredHeight
        container_right.measure(0, 0)
        rightH = container_right.measuredHeight
        var direction="left"
        if(leftH>rightH)direction="right"
        //这里要两边平均
        if(direction=="left")
        {
            container_left.addView(lL)
        }
        else
        {
            container_right.addView(lL)
        }

    }
    //从数据库中加载，并对全部添加
    private fun addNewButton3()
    {
        //加载数据库
        loadDatabase()
        // 遍历 noteList 列表并打印每个元素的属性值
        for (i in noteList.size - 1 downTo 0)
        {
            val note = noteList[i]

            addNewButton2(note)
        }
    }



    var noteList = mutableListOf<Note>()
    //是否处于删除选择状态
    var deleteSe=false
    var timeList = mutableListOf<String>()
    var contentList=mutableListOf<String>()
    //button的listener
    var buttonListener: View.OnTouchListener? = null



    //主程序
    @SuppressLint("ClickableViewAccessibility", "ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook2)
//        //数据库变量



        //收回noteText
        val noteText=intent.getStringExtra("noteText");
        val writeTime=intent.getStringExtra("writeTime");
        addData(writeTime.toString(),noteText.toString())


        //搞ui
        addNewButton3()


        // //下拉框
        var scrollView2= findViewById<ScrollView>(R.id.scrollView2)
        scrollView2.setOnTouchListener(object : View.OnTouchListener {
            private var lastTouchX = 0
            private var lastTouchY = 0
            private val topLayout = findViewById<ConstraintLayout>(R.id.topLayout)
            private val MaxY=60
            private var isTopLayoutVisible = true // 记录 topLayout 是否可见

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                // 获取触摸事件类型
                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        // 处理按下事件

                        lastTouchX = event.x.toInt()
                        lastTouchY = event.y.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // 处理移动事件
                        val deltaX = event.x.toInt() - lastTouchX
                        val deltaY = event.y.toInt() - lastTouchY
                        // 在此处实现滑动监听逻辑
                        if (deltaY > 0&&Math.abs(deltaY) >MaxY ) {//MaxY=0,有问题
                            // 如果向下滑动，则弹出
                            isTopLayoutVisible = true


                        }
                        else if (deltaY < 0&& Math.abs(deltaY) >MaxY) {

                            // 如果向上滑动，则回收
                            isTopLayoutVisible = false
                        }
                        lastTouchX = event.x.toInt()
                        lastTouchY = event.y.toInt()
                    }
                    MotionEvent.ACTION_UP -> {
                        // 处理抬起事件
                        if(isTopLayoutVisible)
                        {
                            if(topLayout.visibility != View.VISIBLE)
                            {
                                //topLayout.visibility = View.VISIBLE
                                // 创建值动画，将透明度从0变化到1，持续1秒钟
                                val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                                    duration = 1000 // 设置动画持续时间为1秒钟
                                    addUpdateListener { animation ->
                                        // 在动画过程中更新布局的透明度
                                        val alpha = animation.animatedValue as Float
                                        topLayout.alpha = alpha
                                    }
                                }

                                // 显示顶部布局，并启动值动画
                                topLayout.visibility = View.VISIBLE
                                animator.start()
                            }

                        }

                        else
                            topLayout.visibility = View.GONE

                    }
                }
                // 返回 true 表示已处理触摸事件(不会阻止scrollView滑动了)
                return false
            }
        })

        //待办清单、每日随记、倒数日按钮监听
        var undoplan= findViewById<Button>(R.id.undoplan)
        var diary= findViewById<Button>(R.id.diary)
        var countdownday= findViewById<Button>(R.id.countdownday)
        diary.setOnClickListener(){
            val intent = Intent(this, diary2::class.java);
            startActivity(intent) // 启动 Activity
            finish()

        }
        countdownday.setOnClickListener(){
            val intent = Intent(this, countdownday2::class.java);
            startActivity(intent) // 启动 Activity
            finish()
        }
        undoplan.setOnClickListener(){
            val intent = Intent(this, undoplan2::class.java);
            startActivity(intent) // 启动 Activity
            finish()
        }

        //back,合并，删除
        var back= findViewById<Button>(R.id.back)
        var mulSelect= findViewById<Button>(R.id.mulselect)
        var delete= findViewById<Button>(R.id.delete)
        //用于删除和合并
        fun buttonDelete()
        {
            // 用户点击确定按钮
            for(time in timeList) {
                val wT = time
                deleteData(wT)
            }
            timeList.clear()
            contentList.clear()
            noteList.clear()
            buttonList.clear()
            val container_left = findViewById<LinearLayout>(R.id.container_left)
            val container_right = findViewById<LinearLayout>(R.id.container_right)
            container_left.removeAllViews()
            container_right.removeAllViews()
            // 更新界面
            addNewButton3()
            back.performClick()
        }
        back.setOnClickListener()
        {
            //清空
            timeList.clear()
            contentList.clear()
            //切换
            val last_l=findViewById<LinearLayout>(R.id.linearLayout2)
            val new_l=findViewById<LinearLayout>(R.id.linearLayout4)
            last_l.visibility=View.VISIBLE
            new_l.visibility=View.GONE
            deleteSe=false
            //变白
            val container_left = findViewById<LinearLayout>(R.id.container_left)
            val container_right = findViewById<LinearLayout>(R.id.container_right)
            for(i in 0 until container_left.childCount)
            {
                val vc=container_left.getChildAt(i)
                vc.setBackgroundColor(Color.WHITE)
            }
            for(i in 0 until container_right.childCount)
            {
                val vc=container_right.getChildAt(i)
                vc.setBackgroundColor(Color.WHITE)
            }
        }
        delete.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
            builder.setMessage("\n删除所选的笔记？")
            builder.setPositiveButton("确定") { dialog, which ->
                buttonDelete()
            }
            builder.setNegativeButton("取消") { dialog, which ->
                // 用户点击取消按钮，什么也不做
            }
            val dialog = builder.create()
            dialog.show()
        }
        mulSelect.setOnClickListener {
            if(timeList.size==1)
            {
                Toast.makeText(this, "记事本：请选择要合并的笔记", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("提示")
                builder.setMessage("\n合并所选的笔记？")
                builder.setPositiveButton("确定") { dialog, which ->
                    // 在这里添加mulSelect按钮的点击事件的处理逻辑
                    val writeTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    var allContent=String()
                    for(content in contentList)
                    {
                        allContent=allContent+content+"\n\n"
                    }
                    addData(writeTime,allContent)
                    // 触发delete按钮的点击事件
                    buttonDelete()

                }
                builder.setNegativeButton("取消") { dialog, which ->
                    // 用户点击取消按钮，什么也不做

                }
                val dialog = builder.create()
                dialog.show()
            }
        }

        //search、back2 back3
        var search=findViewById<Button>(R.id.button2)
        var back2=findViewById<Button>(R.id.back2)
        var back3=findViewById<Button>(R.id.back3)
        search.setOnClickListener(){
            val last_l=findViewById<LinearLayout>(R.id.linearLayout2)
            val new_l=findViewById<LinearLayout>(R.id.linearLayout5)
            val search2=findViewById<Button>(R.id.search2)
            last_l.visibility=View.GONE
            new_l.visibility=View.VISIBLE
            search2.visibility=View.GONE

            val context=this
            //给搜索框设置监听器
            val editText = findViewById<EditText>(R.id.textEdit)
            editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // 在这里执行用户完成编辑操作后的代
                        var text = editText.text
                        // 获取可写的数据库实例
                        val dbHelper = NoteDbHelper(context)
                        val db = dbHelper.writableDatabase
                        val cursor = db.query(
                            "notes",
                            arrayOf("id", "content", "writeTime"),
                            "content LIKE ? OR writeTime LIKE ?",
                            arrayOf("%${text.toString()}%", "%${text.toString()}%"),
                            null,
                            null,
                            null
                        )
                        if (cursor != null) {
                            buttonList.clear()
                            val container_left = findViewById<LinearLayout>(R.id.container_left)
                            val container_right = findViewById<LinearLayout>(R.id.container_right)
                            container_left.removeAllViews()
                            container_right.removeAllViews()
                            while (cursor.moveToNext()) {
                                // 在这里处理每一行的数据
                                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                                val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                                val writeTime = cursor.getString(cursor.getColumnIndexOrThrow("writeTime"))
                                // 传给Note类的构造函数
                                val note = Note(id, content, writeTime.toString())
                                addNewButton2(note)
                            }
                            cursor.close()
                        }
                        db.close()
                        return true
                    }
                    return false
                }
            })

        }
        back2.setOnClickListener(){
            val last_l=findViewById<LinearLayout>(R.id.linearLayout2)
            val new_l=findViewById<LinearLayout>(R.id.linearLayout5)
            val search2=findViewById<Button>(R.id.search2)
            search2.visibility=View.VISIBLE
            last_l.visibility=View.VISIBLE
            new_l.visibility=View.GONE
            addNewButton3()
            // 获取当前页面的View对象
            val view = currentFocus
            // 如果View不为空，表示软键盘打开
            if (view != null) {
                // 获取InputMethodManager实例
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // 收起软键盘
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            timeList.clear()
            contentList.clear()
            noteList.clear()
            buttonList.clear()
            val container_left = findViewById<LinearLayout>(R.id.container_left)
            val container_right = findViewById<LinearLayout>(R.id.container_right)
            container_left.removeAllViews()
            container_right.removeAllViews()
            addNewButton3()


        }
        back3.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent) // 启动 Activity
            finish()
        }
        //新增按钮
        var button_newnote=findViewById<Button>(R.id.newnote);
        button_newnote.setOnClickListener{
            addNewButton1()
        }
    }
}
