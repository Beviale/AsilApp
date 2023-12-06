package uniba.roadhouse.asilapp.view.home;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailMyPathologiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailMyPathologiesFragment extends Fragment {
    EditText doctorNotesLastVisitMyPahologies;
    ImageView shareDetailMyPathologies;



    public DetailMyPathologiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailMyPathologies.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailMyPathologiesFragment newInstance(String param1, String param2) {
        DetailMyPathologiesFragment fragment = new DetailMyPathologiesFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.detail_my_pathologies_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //---------------RIFERIMENTI-------------
        doctorNotesLastVisitMyPahologies = view.findViewById(R.id.doctorNotesLastVisitMyPahologies);
        Utility.enableScroll(doctorNotesLastVisitMyPahologies);
        shareDetailMyPathologies = view.findViewById(R.id.shareDetailMyPathologies);
    }

    @Override
    public void onStart() {
        super.onStart();
        //--------------LISTENER-----------
        shareDetailMyPathologies.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showCheckboxDialogForSharePrivacy();

            }
        });
    }



    private void showCheckboxDialogForSharePrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_pathologies, null);

        CheckBox checkBoxDate = view.findViewById(R.id.dialogShareDetailPathologiesDate);
        CheckBox checkBoxTime = view.findViewById(R.id.dialogShareDetailPathologiesTime);
        CheckBox checkBoxEvalutation = view.findViewById(R.id.dialogShareDetailPathologiesPriority);
        CheckBox checkBoxDoctorNotes = view.findViewById(R.id.dialogShareDetailPathologiesDoctorNotes);

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
}