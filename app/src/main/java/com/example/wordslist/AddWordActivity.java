package com.example.wordslist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wordlist.R;

import databace.MyDatabaseHelper;

public class AddWordActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_words);

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new Save());
    }

    class Save implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            TextView word = findViewById(R.id.editword);
            TextView translation = findViewById(R.id.editTranslation);
            TextView example = findViewById(R.id.editexample);
            TextView exampleTran = findViewById(R.id.editexampleTran);

            dbHelper = new MyDatabaseHelper(AddWordActivity.this, "WordList.db", null, 2);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            if(!word.getText().toString().isEmpty() && !translation.getText().toString().isEmpty()) {
                String[] selectionArgs = {word.getText().toString()};
                final Cursor cursor = db.query("AllWords", null, "word = ?", selectionArgs, null,null,null,null);
                if(cursor.moveToFirst()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddWordActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(word.getText().toString() + "在单词表中已存在");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(AllWordsActivity.this, "确定",
//                                        Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNeutralButton("查看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(AllWordsActivity.this, "编辑",
//                                        Toast.LENGTH_LONG).show();
                            String Word = cursor.getString(cursor.getColumnIndex("word"));
                            String Translation = cursor.getString(cursor.getColumnIndex("translation"));
                            String Example = cursor.getString(cursor.getColumnIndex("example"));
                            String ExampleTran = cursor.getString(cursor.getColumnIndex("exampleTran"));
                            Intent intent = new Intent(AddWordActivity.this, ShowWordActivity.class);
                            intent.putExtra("word", Word);
                            intent.putExtra("translation", Translation);
                            intent.putExtra("example", Example);
                            intent.putExtra("exampleTran", ExampleTran);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.show();
                }else{
                    ContentValues values = new ContentValues();
                    values.put("word", word.getText().toString());
                    values.put("translation", translation.getText().toString());
                    values.put("example", example.getText().toString());
                    values.put("exampleTran", exampleTran.getText().toString());
                    db.insert("AllWords", null, values);
                    Toast.makeText(AddWordActivity.this, "添加成功",
                                        Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(AddWordActivity.this, "单词和翻译处不能为空",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
