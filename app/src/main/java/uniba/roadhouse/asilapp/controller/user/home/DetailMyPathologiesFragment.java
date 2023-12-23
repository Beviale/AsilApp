package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.doctor.DetailUserDoctorFragment;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Patologia;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailMyPathologiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailMyPathologiesFragment extends Fragment {
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

    ProgressBar progressBar;
    ConstraintLayout layoutMyPathologies;

    /**
     * Nome della patologia passata in input al fragment.
     */
    private static String namePathology;


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

    private static View drugsClicked;



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
            namePathology = getArguments().getString("namePathology");
        }
        if(openDoctor==true)
        {
            getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallbackDoctor);
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
        layoutMyPathologies = view.findViewById(R.id.layoutMyPathologies);
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
        if(openDoctor==false)
        {
            progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        }
        if(openDoctor==true)
        {
            editButtonMyPathologiesDate.setVisibility(View.VISIBLE);
            editButtonMyPathologiesTime.setVisibility(View.VISIBLE);
            editButtonMyPahologiesPriority.setVisibility(View.VISIBLE);
            editButtonMyPathologiesDoctorNotes.setVisibility(View.VISIBLE);
            addDrugsMyPathologies.setVisibility(View.VISIBLE);
            progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        }

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
        if(openDoctor==false)
        {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        }
        if(openDoctor==true)
        {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);
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
        }
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
        CheckBox checkBoxSelectAll = view.findViewById(R.id.dialogShareDetailPathologiesSelectAll);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(checkBoxDate);
        checkBoxes.add(checkBoxTime);
        checkBoxes.add(checkBoxPriority);
        checkBoxes.add(checkBoxDoctorNotes);
        checkBoxes.add(checkBoxSelectAll);
        Utility.colorAllCheckbox(checkBoxes, getActivity());


checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             if (checkBoxSelectAll.isChecked()) {
                 checkBoxDate.setChecked(true);
                 checkBoxTime.setChecked(true);
                 checkBoxPriority.setChecked(true);
                 checkBoxDoctorNotes.setChecked(true);
             }
             if (!checkBoxSelectAll.isChecked()) {
                 checkBoxDate.setChecked(false);
                 checkBoxTime.setChecked(false);
                 checkBoxPriority.setChecked(false);
                 checkBoxDoctorNotes.setChecked(false);
             }
         }
     });


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

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
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
        progressBar.setVisibility(View.VISIBLE);
        layoutMyPathologies.setAlpha((float)0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getPathology(namePathology, AccessUser.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutMyPathologies.setAlpha((float)1.0);
                if(!(result.get("esito").toString().equals(getString(R.string.misurationGetSuccessfully))))
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Patologia patologia = (Patologia) result.get("patologia");
                    detailMyPathologiesTitle.setText(namePathology);
                    dateLastVisitMyPathologies.setText(patologia.getData());
                    timeLastVisitMyPathologies.setText(patologia.getOra());
                    priorityLastVisitMyPathologies.setText(patologia.getPriorita());
                    doctorNotesLastVisitMyPahologies.setText(patologia.getNota());
                }
            });
        });

    }

    @SuppressLint("RestrictedApi")
    private void getDrugsData()
    {

        // Creo il constraintLayout
        ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
        registerForContextMenu(constraintLayout);
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

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        drugsClicked = v;
        inflater.inflate(R.menu.share_and_delete_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_share_menu)
        {
            //openShareDrugs();
        }
        if(item.getItemId()==R.id.action_delete_menu)
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
        return super.onContextItemSelected(item);
    }



    /**
     * Comportamento del tasto back quando il fragment è stato aperto con l'account dottore.
     */
    private OnBackPressedCallback onBackPressedCallbackDoctor = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putBoolean("backMyPathologies", true);
            fragmentTransaction.replace(R.id.doctorFragmentView, DetailUserDoctorFragment.class, bundle);
            fragmentTransaction.commit();
        }
    };
}