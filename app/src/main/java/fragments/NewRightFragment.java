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

public class NewRightFragment extends Fragment {
    private TextView word;
    private TextView translation;
    private TextView example;
    private TextView exampleTran;
    private Button edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.new_right, container, false);
        word = view.findViewById(R.id.newrWord);
        translation = view.findViewById(R.id.newrTranslation);
        example = view.findViewById(R.id.newrExample);
        exampleTran = view.findViewById(R.id.newrExampleTran);
        edit = view.findViewById(R.id.newrEdit);

        return view;
    }

}
