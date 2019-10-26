package com.example.wordslist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import model.News;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;



import androidx.fragment.app.Fragment;

import com.facebook.stetho.inspector.protocol.module.Network;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private ListView listView;
    final String url = "http://10.3.173.232:8080/EnglishArticleServer/ArticleMessage.jsp";
    private List<News> newsList = new ArrayList<News>();
    private static final String TAG = "NewsFragment";
    private String[] expRetitle;
    private String[] expRelink;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View Newspage = inflater.inflate(R.layout.news_fragment, container,false);

        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                expRetitle = bundle.getStringArray("expRetitle");
                expRelink = bundle.getStringArray("expRelink");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, expRetitle);
                listView = Newspage.findViewById(R.id.allNews);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent intent = new Intent(getContext(), NewsActivity.class);
                        Log.d(TAG, "onItemClick: "+position+expRelink[position]);
                        intent.putExtra("link", expRelink[position]);
                        startActivity(intent);
                    }
                });
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //防止socket failed: EACCES (Permission denied)异常
                    //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    String[] jsonsplit = responseData.split("\\[|\\]");
                    //Log.d(TAG, jsonsplit[1]);
                    responseData = "["+jsonsplit[1]+"]";
                    newsList =  parseJSONWithGSON(responseData);
                    //Log.d(TAG, newsList.size()+"long");
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    expRetitle = new String[newsList.size()];
                    expRelink = new String[newsList.size()];
                    for (int i = 0; i < newsList.size(); i++) {
                        expRetitle[i] = newsList.get(i).getTitle();
                        expRelink[i] = newsList.get(i).getLink();
                    }
                    bundle.putStringArray("expRetitle", expRetitle);
                    bundle.putStringArray("expRelink", expRelink);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                } catch (IOException e) {
                    System.out.println(e.getMessage()+"error");
                }
            }
        }).start();


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, expRe);
//        listView = Newspage.findViewById(R.id.allNews);
//        listView.setAdapter(adapter);




        return Newspage;
    }

    private List<News> parseJSONWithGSON(String jsonData){
        //Json的解析类对象
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        List<News> news = gson.fromJson(jsonData, new TypeToken<List<News>>(){}.getType());
//        List<News> news = new ArrayList<News>();
//        JsonArray jsonArray = parser.parse(jsonData).getAsJsonArray();
//
//        for (JsonElement user : jsonArray) {
//            //使用GSON，直接转成Bean对象
//            News newsn = gson.fromJson(user, new TypeToken<List<News>>(){}.getType());
//            news.add(newsn);
//        }
        return news;
    }
}
