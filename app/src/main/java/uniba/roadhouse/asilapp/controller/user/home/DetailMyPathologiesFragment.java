package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
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
    ImageView editButtonMyPathologiesDate;
    ImageView editButtonMyPathologiesTime;
    ImageView editButtonMyPahologiesPriority;
    ImageView editButtonMyPathologiesDoctorNotes;
    Button addDrugsMyPathologies;
    LinearLayout linearLayoutDrugs;


    /**
     * Indica se il fragment è stato avviato dopo l'eliminazione di un farmaco.
     */
    Boolean deleteDrugs=false;
    /**
     * Indica se il fragment è stato avviato con un account dottore oppure no.
     */
    Boolean openDoctor=false;
    /**
     * Indica se il fragment è stato avviato con la condivisione.
     */
    Boolean share=false;



    public DetailMyPathologiesFragment() {
    }


    public static DetailMyPathologiesFragment newInstance(String param1, String param2) {
        DetailMyPathologiesFragment fragment = new DetailMyPathologiesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            deleteDrugs = getArguments().getBoolean("delete");
            openDoctor = getArguments().getBoolean("doctor");
            share = getArguments().getBoolean("share");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        editButtonMyPathologiesDate = view.findViewById(R.id.editButtonMyPathologiesDate);
        editButtonMyPathologiesTime = view.findViewById(R.id.editButtonMyPathologiesTime);
        editButtonMyPahologiesPriority = view.findViewById(R.id.editButtonMyPahologiesPriority);
        editButtonMyPathologiesDoctorNotes = view.findViewById(R.id.editButtonMyPathologiesDoctorNotes);
        addDrugsMyPathologies = view.findViewById(R.id.addDrugsMyPathologies);
        ScrollView scrollView = view.findViewById(R.id.scrollDetailMyPathologies);
        linearLayoutDrugs = view.findViewById(R.id.linearLayoutDrugs);

        // Attivo la condivisione se il fragment è stato aperto con il menu contestuale relativo alla condivisione.
        if(share==true)
        {
            showCheckboxDialogForSharePrivacy();
        }


        // Se è stato eliminato un farmaco, faccio in modo che il fragment, una volta riaperto, vada nella sezione relativa ai farmaci.
        if(deleteDrugs==true)
        {
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.scrollToDescendant(addDrugsMyPathologies);
                }
            });
        }

        getData();
        getDrugsData();
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


        editButtonMyPathologiesDoctorNotes.setOnClickListener(v->openEditDoctorNotes());
        editButtonMyPathologiesDate.setOnClickListener(v->openEditDate());
        editButtonMyPathologiesTime.setOnClickListener(v->openEditTime());
        editButtonMyPahologiesPriority.setOnClickListener(v->editPriority());
        addDrugsMyPathologies.setOnClickListener(v->addDrugs());
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
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.deleteMyPathologies)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyleCritical);

                    // Set the dialog title and message
                    builder.setTitle(getString(R.string.deleteMessageTitle))
                            .setMessage(getString(R.string.deleteMessage))
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });


                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }

                return true;
            }
        });
        toolbar.inflateMenu(R.menu.delete_menu);
        super.onResume();
    }


    private void showCheckboxDialogForSharePrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyleShare);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_pathologies, null);

        CheckBox checkBoxDate = view.findViewById(R.id.dialogShareDetailPathologiesDate);
        CheckBox checkBoxTime = view.findViewById(R.id.dialogShareDetailPathologiesTime);
        CheckBox checkBoxPriority = view.findViewById(R.id.dialogShareDetailPathologiesPriority);
        CheckBox checkBoxDoctorNotes = view.findViewById(R.id.dialogShareDetailPathologiesDoctorNotes);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
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


    private void openEditDoctorNotes()
    {
        EditDoctorNotesDialogFragment editDoctorNotesDialogFragment = EditDoctorNotesDialogFragment.newInstance();
        editDoctorNotesDialogFragment.show(getActivity().getSupportFragmentManager(), "EditDoctorNotesDialogFragment");
    }

    private void openEditDate()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme);
        Calendar currentDate = Calendar.getInstance();
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                Utility.clearTime(selectedDate);
                Utility.clearTime(currentDate);
                if(selectedDate.after(currentDate))
                {
                    Utility.showAlertDialog(getActivity(), getString(R.string.futureCalendarErrorTitle), getString(R.string.futureCalendarError));
                }

            }
        });
        datePickerDialog.show();
    }




    private void openEditTime()
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the selected time (hourOfDay and minute)
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    }
                },
                hour,
                minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()) // 24-hour format
        );
        timePickerDialog.show();

    }


    private void editPriority()
    {
        EditPriorityDialogFragment editPriorityDialogFragment = EditPriorityDialogFragment.newInstance();
        editPriorityDialogFragment.show(getActivity().getSupportFragmentManager(), "EditPriorityDialogFragment");
    }


    private void addDrugs()
    {
       AddDrugsDialogFragment addDrugsDialogFragment = AddDrugsDialogFragment.newInstance();
       addDrugsDialogFragment.show(getActivity().getSupportFragmentManager(), "AddDrugsDialogFragment");
    }


    private void getData()
    {

    }

    @SuppressLint("RestrictedApi")
    private void getDrugsData()
    {

        // Creo il constraintLayout
        ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
        Utility.activeAnimationOnClick(getActivity(), constraintLayout);
        constraintLayout.setId(View.generateViewId());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.heightHealthHistory)
        );
        layoutParams.topMargin=getResources().getDimensionPixelSize(R.dimen.marginBetweenInputs);;
        constraintLayout.setLayoutParams(layoutParams);
        constraintLayout.setBackgroundColor(getResources().getColor(R.color.appMainColorDark));
        linearLayoutDrugs.addView(constraintLayout);

        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.titillium_web_bold);


        // Creo la textView relativa al nome del farmaco
        TextView textViewTitle = new TextView(getActivity());
        textViewTitle.setText("Oki");
        textViewTitle.setId(View.generateViewId());
        ConstraintLayout.LayoutParams paramsTitle = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textViewTitle.setTypeface(typeface);
        textViewTitle.setTextColor(getResources().getColor(R.color.white));
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.textTitleHealthHistory));
        textViewTitle.setLayoutParams(paramsTitle);
        constraintLayout.addView(textViewTitle);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(textViewTitle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, getResources().getDimensionPixelSize(R.dimen.marginLeftRightDetailHealthHistory));
        constraintSet.connect(textViewTitle.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) dpToPx(getContext(), 20));
        constraintSet.applyTo(constraintLayout);


        // Creo la textView relativa alle note sul farmaco
        TextView textViewNote = new TextView(getActivity());
        textViewNote.setText("2 la mattina e 2 la sera");
        textViewNote.setId(View.generateViewId());
        ConstraintLayout.LayoutParams paramsNote = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textViewNote.setPadding(0, 0,(int) dpToPx(getContext(), 40), 0);
        textViewNote.setTextColor(getResources().getColor(R.color.white));
        textViewNote.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) dpToPx(getContext(), 13));
        textViewNote.setLayoutParams(paramsNote);
        constraintLayout.addView(textViewNote);
        ConstraintSet constraintSetNote = new ConstraintSet();
        constraintSetNote.clone(constraintLayout);
        constraintSetNote.connect(textViewNote.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, getResources().getDimensionPixelSize(R.dimen.marginLeftRightDetailHealthHistory));
        constraintSetNote.connect(textViewNote.getId(), ConstraintSet.TOP, textViewTitle.getId(), ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 3));
        constraintSetNote.applyTo(constraintLayout);



    }

}