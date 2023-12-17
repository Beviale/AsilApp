package uniba.roadhouse.asilapp.controller.patient.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import uniba.roadhouse.asilapp.controller.other.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailMyPathologiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailMyPathologiesFragment extends Fragment {
    TextView idLastVisitMyPathologies;
    static String itemCliecked;
    TextView detailMyPathologiesTitle;
    TextView dateLastVisitMyPathologies;
    TextView timeLastVisitMyPathologies;
    TextView priorityLastVisitMyPathologies;

    EditText doctorNotesLastVisitMyPahologies;
    ImageView shareDetailMyPathologies;
    TextView oldMyPathologiesTitle;
    TextView oldMyPathologiesId;
    TextView oldMyPathologiesPriority;
    TextView oldMyPathologiesDate;



    public DetailMyPathologiesFragment() {
        // Required empty public constructor
    }


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
            if (getArguments() != null) {
                itemCliecked = getArguments().getString("itemClicked");
            }
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
        timeLastVisitMyPathologies = view.findViewById(R.id.timeLastVisitMyPathologies);
        dateLastVisitMyPathologies = view.findViewById(R.id.dateLastVisitMyPathologies);
        detailMyPathologiesTitle = view.findViewById(R.id.detailMyPathologiesTitle);
        priorityLastVisitMyPathologies = view.findViewById(R.id.priorityLastVisitMyPathologies);
        Utility.enableScroll(doctorNotesLastVisitMyPahologies);
        shareDetailMyPathologies = view.findViewById(R.id.shareDetailMyPathologies);
        idLastVisitMyPathologies = view.findViewById(R.id.idLastVisitMyPathologies);
        oldMyPathologiesTitle = view.findViewById(R.id.oldMyPathologiesTitle);
        oldMyPathologiesPriority = view.findViewById(R.id.oldMyPathologiesPriority);
        oldMyPathologiesId = view.findViewById(R.id.oldMyPathologiesId);
        oldMyPathologiesDate = view.findViewById(R.id.oldMyPathologiesDate);

        setDetailMyPathologiesTitle();
        setDateLastVisitMyPathologies();
        setTimeLastVisitMyPathologies();
        setPrioprityLastVisitMyPathologies();
        setOldDetailMyPathologiesTitle();
        setOldDetailMyPathologiesId();
        setOldDetailMyPathologiesPriority();
        setOldDetailMyPathologiesDate();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    }



    private void setDetailMyPathologiesTitle()
    {
        detailMyPathologiesTitle.setText(itemCliecked);
    }

    private void setDateLastVisitMyPathologies()
    {
        if(itemCliecked.equals(getString(R.string.diabete)))
        {
            dateLastVisitMyPathologies.setText("20/10/2023");
        }
        if(itemCliecked.equals(getString(R.string.dermatitis)))
        {
            dateLastVisitMyPathologies.setText("10/05/2023");
        }
        if(itemCliecked.equals(getString(R.string.brochitis)))
        {
            dateLastVisitMyPathologies.setText("20/10/2022");
        }
    }

    private void setTimeLastVisitMyPathologies()
    {
        if(itemCliecked.equals(getString(R.string.diabete)))
        {
            timeLastVisitMyPathologies.setText("10:20");
        }
        if(itemCliecked.equals(getString(R.string.dermatitis)))
        {
            timeLastVisitMyPathologies.setText("15:00");
        }
        if(itemCliecked.equals(getString(R.string.brochitis)))
        {
            timeLastVisitMyPathologies.setText("17:30");
        }
    }


    private void setPrioprityLastVisitMyPathologies()
    {
        if(itemCliecked.equals(getString(R.string.diabete)))
        {
            priorityLastVisitMyPathologies.setText("alta \uD83D\uDD34");
        }
        if(itemCliecked.equals(getString(R.string.dermatitis)))
        {
            priorityLastVisitMyPathologies.setText("media \uD83D\uDFE0");
        }
        if(itemCliecked.equals(getString(R.string.brochitis)))
        {
            priorityLastVisitMyPathologies.setText("bassa \uD83D\uDFE2");
        }

    }

    private void setOldDetailMyPathologiesTitle()
    {
        oldMyPathologiesTitle.setText(itemCliecked);
    }

    private void setOldDetailMyPathologiesId()
    {
        if(itemCliecked.equals(getString(R.string.diabete)))
        {
            oldMyPathologiesId.setText("14");
        }
        if(itemCliecked.equals(getString(R.string.dermatitis)))
        {
            oldMyPathologiesId.setText("9");
        }
        if(itemCliecked.equals(getString(R.string.brochitis)))
        {
            oldMyPathologiesId.setText("5");
        }
    }

    private void setOldDetailMyPathologiesPriority()
    {
        if(itemCliecked.equals(getString(R.string.diabete)))
        {
            oldMyPathologiesPriority.setText("alta \uD83D\uDD34");
        }
        if(itemCliecked.equals(getString(R.string.dermatitis)))
        {
            oldMyPathologiesPriority.setText("media \uD83D\uDFE0");
        }
        if(itemCliecked.equals(getString(R.string.brochitis)))
        {
            oldMyPathologiesPriority.setText("alta \uD83D\uDD34");
        }
    }

    private void setOldDetailMyPathologiesDate()
    {
        if(itemCliecked.equals(getString(R.string.diabete)))
        {
            oldMyPathologiesId.setText("20/06/2023");
        }
        if(itemCliecked.equals(getString(R.string.dermatitis)))
        {
            oldMyPathologiesId.setText("25/01/2023");
        }
        if(itemCliecked.equals(getString(R.string.brochitis)))
        {
            oldMyPathologiesId.setText("30/11/2021");
        }
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


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
        super.onResume();
    }





    private void showCheckboxDialogForSharePrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_pathologies, null);

        CheckBox checkBoxId = view.findViewById(R.id.dialogShareDetailPathologiesId);
        CheckBox checkBoxDate = view.findViewById(R.id.dialogShareDetailPathologiesDate);
        CheckBox checkBoxTime = view.findViewById(R.id.dialogShareDetailPathologiesTime);
        CheckBox checkBoxPriority = view.findViewById(R.id.dialogShareDetailPathologiesPriority);
        CheckBox checkBoxDoctorNotes = view.findViewById(R.id.dialogShareDetailPathologiesDoctorNotes);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(checkBoxId);
        checkBoxes.add(checkBoxDate);
        checkBoxes.add(checkBoxTime);
        checkBoxes.add(checkBoxPriority);
        checkBoxes.add(checkBoxDoctorNotes);
        Utility.colorAllCheckbox(checkBoxes, getActivity());


        builder.setView(view)
                .setTitle(getString(R.string.titleShareDialogPrivacy))
                .setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String share = new String();
                        if(checkBoxId.isChecked())
                            share = share.concat(getString(R.string.idLastVisitMyPathologiesLabel)).concat(idLastVisitMyPathologies.getText().toString().concat("\n"));
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        if(checkBoxDate.isChecked())
                            share = share.concat(getString(R.string.dateLastVisitMyPathologiesLabel).concat(dateLastVisitMyPathologies.getText().toString()).concat("\n"));
                        if(checkBoxTime.isChecked())
                            share = share.concat(getString(R.string.timeLastVisitMyPathologiesLabel).concat(timeLastVisitMyPathologies.getText().toString()).concat("\n"));
                        if(checkBoxPriority.isChecked())
                            share = share.concat(getString(R.string.priorityLastVisitMyPathologiesLabel).concat(priorityLastVisitMyPathologies.getText().toString()).concat("\n"));
                        if(checkBoxDoctorNotes.isChecked())
                            share = share.concat(getString(R.string.doctorNotesLastVisitMyPahologiesLabel).concat("\n").concat(doctorNotesLastVisitMyPahologies.getText().toString()).concat("\n"));
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