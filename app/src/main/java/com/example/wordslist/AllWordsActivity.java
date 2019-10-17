package com.example.wordslist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import model.Words;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.wordlist.R;

import java.util.ArrayList;
import java.util.List;

import databace.MyDatabaseHelper;

public class AllWordsActivity extends AppCompatActivity{

    private MyDatabaseHelper dbHelper;
    private int listsize = 0;
    private ListView listView;

    private static TextView word;
    private static TextView translation;
    private static TextView example;
    private static TextView exampleTran;
    private Button edit;
    private Button move;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_words);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        dbHelper = new MyDatabaseHelper(this, "WordList.db", null, 2);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            List<Words> allWords = new ArrayList<Words>();

            //查询AllWords全部数据
            Cursor cursor = db.query("AllWords", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                System.out.println("start");
                do {
                    Words allwords = new Words();
                    String word = cursor.getString(cursor.getColumnIndex("word"));
                    String translation = cursor.getString(cursor.getColumnIndex("translation"));
                    String example = cursor.getString(cursor.getColumnIndex("example"));
                    String exampleTran = cursor.getString(cursor.getColumnIndex("exampleTran"));
                    allwords.setWord(word);
                    allwords.setTranslation(translation);
                    if (example != null) {
                        allwords.setExample(example);
                    }
                    if (exampleTran != null) {
                        allwords.setExampleTran(exampleTran);
                    }
                    allWords.add(allwords);

                } while (cursor.moveToNext());
            }
            cursor.close();
            String[] expRe = new String[allWords.size()];
            listsize = allWords.size();
            for (int i = 0; i < allWords.size(); i++) {
                expRe[i] = allWords.get(i).getWord() + ":" + allWords.get(i).getTranslation();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllWordsActivity.this, android.R.layout.simple_list_item_1, expRe);
            listView = (ListView) findViewById(R.id.allwords);
            listView.setAdapter(adapter);

            //设置listView的item的点击事件，通过AlertDialog显示单词全部信息和编辑选项
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AllWordsActivity.this);
                    System.out.println(position);
                    String text = listView.getItemAtPosition(position).toString();
                    final String[] Text = text.split(":");
                    Cursor all = db.rawQuery("select * from AllWords where word = ?", new String[]{Text[0]});
                    all.moveToFirst();
                    final String example = all.getString(all.getColumnIndex("example"));
                    final String exampleTran = all.getString(all.getColumnIndex("exampleTran"));
                    builder.setTitle(Text[0]);
                    builder.setMessage("释义：" + Text[1] + "\n例句：" + example + "\n翻译：" + exampleTran);
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
                            if (!cursorNew.moveToFirst()) {
                                ContentValues value = new ContentValues();
                                value.put("word", Text[0]);
                                value.put("translation", Text[1]);
                                value.put("example", example);
                                value.put("exampleTran", exampleTran);
                                db.insert("NewWords", null, value);
                                Toast.makeText(AllWordsActivity.this, "添加成功",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AllWordsActivity.this, "生词表中已存在该单词",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.show();

                }
            });
        }else{
//            AllRightFragment rightFragment = new AllRightFragment();
//            AllLeftFragment leftFragment  = new AllLeftFragment();
//将上面的两个Fragment添加进来
//            getSupportFragmentManager().beginTransaction().replace(R.id.all_right, rightFragment, "rightFragment").commit();
//            getSupportFragmentManager().beginTransaction().replace(R.id.all_left, leftFragment, "leftFragment").commit();
//
//            TextView test = findViewById(R.id.rWord);
//            test.setText("lalala");
            word = findViewById(R.id.allrWord);
            translation = findViewById(R.id.allrTranslation);
            example = findViewById(R.id.allrExample);
            exampleTran = findViewById(R.id.allrExampleTran);
            edit = findViewById(R.id.allrEdit);
            edit.setOnClickListener(new Edit());
            move = findViewById(R.id.allrMove);
            move.setOnClickListener(new Move());
        }
    }

    class Edit implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(!word.getText().toString().isEmpty()) {
                Intent intent = new Intent(AllWordsActivity.this, EditAllActivity.class);
                intent.putExtra("word", word.getText().toString());
                intent.putExtra("translation", translation.getText().toString());
                intent.putExtra("example", example.getText().toString());
                intent.putExtra("exampleTran", exampleTran.getText().toString());
                startActivity(intent);
            }else{
                Toast.makeText(AllWordsActivity.this, "请选择要编辑的单词",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class Move implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            String[] selectionArgs = {word.getText().toString()};
            if(!word.getText().toString().isEmpty()) {
                Cursor cursorNew = db.query("NewWords", null, "word=?", selectionArgs, null, null, null, null);
                if (!cursorNew.moveToFirst()) {
                    ContentValues value = new ContentValues();
                    value.put("word", word.getText().toString());
                    value.put("translation", translation.getText().toString());
                    value.put("example", example.getText().toString());
                    value.put("exampleTran", exampleTran.getText().toString());
                    db.insert("NewWords", null, value);
                    Toast.makeText(AllWordsActivity.this, "添加成功",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AllWordsActivity.this, "生词表中已存在该单词",
                            Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(AllWordsActivity.this, "单词为空，添加失败",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void setData(String[] string) {

        System.out.println(string.length);
        word.setText(string[0]);
        translation.setText(string[1]);
        example.setText(string[2]);
        exampleTran.setText(string[3]);

    }

}
