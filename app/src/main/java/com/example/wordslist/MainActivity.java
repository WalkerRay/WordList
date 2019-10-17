package com.example.wordslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordlist.R;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.common.LogUtil;

import java.util.ArrayList;
import java.util.List;

import databace.MyDatabaseHelper;
import model.Words;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MyDatabaseHelper dbHelper;

    private SearchView wordSearchView;
    private ListView searchList;

    //标签信息
    private TextView item_news, item_collection;
    //创建ViewPager
    private ViewPager vp ;

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDatabaseHelper(this, "WordList.db", null, 2);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.execSQL("delete from NewWords where word = ?", new String[]{""});
//        ContentValues values = new ContentValues();
//        values.put("title", "Xi says no force can ever undermine China's status");
//        values.put("address", "http://english.cctv.com/2019/10/02/ARTI9fCIRS3sE4RVj5T3BauB191002.shtml?spm=C69523.P89571092934.E19742616158.1");
//        db.insert("News", null, values);
//        db.execSQL("insert into NewWords (word, translation, example, exampleTran) values(?, ?, ?, ?)", new String[]{"apple", "苹果", "This is an apple", "这是一个苹果"});
//        db.execSQL("insert into NewWords (word, translation, example, exampleTran) values(?, ?, ?, ?)", new String[]{"banana", "香蕉", "This is a banana", "这是一个香蕉"});
//        db.execSQL("insert into NewWords (word, translation, example, exampleTran) values(?, ?, ?, ?)", new String[]{"happy", "高兴的", "I'm really happy", "我好开心啊"});


        //用于android调试
        Stetho.initializeWithDefaults(this);

        //设置
        initViews();
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        vp = (ViewPager) findViewById(R.id.newsViewPager);
        vp.setOffscreenPageLimit(2);//ViewPager的缓存为2帧
        vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);//初始设置ViewPager选中第一帧
        item_news.setTextColor(Color.parseColor("#66CDAA"));

        //ViewPager的监听事件
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*此方法在页面被选中时调用*/
                changeTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /*此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
                arg0 ==1的时辰默示正在滑动，
                arg0==2的时辰默示滑动完毕了，
                arg0==0的时辰默示什么都没做。*/
            }
        });

        Button wordlist = findViewById(R.id.WordList);
        wordlist.setOnClickListener(new WordList());
        Button notremember = findViewById(R.id.NotRemember);
        notremember.setOnClickListener(new NotRemember());

        //跳转至单词添加
        Button addword = findViewById(R.id.AddWord);
        addword.setOnClickListener(new AddWord());

        //查询单词
        wordSearchView = findViewById(R.id.search);
        wordSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            //当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] selectionArgs = {query};
                Cursor cursor = (Cursor) db.query("AllWords", null, "word = ?", selectionArgs, null, null, null, null);
                if(cursor.moveToFirst()){

                    String word = cursor.getString(cursor.getColumnIndex("word"));
                    String translation = cursor.getString(cursor.getColumnIndex("translation"));
                    String example = cursor.getString(cursor.getColumnIndex("example"));
                    String exampleTran = cursor.getString(cursor.getColumnIndex("exampleTran"));

                    Intent intent = new Intent(MainActivity.this, ShowWordActivity.class);
                    intent.putExtra("word", word);
                    intent.putExtra("translation", translation);
                    intent.putExtra("example", example);
                    intent.putExtra("exampleTran", exampleTran);
                    startActivity(intent);
                    cursor.close();
                }
                else{
                    Toast.makeText(MainActivity.this, "未查询到该单词",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            }

            //当搜索内容改变时触发该方法(模糊查询)
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList = findViewById(R.id.searchList);
                if(!newText.isEmpty()) {
                    String[] selectionArgs = {"%" + newText + "%"};
                    Cursor cursor = db.query("AllWords", null, "word like ?", selectionArgs, null, null, null, null);
                    List<Words> newWords = new ArrayList<Words>();
                    if (cursor.moveToFirst()) {
                        do {
                            Words notRemember = new Words();
                            String word = cursor.getString(cursor.getColumnIndex("word"));
                            String translation = cursor.getString(cursor.getColumnIndex("translation"));
                            String example = cursor.getString(cursor.getColumnIndex("example"));
                            String exampleTran = cursor.getString(cursor.getColumnIndex("exampleTran"));
                            notRemember.setWord(word);
                            notRemember.setTranslation(translation);
                            if (example != null) {
                                notRemember.setExample(example);
                            }
                            if (exampleTran != null) {
                                notRemember.setExampleTran(exampleTran);
                            }
                            newWords.add(notRemember);

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    String[] expRe = new String[newWords.size()];
                    for (int i = 0; i < newWords.size(); i++) {
                        expRe[i] = newWords.get(i).getWord() + ":" + newWords.get(i).getTranslation();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, expRe);
                    searchList.setAdapter(adapter);

                    searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            String text = searchList.getItemAtPosition(position).toString();
                            final String[] Text = text.split(":");

                            Cursor search = db.rawQuery("select * from AllWords where word = ?", new String[] {Text[0]});
                            search.moveToFirst();
                            String example = search.getString(search.getColumnIndex("example"));
                            String exampleTran = search.getString(search.getColumnIndex("exampleTran"));
                            Intent intent = new Intent(MainActivity.this, ShowWordActivity.class);
                            intent.putExtra("word", Text[0]);
                            intent.putExtra("translation", Text[1]);
                            intent.putExtra("example", example);
                            intent.putExtra("exampleTran", exampleTran);
                            startActivity(intent);
                        }

                    });

                }

                return true;
            }
        });
    }

    class AddWord implements  View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
            startActivity(intent);
        }
    }

    class WordList implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AllWordsActivity.class);
            startActivity(intent);
        }
    }

    class NotRemember implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, NewWordsActivity.class);
            startActivity(intent);
        }
    }

    //onClick事件（点击底部Text动态修改ViewPager内容）
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_news:
                vp.setCurrentItem(0, true);
                break;
            case R.id.item_collection:
                vp.setCurrentItem(1, true);
                break;
        }
    }

    //初始化布局View
    public void initViews(){
        //二个竖屏Fragment
        NewsFragment nFragment;
        CollectionFragment cFragment;

        System.out.println("doingInitPortView");
        item_news = (TextView) findViewById(R.id.item_news);
        item_collection = (TextView) findViewById(R.id.item_collection);

        item_news.setOnClickListener(this);
        item_collection.setOnClickListener(this);


        vp = (ViewPager) findViewById(R.id.newsViewPager);
        nFragment = new NewsFragment();
        cFragment = new CollectionFragment();

        //给FragmentList添加数据
        mFragmentList.add(nFragment);
        mFragmentList.add(cFragment);

    }

    //ViewPager的适配器(ViewPager类需要一个PagerAdapter适配器类给它提供数据。这里面需要的数据是Fragment，所以是Fragment适配器)
    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }


        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private void changeTextColor(int position) {
        if (position == 0) {
            item_news.setTextColor(Color.parseColor("#66CDAA"));
            item_collection.setTextColor(Color.parseColor("#000000"));
        } else if (position == 1) {
            item_collection.setTextColor(Color.parseColor("#66CDAA"));
            item_news.setTextColor(Color.parseColor("#000000"));
        }
    }


}
