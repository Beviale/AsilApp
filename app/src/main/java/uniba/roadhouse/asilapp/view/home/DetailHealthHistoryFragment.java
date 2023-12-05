package uniba.roadhouse.asilapp.view.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uniba.roadhouse.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailHealthHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailHealthHistoryFragment extends Fragment {
    TextView detailHealthHistoryTitle;

    String itemCliecked;



    public DetailHealthHistoryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DetailHealthHistoryFragment newInstance() {
        DetailHealthHistoryFragment fragment = new DetailHealthHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           itemCliecked = getArguments().getString("itemClicked");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_health_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //-----------RIFERIMENTI-------------
        detailHealthHistoryTitle = view.findViewById(R.id.detailHealthHistoryTitle);
        detailHealthHistoryTitle.setText(itemCliecked);
    }
}