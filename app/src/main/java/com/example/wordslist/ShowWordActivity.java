package com.example.wordslist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wordlist.R;

public class ShowWordActivity extends AppCompatActivity {

    private TextView word;
    private TextView translation;
    private TextView example;
    private TextView exampleTran;
    private Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_words);

        word = findViewById(R.id.showWord);
        translation = findViewById(R.id.showTranslation);
        example = findViewById(R.id.showexample);
        exampleTran = findViewById(R.id.showexampleTran);
        edit = findViewById(R.id.edit);

        Intent intent = getIntent();
        String Word = intent.getStringExtra("word");
        String Translation = intent.getStringExtra("translation");
        String Example = intent.getStringExtra("example");
        String ExampleTran = intent.getStringExtra("exampleTran");
        word.setText(Word);
        translation.setText(Translation);
        example.setText(Example);
        exampleTran.setText(ExampleTran);
        edit.setOnClickListener(new Edit());

    }

    class Edit implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ShowWordActivity.this, EditNewActivity.class);
            intent.putExtra("word", word.getText().toString());
            intent.putExtra("translation", translation.getText().toString());
            intent.putExtra("example", example.getText().toString());
            intent.putExtra("exampleTran", exampleTran.getText().toString());
            startActivity(intent);
            finish();
        }
    }
}
