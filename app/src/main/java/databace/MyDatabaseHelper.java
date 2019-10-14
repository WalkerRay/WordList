package databace;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_NEWS = "create table News("
            + "id integer primary key autoincrement,"
            + "title text,"
            + "address text)";

    public static final String CREATE_ALLWORDS = "create table AllWords("
            + "word text primary key,"
            + "translation text not null,"
            + "example text,"
            + "exampleTran text)";

    public static final String CREATE_NOTREMEMBER = "create table NewWords("
            + "word text primary key,"
            + "translation text not null,"
            + "example text,"
            + "exampleTran text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS);
        db.execSQL(CREATE_ALLWORDS);
        db.execSQL(CREATE_NOTREMEMBER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists News");
        db.execSQL("drop table if exists Allwrods");
        db.execSQL("drop table if exists NewWords");
        onCreate(db);
    }

    public void AddNews(String title, String address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("adderss", address);
        db.insert("News", null, values);
        values.clear();
    }
}

