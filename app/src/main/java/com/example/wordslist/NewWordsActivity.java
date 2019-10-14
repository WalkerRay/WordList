package com.example.wordslist;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.wordlist.R;

import java.util.ArrayList;
import java.util.List;

import databace.MyDatabaseHelper;
import model.Words;

public class NewWordsActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private ListView listView;
    private int listsize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_words);
        List<Words> newWords = new ArrayList<Words>();

        dbHelper = new MyDatabaseHelper(this, "WordList.db", null, 2);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        //查询NewWords全部数据
        Cursor cursor = db.query("NewWords", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            System.out.println("start");
            do{
                Words notRemember = new Words();
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String translation = cursor.getString(cursor.getColumnIndex("translation"));
                String example = cursor.getString(cursor.getColumnIndex("example"));
                String exampleTran = cursor.getString(cursor.getColumnIndex("exampleTran"));
                notRemember.setWord(word);
                notRemember.setTranslation(translation);
                if(example != null){
                    notRemember.setExample(example);
                }
                if(exampleTran != null){
                    notRemember.setExampleTran(exampleTran);
                }
                newWords.add(notRemember);

            }while(cursor.moveToNext());
        }
        cursor.close();
        String[] expRe = new String[newWords.size()];
        listsize = newWords.size();
        for(int i = 0; i < newWords.size(); i ++){
            expRe[i] = newWords.get(i).getWord()+":"+newWords.get(i).getTranslation();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewWordsActivity.this, android.R.layout.simple_list_item_1, expRe);
        listView = (ListView)findViewById(R.id.newwords);
        listView.setAdapter(adapter);

        //设置listView的item的点击事件，通过AlertDialog显示单词全部信息和编辑选项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewWordsActivity.this);
                System.out.println(position);

                        String text = listView.getItemAtPosition(position).toString();
                        final String[] Text = text.split(":");
                        Cursor all = db.rawQuery("select * from NewWords where word = ?", new String[] {Text[0]});
                        all.moveToFirst();
                        final String example = all.getString(all.getColumnIndex("example"));
                        final String exampleTran = all.getString(all.getColumnIndex("exampleTran"));
                        builder.setTitle(Text[0]);
                        builder.setMessage("释义："+Text[1]+"\n例句："+example+"\n翻译："+exampleTran);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                            }
                        });
                        builder.setNeutralButton("编辑", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
//                                Toast.makeText(AllWordsActivity.this, "编辑",
//                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(NewWordsActivity.this, EditNewActivity.class);
                                intent.putExtra("word", Text[0]);
                                intent.putExtra("translation", Text[1]);
                                intent.putExtra("example", example);
                                intent.putExtra("exampleTran", exampleTran);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("移出生词表", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                db.execSQL("delete from NewWords where word=?", new String[]{Text[0]});
                                Toast.makeText(NewWordsActivity.this, "移出成功",
                                        Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(NewWordsActivity.this, NewWordsActivity.class);
                                startActivity(intent);
                                finish();//关闭自己

                            }
                        });
                        builder.show();
                }
        });
    }
}
