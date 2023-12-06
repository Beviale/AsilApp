package uniba.roadhouse.asilapp.view.home;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
                showCheckboxDialogForSharePrivacy();
            }
        });
    }

    private void showCheckboxDialogForSharePrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_health_history, null);
        CheckBox checkBoxDate = view.findViewById(R.id.dialogShareDetailHistoryDate);
        CheckBox checkBoxTime = view.findViewById(R.id.dialogShareDetailHistoryTime);
        CheckBox checkBoxValue = view.findViewById(R.id.dialogShareDetailHistoryValue);
        CheckBox checkBoxEvalutation = view.findViewById(R.id.dialogShareDetailHistoryEvalutation);
        CheckBox checkBoxDoctorNotes = view.findViewById(R.id.dialogShareDetailHistoryDoctorNotes);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(checkBoxDate);
        checkBoxes.add(checkBoxTime);
        checkBoxes.add(checkBoxValue);
        checkBoxes.add(checkBoxEvalutation);
        checkBoxes.add(checkBoxDoctorNotes);
        Utility.colorAllCheckbox(checkBoxes, getActivity());
        builder.setView(view)
                .setTitle(getString(R.string.titleShareDialogPrivacy))
                .setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle negative button click
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

