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

import com.example.wordlist.R;

import databace.MyDatabaseHelper;

public class EditNewActivity extends AppCompatActivity {

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
            dbHelper = new MyDatabaseHelper(EditNewActivity.this, "WordList.db", null, 2);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if(word.getText().toString() != null && translation.getText().toString() != null) {
                System.out.println("word is not empty");
                String[] selectionArgs = {word.getText().toString()};
                String changeWord = word.getText().toString();
                String Translation = translation.getText().toString();
                String Example = example.getText().toString();
                String ExampleTran = exampleTran.getText().toString();
                Cursor cursorNew = db.query("NewWords", null, "word=?", selectionArgs, null, null, null, null);
                //NewWords中存在的数据AllWords中也必然存在
                if (cursorNew.moveToFirst()) {
                    ContentValues value = new ContentValues();
                    value.put("translation", Translation);
                    value.put("example", Example);
                    value.put("exampleTran", ExampleTran);
                    db.update("NewWords", value, "word=?", selectionArgs);
                    db.update("AllWords", value, "word=?", selectionArgs);
                }
                else{
                    ContentValues value = new ContentValues();
                    value.put("word", changeWord);
                    value.put("translation", Translation);
                    value.put("example", Example);
                    value.put("exampleTran", ExampleTran);

                    //存储数据库中原本的主键
                    String[] updateArgs = {Word};
                    //更新原主键的行中所有数据
                    db.update("NewWords", value, "word=?", updateArgs);
                    db.update("AllWords", value, "word=?", updateArgs);
                }
                Intent intent = new Intent(EditNewActivity.this, NewWordsActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(EditNewActivity.this, "单词和翻译处不能为空",
                                        Toast.LENGTH_LONG).show();
                System.out.println("word is empty");
            }
        }
    }
}
