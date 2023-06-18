package com.example.mytoolbox
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import java.util.WeakHashMap


val buttonList = mutableListOf<Button>()
class undoplan2 : AppCompatActivity() {


    val buttonList = mutableListOf<Button>()
    var conList=mutableListOf<View>()
    var lastIt: View? = null
    var lastButtonText:String? = ""

    // 数据基本类型Note
    data class Note(val id: Long, val content: String ,val Isdel: String)
    var noteList = mutableListOf<undoplan2.Note>()

    class NoteDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
    {

        // 创建数据库表的 SQL 语句
        private companion object
        {
            private const val DATABASE_NAME = "make.db"
            private const val DATABASE_VERSION = 1
            private const val CREATE_TABLE = "CREATE TABLE undoPlan (" +
                    "id INTEGER PRIMARY KEY," +
                    "content TEXT," +
                    "Isdel TEXT"+
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
    private fun addData(Isdel: String,noteText:String)
    {

        if(noteText!="null"&&noteText!=""&&Isdel!="null")
        {
            // 获取可写的数据库实例
            //数据库变量
            val dbHelper = NoteDbHelper(this)
            val db = dbHelper.writableDatabase
//            var maxId = 0


            //查询最大的id值

            val maxIdQuery = "SELECT MAX(id) FROM undoPlan"
            val cursor = db.rawQuery(maxIdQuery, null)
            var maxId = -1L
            if (cursor.moveToFirst()) {
                maxId = cursor.getLong(0)
            }

            cursor.close()

            val values = ContentValues().apply {
                put("id", maxId+1)
                put("content", noteText )
                put("Isdel", Isdel)
            }
            val newRow = db.insert("undoPlan", null, values)
            // 关闭数据库
            db.close()

//            // 检查是否成功插入数据
//            if (newRow == -1L) {
//                Toast.makeText(this, "日记写入失败", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "日记写入成功", Toast.LENGTH_SHORT).show()
//            }

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

            addTwoButton(note.content.toString(),note.Isdel)
        }
    }
    //加载数据库
    private fun loadDatabase()
    {
        // 获取可写的数据库实例
        val dbHelper = NoteDbHelper(this)
        val db = dbHelper.writableDatabase
        // 查询 "notes" 表中的所有数据
        val cursor = db.query("undoPlan", arrayOf("id",  "content", "Isdel"), null, null, null, null, null)
        //遍历结果集并将每一行放到一个列表中
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
            // 获取时间戳的字符串表示
            val writeTime= cursor.getString(cursor.getColumnIndexOrThrow("Isdel"))
            // 传给Note类的构造函数
            val note = undoplan2.Note(id, content, writeTime.toString())

            noteList.add(note)
        }
        // 关闭游标和数据库
        cursor.close()
        db.close()
        dbHelper.close()
    }
    //上传数据
    private fun unLoad()
    {
        // 获取可写的数据库实例
        val dbHelper = NoteDbHelper(this)
        val db = dbHelper.writableDatabase
        db.execSQL("delete from " + "undoPlan")
        db.close()


        //上传
        for(view in conList)
        {
            var textButton = view.findViewById<Button>(R.id.text)
            var content=textButton.text
            var isD="false"
            if (textButton.alpha==0.5f)isD="true"
            addData(isD.toString(),content.toString())

        }
    }



    //增加一个twoButton
    private fun addTwoButton(note:String,isDel:String)
    {

        // 在此处处理新笔记的逻辑
        var linearLayout=findViewById<LinearLayout>(R.id.container)
        var twoButtonView = LayoutInflater.from(this).inflate(R.layout.two_button, linearLayout, false)
        var textButton = twoButtonView.findViewById<Button>(R.id.text)
        textButton.text = note.toString()
        linearLayout.addView(twoButtonView,0);
        conList.add(0,twoButtonView)


        //为新创建的 TwoButtonView 设置点击监听器
        val leftButton = twoButtonView.findViewById<Button>(R.id.tick)
        val rightButton = twoButtonView.findViewById<Button>(R.id.text)
        //左Button
        leftButton.setOnClickListener {

            fun cartoon(v:View,startY:Float,endY:Float)
            {
                //创建动画对象
                val anim = ObjectAnimator.ofFloat(v, "Y", startY, endY)
                // 设置动画持续时间
                anim.duration = 500
                // 禁止其他触摸事件
                anim.interpolator = LinearInterpolator()
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        v.isClickable = false
                    }
                    override fun onAnimationEnd(animation: Animator) {
                        v.isClickable = true
                    }
                })
                // 启动动画
                anim.start()
            }

            fun cartoon2(DownList:MutableList<View>)
            {
                if(DownList.size!=0)
                {
                    //更新conList
                    val firstIndex=conList.indexOf(DownList[0])
                    conList.remove(twoButtonView)
                    conList.add(firstIndex,twoButtonView)
                    //头
                    val firstView=DownList[0]
                    //相当于交换
                    if(DownList.size==1)
                    {
                        cartoon(firstView,firstView.y,twoButtonView.y)

                    }
                    if(DownList.size>1)
                    {
                        val secondView=DownList[1]
                        var deY=firstView.y-secondView.y
                        for (l in DownList)
                        {
                            cartoon(l,l.y,l.y-deY)
                        }
                    }
                    cartoon(twoButtonView,twoButtonView.y,firstView.y)
                }
            }

            // 删除，向上
            if(leftButton.alpha==1f)
            {
                //获取UpList
                var UpList=mutableListOf<View>()
                for (i in conList.size - 1 downTo 0)
                {
                    val l = conList[i]
                    if (l == twoButtonView) break
                    val lB = l.findViewById<Button>(R.id.tick)
                    if (lB.alpha == 1f) UpList.add(l)
                }

                //背景
                val drawable = ContextCompat.getDrawable(this, R.drawable.circletick)
                leftButton.background = drawable
                //左透明度
                leftButton.alpha=0.5f
                //右透明度
                rightButton.alpha=0.5f
                //删除线
                rightButton.setPaintFlags(leftButton.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                //动画
                cartoon2(UpList)

            }
            // 取消删除，//下移的列表:在leftButton之上,alpha=0.5f
            else
            {

                //获取DownList
                var DownList=mutableListOf<View>()
                for (l in conList)
                {
                    if(l==twoButtonView)break
                    val lB=l.findViewById<Button>(R.id.tick)
                    if(lB.alpha==0.5f) DownList.add(l)
                }

                //变暗淡
                val drawable = ContextCompat.getDrawable(this, R.drawable.circle)
                leftButton.background = drawable
                leftButton.alpha=1f
                rightButton.alpha=1f
                rightButton.setPaintFlags(rightButton.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
                //动画
                cartoon2(DownList)
            }

        }
        //右Button
        rightButton.setOnClickListener {
            // 右边的按钮被点击时的逻辑
            lastIt=it
            lastButtonText=rightButton.text.toString()
            val newnote = findViewById<Button>(R.id.newnote)
            newnote.performClick()
        }
        buttonList.add(leftButton)
        buttonList.add(rightButton)
        if (isDel=="true")
        {
            leftButton.performClick()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_undoplan2)

        addNewButton3()
        //返回按钮
        var back2= findViewById<Button>(R.id.back2)
        back2.setOnClickListener()
        {
            val intent = Intent(this, notebook2::class.java);
            unLoad()
            startActivity(intent) // 启动 Activity
            finish()
        }
        //新增
        val context=this
        val newnote = findViewById<Button>(R.id.newnote)
        newnote.setOnClickListener {

            // 将按钮设置为不可见
            newnote.visibility = View.GONE
            //加载输入框资源
            val popupView = layoutInflater.inflate(R.layout.resources, null)
            val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            //自动获取焦点
            val editEdit= popupView.findViewById<EditText>(R.id.edit_text2)
            if(lastIt!=null)
            {
                editEdit.setText(lastButtonText)
                lastButtonText=""
            }
            editEdit.setFocusable(true);
            editEdit.setFocusableInTouchMode(true);
            editEdit.requestFocus();
            //背景虚化
            var con= findViewById<ScrollView>(R.id.scrollView2)
            var color = ContextCompat.getColor(context, R.color.deep_gray)
            var drawable = ColorDrawable(color)
            con.background = drawable
            //输入框背景被色
            color=ContextCompat.getColor(context, R.color.white)
            drawable = ColorDrawable(color)
            popupView.background=drawable
            // 设置输入法模式
            popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            // 防止遮挡
            popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            //设置锚点，并展示
            val anchorView: View = findViewById(R.id.frameLayout)
            popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0)
            // 监听弹出窗口的dismiss事件，使得按钮弹出
            popupWindow.setOnDismissListener {
                newnote.visibility = View.VISIBLE
                val color = ContextCompat.getColor(context, R.color.gray)
                val drawable = ColorDrawable(color)
                con.background = drawable
            }

            val buttonFly = popupView.findViewById<Button>(R.id.confirm_button2)
//                //按按钮输出内容
            buttonFly.setOnClickListener {

                val note = editEdit.text.toString()
                //加入内容
                if(lastIt==null)
                {
                    addTwoButton(note,"false")
                }
                else
                {
                    (lastIt as? Button)?.text=note
                    lastIt=null
                }

                newnote.visibility=View.VISIBLE
                popupWindow.dismiss()
                val color = ContextCompat.getColor(context, R.color.gray)
                val drawable = ColorDrawable(color)
                con.background = drawable
            }

        }

        //清空
        val cleanB=findViewById<Button>(R.id.cleanB)
        cleanB.setOnClickListener()
        {
            var linearLayout=findViewById<LinearLayout>(R.id.container)
            linearLayout.removeAllViews()
            conList.clear()
        }




    }

}