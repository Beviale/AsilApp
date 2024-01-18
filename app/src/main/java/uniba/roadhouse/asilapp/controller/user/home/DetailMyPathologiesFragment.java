package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.doctor.AddDrugsDialogFragment;
import uniba.roadhouse.asilapp.controller.doctor.DetailUserDoctorFragment;
import uniba.roadhouse.asilapp.controller.doctor.DoctorActivity;
import uniba.roadhouse.asilapp.controller.doctor.EditDoctorNotesDialogFragment;
import uniba.roadhouse.asilapp.controller.doctor.EditPriorityDialogFragment;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Farmaco;
import uniba.roadhouse.asilapp.model.dao.Patologia;

/**
 * Schermata di dettaglio di una patologia. Può essere aperta sia tramite account utente che account dottore.
 */
public class DetailMyPathologiesFragment extends Fragment implements AddDrugsDialogFragment.closeListenerAddDrugs, EditPriorityDialogFragment.closeListenerEditPriority, EditDoctorNotesDialogFragment.closeEditDoctorNotes {
    /**
     * Titolo della schermata. Indica il nome della patologia.
     */
    TextView detailMyPathologiesTitle;
    /**
     * ScrollView dell'intera schermata.
     */
    ScrollView scrollView;
    /**
     * Data dell'ultima visita associata alla patologia.
     */
    TextView dateLastVisitMyPathologies;
    /**
     * Ora dell'ultima visita associata alla patologia.
     */
    TextView timeLastVisitMyPathologies;
    /**
     * Priorità assegnata dal medico alla patologia.
     */
    TextView priorityLastVisitMyPathologies;
    /**
     * Note assegnate dal medico per la patologia.
     */
    EditText doctorNotesLastVisitMyPahologies;
    /**
     * Icona di condivisione dati.
     */
    ImageView shareDetailMyPathologies;
    /**
     * Icona di modifica della data di ultima visita [Account dottore]
     */
    ImageView editButtonMyPathologiesDate;
    /**
     * Icona di modifica dell'ora relativa all'ultima visita [Account dottore]
     */
    ImageView editButtonMyPathologiesTime;
    /**
     * Icona di modifica della priorità della patologia [Account dottore]
     */
    ImageView editButtonMyPahologiesPriority;
    /**
     * Icona di modifica delle note del medico associate alla patologia [Account dottore]
     */
    ImageView editButtonMyPathologiesDoctorNotes;
    /**
     * Button che permette l'aggiunta di un farmaco [Account dottore]
     */
    Button addDrugsMyPathologies;
    /**
     * Layout relativa ai farmaci associati alla patologia.
     */
    LinearLayout linearLayoutDrugs;
    /**
     * ProgressBar da mostrare durante il caricamento dei dati dal database.
     */
    ProgressBar progressBar;
    /**
     * Layout dell'intero fragment.
     */
    ConstraintLayout layoutMyPathologies;
    /**
     * Scritta che avvisa l'utente che attualmente non risultano inseriti dei farmaci per la patologia.
     */
    TextView emptyDrugs;
    /**
     * Le chavi sono le view dei farmaci mentre i valori i rispettivi nomi.
     */
    HashMap<View, String> mapViewDrugName;
    /**
     * Le chiavi sono le view dei farmaci mentre i valori le rispettive note del medico.
     */
    HashMap<View, String> mapViewDoctorNotes;


    /**
     * Nome della patologia passata in input al fragment.
     */
    private String namePathology;
    /**
     * Request code per il DialogFragment di aggiunta farmaco.
     */
    private final static Integer REQUEST_CODE_ADD_DRUGS=1;
    /**
     * Request code per il DialogFragment di modifica della priorità.
     */
    private final static Integer REQUEST_CODE_EDIT_PRIORITY=2;
    /**
     * Request code per il DialogFragment di modifica delle note del medico.
     */
    private final static Integer REQUEST_CODE_EDIT_DOCTOR_NOTES=3;



    /**
     * Indica se il fragment è stato avviato dopo l'eliminazione di un farmaco.
     */
    private Boolean deleteDrugs=false;
    /**
     * Indica se il fragment è stato avviato con un account dottore oppure no.
     */
    private Boolean openDoctor=false;
    /**
     * Indica se il fragment è stato avviato per la condivisione dei dati.
     */
    private Boolean share=false;
    /**
     * Indica la view del farmaco cliccata dall'utente tramite menu contestuale.
     */
    private View drugsClicked;



    public DetailMyPathologiesFragment() {
    }


    public static DetailMyPathologiesFragment newInstance(String param1, String param2) {
        DetailMyPathologiesFragment fragment = new DetailMyPathologiesFragment();
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
        if(openDoctor==false)
        {
            getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallbackUser);
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
        emptyDrugs = view.findViewById(R.id.emptyDrugs);
        dateLastVisitMyPathologies = view.findViewById(R.id.dateLastVisitMyPathologies);
        detailMyPathologiesTitle = view.findViewById(R.id.detailMyPathologiesTitle);
        priorityLastVisitMyPathologies = view.findViewById(R.id.priorityLastVisitMyPathologies);
        shareDetailMyPathologies = view.findViewById(R.id.shareDetailMyPathologies);
        editButtonMyPathologiesDate = view.findViewById(R.id.editButtonMyPathologiesDate);
        editButtonMyPathologiesTime = view.findViewById(R.id.editButtonMyPathologiesTime);
        editButtonMyPahologiesPriority = view.findViewById(R.id.editButtonMyPahologiesPriority);
        editButtonMyPathologiesDoctorNotes = view.findViewById(R.id.editButtonMyPathologiesDoctorNotes);
        addDrugsMyPathologies = view.findViewById(R.id.addDrugsMyPathologies);
        scrollView = view.findViewById(R.id.scrollDetailMyPathologies);
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



        Utility.enableScroll(doctorNotesLastVisitMyPahologies);
        // Attivo la condivisione se il fragment è stato aperto per la condivisione dei dati.
        if(share==true)
        {
            showCheckboxDialogForSharePrivacy();
        }
        getData();
        getDrugsData(false);
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
        // Se il fragment è stato aperto con l'account dottore, permetto l'eliminazione della patologia tramite action overflow.
        if(openDoctor==true)
        {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.delete_menu);
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
                                        progressBar.setVisibility(View.VISIBLE);
                                        layoutMyPathologies.setAlpha((float)0.5);
                                        CompletableFuture<String> future = Dao.deletePatology(UserLogin.getUsername(), namePathology, getActivity());
                                        future.thenAccept(result -> {
                                            getActivity().runOnUiThread(() -> {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                layoutMyPathologies.setAlpha((float)1.0);
                                                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                                if(result.equals(getString(R.string.editPatologySuccessfull)))
                                                {
                                                    getActivity().onBackPressed();
                                                }
                                            });
                                        });

                                    }
                                });
                        // Create and show the AlertDialog
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    return true;
                }
            });
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
    }


    /**
     * Mostra la finestra di dialogo per la condivisione dei dati relativi alla patologia.
     * Permette all'utente/dottore di selezionare singolarmente gli elementi da condivididere.
     */
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

    /**
     * Apre il DialogFragment relativo alla modifica delle note del medico [Account dottore]
     */
    private void openEditDoctorNotes()
    {
        EditDoctorNotesDialogFragment editDoctorNotesDialogFragment = EditDoctorNotesDialogFragment.newInstancePathology(UserLogin.getUsername(), namePathology);
        editDoctorNotesDialogFragment.show(getActivity().getSupportFragmentManager(), "EditDoctorNotesDialogFragment");
        editDoctorNotesDialogFragment.setTargetFragment(this, REQUEST_CODE_EDIT_DOCTOR_NOTES);

    }


    /**
     * Permette la modifica della data dell'ultima visita mediante un DatePickerDialog [Account Dottore]
     */
    private void openEditDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme);
        Calendar currentDate = Calendar.getInstance();
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                Utility.clearTime(selectedDate);
                Utility.clearTime(currentDate);
                // Se la data inserita è posteriore a quella attuale, mostro un errore con una finestra di dialogo.
                if (selectedDate.after(currentDate)) {
                    Utility.showAlertDialog(getActivity(), getString(R.string.futureCalendarErrorTitle), getString(R.string.futureCalendarError));
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String newDate = dateFormat.format(selectedDate.getTime());
                CompletableFuture<String> future = Dao.editPatologyDate(UserLogin.getUsername(), namePathology, newDate, getActivity());
                future.thenAccept(result -> {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        getData();

                    });
                });
            }
        });
        datePickerDialog.show();
    }



    /**
     * Permette la modifica dell'orario dell'ultima visita mediante un TimePickerDialog [Account Dottore]
     */
    private void openEditTime()
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        progressBar.setVisibility(View.GONE);
                        CompletableFuture<String> future = Dao.editPatologyHour(UserLogin.getUsername(), namePathology, selectedTime, getActivity());
                        future.thenAccept(result -> {
                            getActivity().runOnUiThread(() -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                getData();
                            });
                        });
                    }
                },
                hour,
                minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()) // 24-hour format
        );
        timePickerDialog.show();

    }


    /**
     * Apre il DialogFragment relativo alla modifica della priorità della patologia.
     */
    private void editPriority()
    {
        EditPriorityDialogFragment editPriorityDialogFragment = EditPriorityDialogFragment.newInstance(UserLogin.getUsername(), namePathology);
        editPriorityDialogFragment.show(getActivity().getSupportFragmentManager(), "EditPriorityDialogFragment");
        editPriorityDialogFragment.setTargetFragment(this, REQUEST_CODE_EDIT_PRIORITY);

    }


    /**
     * Apre il DialogFragment relativo all'aggiunta di un nuovo farmaco associato alla patologia.
     */
    private void addDrugs()
    {
       AddDrugsDialogFragment addDrugsDialogFragment = AddDrugsDialogFragment.newInstance(namePathology);
       addDrugsDialogFragment.show(getActivity().getSupportFragmentManager(), "AddDrugsDialogFragment");
       addDrugsDialogFragment.setTargetFragment(this, REQUEST_CODE_ADD_DRUGS);

    }

    /**
     * Prende del database tutti i dati relativi alla patologia e setta tutte le varie View.
     */
    private void getData()
    {
        progressBar.setVisibility(View.VISIBLE);
        layoutMyPathologies.setAlpha((float)0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getPathology(namePathology, UserLogin.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                try{
                    progressBar.setVisibility(View.GONE);
                    layoutMyPathologies.setAlpha((float)1.0);
                    // Se è stato eliminato un farmaco, faccio in modo che il fragment, una volta riaperto, vada nella sezione relativa ai farmaci.
                    if(deleteDrugs==true)
                    {
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.smoothScrollTo(0, 999999);
                            }
                        });
                    }
                    if(!(result.get("esito").toString().equals(getString(R.string.misurationGetSuccessfully))))
                    {
                        Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Patologia patologia = (Patologia) result.get("patologia");
                        detailMyPathologiesTitle.setText(namePathology);
                        if(patologia.getData().isEmpty()){
                            dateLastVisitMyPathologies.setText(getString(R.string.emptyDate));
                        }
                        else{
                            dateLastVisitMyPathologies.setText(patologia.getData());
                        }
                        if(patologia.getOra().isEmpty()){
                            timeLastVisitMyPathologies.setText(getString(R.string.emptyTime));
                        }else{
                            timeLastVisitMyPathologies.setText(patologia.getOra());
                        }
                        priorityLastVisitMyPathologies.setText(Utility.convertPriorityEnumToString(getActivity(), patologia.getPriorita()));
                        if(patologia.getNota().isEmpty()){
                            doctorNotesLastVisitMyPahologies.setText(R.string.emptyDoctorNotes);
                        }else{
                            doctorNotesLastVisitMyPahologies.setText(patologia.getNota());
                        }
                    }
                }catch (Exception e) {
                    if(openDoctor==false)
                    {
                        Activity activity = new HomeActivity();
                        activity.onBackPressed();
                    }
                    if(openDoctor==true)
                    {
                        Activity activity = new DoctorActivity();
                        activity.onBackPressed();
                    }

                }
            });
        });
    }


    /**
     * Prende dal database tutti i dati relativi ai farmaci creando delle View dinamicamente.
     * @param scroll, se true, la scrollview del fragment deve essere automaticamente portata in basso.
     */
    @SuppressLint("RestrictedApi")
    private void getDrugsData(Boolean scroll)
    {
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutDrugs.setAlpha((float)0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getAllFarmaci(UserLogin.getUsername(), namePathology, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                try{
                    progressBar.setVisibility(View.INVISIBLE);
                    linearLayoutDrugs.setAlpha((float)1.0);
                    if(!(result.get("esito").toString().equals(getString(R.string.getFarmaciSuccessfull))))
                    {
                        Toast.makeText(getActivity(),result.get("esito").toString(), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        ArrayList<Farmaco> farmaci = (ArrayList<Farmaco>)result.get("farmaci");
                        mapViewDrugName = new HashMap<View, String>();
                        mapViewDoctorNotes = new HashMap<View, String>();
                        for(Farmaco farmaco: farmaci)
                        {
                            // Nasscondo la scritta che indica l'assenza di farmaci
                            emptyDrugs.setVisibility(View.GONE);
                            // Creo il constraintLayout
                            ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
                            registerForContextMenu(constraintLayout);
                            mapViewDrugName.put(constraintLayout, farmaco.getNome());
                            mapViewDoctorNotes.put(constraintLayout, farmaco.getNota());
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
                            textViewTitle.setText(farmaco.getNome());
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
                            textViewNote.setText(farmaco.getNota());
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
                    // Porto la scrollview del fragment in basso.
                    if(scroll==true)
                    {
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.smoothScrollTo(0, 999999);
                            }
                        });
                    }
                }catch (Exception e){
                    if(openDoctor==false)
                    {
                        Activity activity = new HomeActivity();
                        activity.onBackPressed();
                    }
                    if(openDoctor==true)
                    {
                        Activity activity = new DoctorActivity();
                        activity.onBackPressed();
                    }
                }
            });
        });
    }

    /**
     * Menu contestuale relativoa alla pressione prolungata della view di un farmaco.
     * L'account utente può solo condividere i dati di un farmaco, mentre l'account dottore può anche rimuovere un farmaco.
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        drugsClicked = v;
        // Con l'account utente è possibile esclusivamente condividere i dati del farmaco.
        if(openDoctor==false)
            inflater.inflate(R.menu.share_menu, menu);
        // Con l'account dottore è possibile sia condividere i dati che eliminare un farmaco.
        if(openDoctor==true)
            inflater.inflate(R.menu.share_and_delete_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_share_menu || item.getItemId()==R.id.action_share)
        {
            openShareDrugs();
        }
        if(item.getItemId()==R.id.action_delete_menu)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyleCritical);

            builder.setTitle(getString(R.string.deleteMessageTitle))
                    .setMessage(getString(R.string.deleteMessage))
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressBar.setVisibility(View.VISIBLE);
                            CompletableFuture<String> future = Dao.deleteFarmaco(UserLogin.getUsername(), namePathology, mapViewDrugName.get(drugsClicked).toString(), getActivity());
                            future.thenAccept(result -> {
                                getActivity().runOnUiThread(() -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("doctor", true);
                                    bundle.putString("namePathology", namePathology);
                                    bundle.putBoolean("delete", true);
                                    fragmentTransaction.replace(R.id.doctorFragmentView, DetailMyPathologiesFragment.class, bundle);
                                    fragmentTransaction.commit();
                                });
                            });

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onContextItemSelected(item);
    }



    /**
     * Comportamento del tasto back quando il fragment viene aperto con l'account dottore.
     * Avvisa DetailUserDoctorFragment che deve selezionare il tab relativo alle patologie.
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

    /**
     * Comportamento del tasto back quando il fragment viene aperto con l'account utente.
     * Avvisa MedicalParametersFragment che deve selezionare il tab relativo alle patologie.
     */
    private OnBackPressedCallback onBackPressedCallbackUser = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putBoolean("backMyPathologies", true);
            fragmentTransaction.replace(R.id.homeContainerView, MedicalParametersFragment.class, bundle);
            fragmentTransaction.addToBackStack(getString(R.string.healthMenuScreen));
            fragmentTransaction.commit();
        }
    };


    /**
     * Si attiva alla chiusura del DialogFragment di aggiunta farmaco.
     */
    @Override
    public void closeAddDrugs() {
        linearLayoutDrugs.removeAllViews();
        getDrugsData(true);
    }

    /**
     * Consente la condivisione dei dati di un singolo famrmaco associato alla patologia.
     */
    private  void openShareDrugs()
    {
        String share="";
        share = getString(R.string.nameAddDrugPlaceholder).concat(": ").concat(mapViewDrugName.get(drugsClicked).toString().concat("\n"));
        share = share.concat(getString(R.string.noteAddDrugPlaceholder)).concat(": ").concat(mapViewDoctorNotes.get(drugsClicked).toString());
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, share);
        startActivity(intent);
    }


    /**
     * Si attiva alla chiusura del DialogFragment relativo alla modifica della priorità della patologia.
     */
    @Override
    public void closeEditPriority() {
        getData();
    }

    /**
     * Si attiva alla chiusura del DialogFragment relativo alla modifica delle note del medico associate alla patologia.
     */
    @Override
    public void closeEditDoctorNotes() {
        getData();
    }
}