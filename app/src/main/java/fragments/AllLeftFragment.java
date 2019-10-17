package fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.wordlist.R;
import com.example.wordslist.AllWordsActivity;

import java.util.ArrayList;
import java.util.List;

import databace.MyDatabaseHelper;
import model.Words;

public class AllLeftFragment extends Fragment {

    private MyDatabaseHelper dbHelper;
    private int listsize = 0;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.all_left, container, false);
        List<Words> allWords = new ArrayList<Words>();

        dbHelper = new MyDatabaseHelper(AllLeftFragment.this.getContext(), "WordList.db", null, 2);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        //查询AllWords全部数据
        Cursor cursor = db.query("AllWords", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            System.out.println("start2");
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
        //listsize = allWords.size();
        for(int i = 0; i < allWords.size(); i ++){
            expRe[i] = allWords.get(i).getWord()+":"+allWords.get(i).getTranslation();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllLeftFragment.this.getContext(), android.R.layout.simple_list_item_1, expRe);
        listView = (ListView)view.findViewById(R.id.allwords);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = listView.getItemAtPosition(position).toString();
                final String[] Text = text.split(":");
                Cursor all = db.rawQuery("select * from AllWords where word = ?", new String[]{Text[0]});
                all.moveToFirst();
                final String example = all.getString(all.getColumnIndex("example"));
                final String exampleTran = all.getString(all.getColumnIndex("exampleTran"));
                String[] data = {Text[0], Text[1], example, exampleTran};
                System.out.println(Text[0]+Text[1]+example+exampleTran);
                //AllRightFragment rightFragment = (AllRightFragment) getActivity().getSupportFragmentManager().findFragmentByTag("rightFragment");
                AllWordsActivity.setData(data);
            }
        });

        return view;
    }
}
