package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wordlist.R;
import com.example.wordslist.AllWordsActivity;
import com.example.wordslist.EditAllActivity;

public class AllRightFragment extends Fragment {
    private TextView word;
    private TextView translation;
    private TextView example;
    private TextView exampleTran;
    private Button edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.all_right, container, false);
        word = view.findViewById(R.id.allrWord);
        translation = view.findViewById(R.id.allrTranslation);
        example = view.findViewById(R.id.allrExample);
        exampleTran = view.findViewById(R.id.allrExampleTran);
        edit = view.findViewById(R.id.allrEdit);

        return view;
    }


}
