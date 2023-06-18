import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
//import android.widget.Toast
//import androidx.annotation.Nullable;

class MyDatabaseHelper(val context: Context, name:String, version:Int):
SQLiteOpenHelper(context,name,null,version){
    //程序状态表: 存储小程序启用状态
    private val createFunStatus = "create Table FunStatus ("+
            "funName text primary key,"+
            "status integer)"
    //单词书: 存储已有的单词书
    private val createBooks = "create Table Books ("+
            "id integer primary key,"+
            "name text)"
    //单词表: 存储所有单词书的所有表
    private val createWords = "create Table Words ("+
            "word text primary key,"+
            "mean text,"+
            "status text,"+
            "book text)"
    //计时记录表: 存储番茄时钟历史记录
    private val createTimeRecord = "create Table TimeRecord ("+
            "id integer primary key,"+
            "date text,"+
            "length text,"+
            "comment text)"
    override fun onCreate(db:SQLiteDatabase) {
        db.execSQL(createFunStatus)
        db.execSQL(createTimeRecord)
        db.execSQL(createWords)
        db.execSQL(createBooks)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop Table if exists FunStatus")
        db.execSQL("drop Table if exists Words")
        db.execSQL("drop Table if exists TimeRecord")
        db.execSQL("drop Table if exists Books")
        onCreate(db)
    }
}