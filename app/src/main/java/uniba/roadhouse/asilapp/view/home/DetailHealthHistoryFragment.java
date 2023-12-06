package uniba.roadhouse.asilapp.view.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailHealthHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailHealthHistoryFragment extends Fragment {

    ImageView shareDetailHealthHistory;
    TextView unityDetaildHealthHistory;
    TextView detailHealthHistoryTitle;
    EditText doctorNotesLastRecordHealthHistory;

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
        shareDetailHealthHistory = view.findViewById(R.id.shareDetailHealthHistory);
        registerForContextMenu(shareDetailHealthHistory);
        doctorNotesLastRecordHealthHistory = view.findViewById(R.id.doctorNotesLastRecordHealthHistory);
        Utility.enableScroll(doctorNotesLastRecordHealthHistory);
        detailHealthHistoryTitle = view.findViewById(R.id.detailHealthHistoryTitle);
        detailHealthHistoryTitle.setText(itemCliecked);
        unityDetaildHealthHistory = view.findViewById(R.id.unityDetaildHealthHistory);
        unityDetaildHealthHistory.setText(setUnity());
    }

    @Override
    public void onStart() {
        super.onStart();
        shareDetailHealthHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.share_context, menu);
    }

    private String setUnity()
    {
        String unity = new String();
        if(itemCliecked.equals(getString(R.string.temperatureHealthHistory)))
            unity=getString(R.string.unityTemperature);
        if(itemCliecked.equals(getString(R.string.bloodPressureHealthHistory)))
            unity=getString(R.string.unityBloodPressure);
        if(itemCliecked.equals(getString(R.string.weightHealthHistory)))
            unity=getString(R.string.unityWeight);
        if(itemCliecked.equals(getString(R.string.bpmHealthHistory)))
            unity=getString(R.string.unityBPM);
        if(itemCliecked.equals(getString(R.string.tremblingHealthHistory)))
            unity=getString(R.string.unityTrembling);
        if(itemCliecked.equals(getString(R.string.tremblingHealthHistory)))
            unity=getString(R.string.unityTrembling);
        if(itemCliecked.equals(getString(R.string.glucoseHealthHistory)))
            unity=getString(R.string.unityGlucose);
        return unity;
    }



}

