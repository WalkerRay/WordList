package com.example.wordslist;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import databace.MyDatabaseHelper;

public class EditAllActivity extends AppCompatActivity {

    private EditText word;
    private EditText translation;
    private EditText example;
    private EditText exampleTran;
    private Button save;
    //用于存储数据库中原本的word主键
    private String Word;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_words);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        word = findViewById(R.id.editword);
        translation = findViewById(R.id.editTranslation);
        example = findViewById(R.id.editexample);
        exampleTran = findViewById(R.id.editexampleTran);
        save = findViewById(R.id.save);
        Intent intent = getIntent();
        Word = intent.getStringExtra("word");
        String Translation = intent.getStringExtra("translation");
        String Example = intent.getStringExtra("example");
        String ExampleTran = intent.getStringExtra("exampleTran");
        word.setText(Word);
        translation.setText(Translation);
        example.setText(Example);
        exampleTran.setText(ExampleTran);
        save.setOnClickListener(new Save());
    }

    class Save implements View.OnClickListener {
        @Override
        public void onClick(View v){
            dbHelper = new MyDatabaseHelper(EditAllActivity.this, "WordList.db", null, 2);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if(word.getText().toString() != null && translation.getText().toString() != null) {
                String[] selectionArgs = {word.getText().toString()};

                //用于改变的单词数据（即新的）
                String changeWord = word.getText().toString();
                String Translation = translation.getText().toString();
                String Example = example.getText().toString();
                String ExampleTran = exampleTran.getText().toString();

                //通过编辑后的单词数据中的word主键查找单词表和生词表两个数据库
                Cursor cursorNew1 = db.query("NewWords", null, "word=?", selectionArgs, null, null, null, null);
                Cursor cursorAll = db.query("AllWords", null, "word=?", selectionArgs, null, null, null, null);
                //主键（即word）未发生改变的情况下
                if (cursorAll.moveToFirst()) {
                    ContentValues value = new ContentValues();
                    value.put("translation", Translation);
                    value.put("example", Example);
                    value.put("exampleTran", ExampleTran);
                    //如果生词表中也存在改词，对其进行更新
                    if(cursorNew1.moveToFirst()){
                        db.update("NewWords", value, "word=?", selectionArgs);
                    }
                    db.update("AllWords", value, "word=?", selectionArgs);
                }
                //主键改变的情况下
                else{
                    ContentValues value = new ContentValues();
                    value.put("word", changeWord);
                    value.put("translation", Translation);
                    value.put("example", Example);
                    value.put("exampleTran", ExampleTran);
                    //存储数据库中原本的主键
                    String[] updateArgs = {Word};
                    //通过原主键在NewWords数据库中寻找，若是存在数据就对其进行更新，否则只更新AllWords
                    Cursor cursorNew2 = db.query("NewWords", null, "word=?", updateArgs, null, null, null, null);
                    if(cursorNew2.moveToFirst()) {
                        db.update("NewWords", value, "word=?", updateArgs);
                    }
                    db.update("AllWords", value, "word=?", updateArgs);
                }
                Intent intent = new Intent(EditAllActivity.this, AllWordsActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(EditAllActivity.this, "单词和翻译处不能为空",
                        Toast.LENGTH_LONG).show();
                System.out.println("word is empty");
            }
        }
    }
}
