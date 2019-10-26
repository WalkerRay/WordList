package com.example.wordslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class NewsActivity extends AppCompatActivity {

    private WebView newsWeb;
    private String link = new String();
    private static final String TAG = "NewsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        Log.d(TAG, "onCreate: "+link);
        newsWeb = findViewById(R.id.newsWebView);
        newsWeb.loadUrl(link);
    }
}
