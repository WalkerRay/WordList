package com.example.wordslist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import model.Words;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wordlist.R;

import java.util.ArrayList;
import java.util.List;

import databace.MyDatabaseHelper;

public class AllWordsActivity extends AppCompatActivity{

    private MyDatabaseHelper dbHelper;
    private int listsize = 0;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_words);
        List<Words> allWords = new ArrayList<Words>();

        dbHelper = new MyDatabaseHelper(this, "WordList.db", null, 2);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        //查询AllWords全部数据
        Cursor cursor = db.query("AllWords", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            System.out.println("start");
            do{
                Words allwords = new Words();
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String translation = cursor.getString(cursor.getColumnIndex("translation"));
                String example = cursor.getString(cursor.getColumnIndex("example"));
                String exampleTran = cursor.getString(cursor.getColumnIndex("exampleTran"));
                allwords.setWord(word);
                allwords.setTranslation(translation);
                if(example != null){
                    allwords.setExample(example);
                }
                if(exampleTran != null){
                    allwords.setExampleTran(exampleTran);
                }
                allWords.add(allwords);

            }while(cursor.moveToNext());
        }
        cursor.close();
        String[] expRe = new String[allWords.size()];
        listsize = allWords.size();
        for(int i = 0; i < allWords.size(); i ++){
            expRe[i] = allWords.get(i).getWord()+":"+allWords.get(i).getTranslation();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllWordsActivity.this, android.R.layout.simple_list_item_1, expRe);
        listView = (ListView)findViewById(R.id.allwords);
        listView.setAdapter(adapter);

        //设置listView的item的点击事件，通过AlertDialog显示单词全部信息和编辑选项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllWordsActivity.this);
                System.out.println(position);
                        String text = listView.getItemAtPosition(position).toString();
                        final String[] Text = text.split(":");
                        Cursor all = db.rawQuery("select * from AllWords where word = ?", new String[] {Text[0]});
                        all.moveToFirst();
                        final String example = all.getString(all.getColumnIndex("example"));
                        final String exampleTran = all.getString(all.getColumnIndex("exampleTran"));
                        builder.setTitle(Text[0]);
                        builder.setMessage("释义："+Text[1]+"\n例句："+example+"\n翻译："+exampleTran);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(AllWordsActivity.this, "确定",
//                                        Toast.LENGTH_LONG).show();

                            }
                        });
                        builder.setNeutralButton("编辑", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(AllWordsActivity.this, "编辑",
//                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AllWordsActivity.this, EditAllActivity.class);
                                intent.putExtra("word", Text[0]);
                                intent.putExtra("translation", Text[1]);
                                intent.putExtra("example", example);
                                intent.putExtra("exampleTran", exampleTran);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("添加至生词表", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(AllWordsActivity.this, "添加至生词表",
//                                        Toast.LENGTH_LONG).show();
                                String[] selectionArgs = {Text[0]};
                                Cursor cursorNew = db.query("NewWords", null, "word=?", selectionArgs, null, null, null, null);
                                if(!cursorNew.moveToFirst()) {
                                    ContentValues value = new ContentValues();
                                    value.put("word", Text[0]);
                                    value.put("translation", Text[1]);
                                    value.put("example", example);
                                    value.put("exampleTran", exampleTran);
                                    db.insert("NewWords", null, value);
                                    Toast.makeText(AllWordsActivity.this, "添加成功",
                                            Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(AllWordsActivity.this, "生词表中已存在该单词",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.show();

            }
        });
    }

}
