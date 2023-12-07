package uniba.roadhouse.asilapp.view.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;


public class DetaiOldHealthHistoryFragment extends Fragment {
    TextView idRecordHealthHistoryOld;
    TextView dateRecordHealthHistoryOld;
    TextView timeRecordHealthHistoryOld;
    TextView valueLastRecordHealthHistoryOld;
    TextView unityDetaildHealthHistoryOld;
    TextView evalutationRecordHealthHistoryOld;
    TextView doctorNotesRecordHealthHistoryOld;
    ImageView shareDetailHealthHistoryOld;


    public DetaiOldHealthHistoryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DetaiOldHealthHistoryFragment newInstance(String param1, String param2) {
        DetaiOldHealthHistoryFragment fragment = new DetaiOldHealthHistoryFragment();
        Bundle args = new Bundle();
       ;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detai_old_health_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //--------RIFERIMENTI----------------
        idRecordHealthHistoryOld = view.findViewById(R.id.idRecordHealthHistoryOld);
        dateRecordHealthHistoryOld = view.findViewById(R.id.dateRecordHealthHistoryOld);
        timeRecordHealthHistoryOld = view.findViewById(R.id.timeRecordHealthHistoryOld);
        valueLastRecordHealthHistoryOld = view.findViewById(R.id.valueLastRecordHealthHistoryOld);
        evalutationRecordHealthHistoryOld = view.findViewById(R.id.evalutationRecordHealthHistoryOld);
        doctorNotesRecordHealthHistoryOld = view.findViewById(R.id.doctorNotesRecordHealthHistoryOld);
        Utility.enableScroll(doctorNotesRecordHealthHistoryOld);
        shareDetailHealthHistoryOld = view.findViewById(R.id.shareDetailHealthHistoryOld);
    }

    @Override
    public void onStart() {
        super.onStart();
        //-----------LISTENER------------------
        shareDetailHealthHistoryOld.setOnClickListener(v->showCheckboxDialogForSharePrivacy());
    }


    private void showCheckboxDialogForSharePrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_health_history, null);
        CheckBox checkBoxId = view.findViewById(R.id.dialogShareDetailHistoryId);
        CheckBox checkBoxDate = view.findViewById(R.id.dialogShareDetailHistoryDate);
        CheckBox checkBoxTime = view.findViewById(R.id.dialogShareDetailHistoryTime);
        CheckBox checkBoxValue = view.findViewById(R.id.dialogShareDetailHistoryValue);
        CheckBox checkBoxEvalutation = view.findViewById(R.id.dialogShareDetailHistoryEvalutation);
        CheckBox checkBoxDoctorNotes = view.findViewById(R.id.dialogShareDetailHistoryDoctorNotes);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(checkBoxId);
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
                        String share = new String();
                        if(checkBoxId.isChecked())
                            share = share.concat(getString(R.string.idLastRecordHealthHistoryLabel)).concat(idRecordHealthHistoryOld.getText().toString().concat("\n"));
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        if(checkBoxDate.isChecked())
                            share = share.concat(getString(R.string.dateLastRecordHealthHistoryLabel).concat(dateRecordHealthHistoryOld.getText().toString()).concat("\n"));
                        if(checkBoxTime.isChecked())
                            share = share.concat(getString(R.string.timeLastRecordHealthHistoryLabel).concat(timeRecordHealthHistoryOld.getText().toString()).concat("\n"));
                        if(checkBoxEvalutation.isChecked())
                            share = share.concat(getString(R.string.evalutationLastRecordHealthHistoryLabel).concat(evalutationRecordHealthHistoryOld.getText().toString()).concat("\n"));
                        if(checkBoxDoctorNotes.isChecked())
                            share = share.concat(getString(R.string.doctorNotesLastRecordHealthHistoryLabel).concat("\n").concat(doctorNotesRecordHealthHistoryOld.getText().toString()).concat("\n"));
                        intent.setType("text/plain");
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, share);
                        startActivity(intent);
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
}