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

import com.example.wordslist.AllWordsActivity;
import com.example.wordslist.NewWordsActivity;
import com.example.wordslist.R;

import java.util.ArrayList;
import java.util.List;

import databace.MyDatabaseHelper;
import model.Words;

public class NewLeftFragment extends Fragment {

    private MyDatabaseHelper dbHelper;
    private int listsize = 0;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.new_left, container, false);
        List<Words> newWords = new ArrayList<Words>();

        dbHelper = new MyDatabaseHelper(NewLeftFragment.this.getContext(), "WordList.db", null, 2);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        //查询NewWords全部数据
        Cursor cursor = db.query("NewWords", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            System.out.println("start1");
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
        listsize = newWords.size();
        for (int i = 0; i < newWords.size(); i++) {
            expRe[i] = newWords.get(i).getWord() + ":" + newWords.get(i).getTranslation();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewLeftFragment.this.getContext(), android.R.layout.simple_list_item_1, expRe);
        listView = (ListView) view.findViewById(R.id.newwords);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = listView.getItemAtPosition(position).toString();
                final String[] Text = text.split(":");
                Cursor all = db.rawQuery("select * from NewWords where word = ?", new String[]{Text[0]});
                all.moveToFirst();
                final String example = all.getString(all.getColumnIndex("example"));
                final String exampleTran = all.getString(all.getColumnIndex("exampleTran"));
                String[] data = {Text[0], Text[1], example, exampleTran};
                System.out.println(Text[0]+Text[1]+example+exampleTran);
                //AllRightFragment rightFragment = (AllRightFragment) getActivity().getSupportFragmentManager().findFragmentByTag("rightFragment");
                NewWordsActivity.setData(data);
            }
        });

        return view;
    }
}
