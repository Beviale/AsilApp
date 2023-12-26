package uniba.roadhouse.asilapp.controller.doctor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Patologia;

/**
 * Schermata che consente al dottore di inserire una nuova patologia associata ad un utente.
 */
public class NewPathologyFragment extends Fragment {
    /**
     * Layout del campo di testo per l'inserimento del nome della patologia.
     */
    TextInputLayout nameNewPathologyLayout;
    /**
     * Campo di testo per l'inserimento del nome della patologia.
     */
    TextInputEditText nameNewPathologyInput;
    /**
     * Layout del campo di testo per l'inserimento della data dell'ultima visita.
     */
    TextInputLayout dateNewPathologyLayout;
    /**
     * AutoCompleteTextView per l'inserimento della data dell'ultima visita.
     */
    AutoCompleteTextView dateNewPathologyInput;
    /**
     * Layout del campo di testo per l'inserimento dell'orario dell'ultima visita.
     */
    TextInputLayout timeNewPathologyLayout;
    /**
     * AutoCompleteTextView per l'inserimento dell'orario dell'ultima visita.
     */
    AutoCompleteTextView timeNewPathologyInput;
    /**
     * RadioGroup contenente le opzioni selezionabili per la priorità della patologia.
     */
    RadioGroup priorityNewMyPathologiesRadioGroup;
    /**
     * RadioButton che rappresenta una priorità bassa.
     */
    RadioButton myNewPathologiesLow;
    /**
     * RadioButton che rappresenta una prioprità media.
     */
    RadioButton myNewPathologiesMedium;
    /**
     * RadioButton che rappresenta una priorità elevata.
     */
    RadioButton myNewPathologiesHigh;
    /**
     * Campo di testo per l'inserimento delle note associate alla patologia.
     */
    EditText doctorNotesNewPathology;
    /**
     * Button che consente l'aggiunta della patologia.
     */
    Button buttonAddNewPathology;
    /**
     * ProgressBar da mostrare durante la memorizza della patologia nel database.
     */
    ProgressBar progressBar;
    /**
     * Layout relativo all'intero fragment.
     */
    ConstraintLayout layoutNewPathology;




    public NewPathologyFragment() {
    }


    public static NewPathologyFragment newInstance(String param1, String param2) {
        NewPathologyFragment fragment = new NewPathologyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_pathology, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //--------------RIFERIMENTI--------------------
        nameNewPathologyLayout = view.findViewById(R.id.nameNewPathologyLayout);
        nameNewPathologyInput = view.findViewById(R.id.nameNewPathologyInput);
        layoutNewPathology = view.findViewById(R.id.layoutNewPathology);
        progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        dateNewPathologyLayout = view.findViewById(R.id.dateNewPathologyLayout);
        dateNewPathologyInput = view.findViewById(R.id.dateNewPathologyInput);
        timeNewPathologyLayout = view.findViewById(R.id.timeNewPathologyLayout);
        timeNewPathologyInput = view.findViewById(R.id.timeNewPathologyInput);
        priorityNewMyPathologiesRadioGroup = view.findViewById(R.id.priorityNewMyPathologiesRadioGroup);
        myNewPathologiesLow = view.findViewById(R.id.myNewPathologiesLow);
        myNewPathologiesMedium = view.findViewById(R.id.myNewPathologiesMedium);
        myNewPathologiesHigh = view.findViewById(R.id.myNewPathologiesHigh);
        buttonAddNewPathology = view.findViewById(R.id.buttonAddNewPathology);
        doctorNotesNewPathology = view.findViewById(R.id.doctorNotesNewPathology);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        //---------------LISTENER-----------------
        dateNewPathologyInput.setOnClickListener(v->selectDate());
        dateNewPathologyLayout.setEndIconOnClickListener(v->selectDate());
        timeNewPathologyInput.setOnClickListener(v->selectTime());
        timeNewPathologyLayout.setEndIconOnClickListener(v->selectTime());
        buttonAddNewPathology.setOnClickListener(v->addNewPathology());
        super.onStart();
    }

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
    }



    /**
     * Consente la selezione della data dell'ultima visita. Viene utilizzato un DatePickerDialog.
     */
    private void selectDate()
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
                    dateNewPathologyInput.setText("");
                }
                else
                {
                    dateNewPathologyInput.setText(new StringBuilder().append(dayOfMonth).append("/").append(month + 1).append("/").append(year));
                }
            }
        });
        datePickerDialog.show();
    }



    /**
     * Consente la selezione dell'orario dell'ultima visita. Apre un TimePickerDialog.
     */
    private void selectTime()
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        timeNewPathologyInput.setText(selectedTime);
                    }
                },
                hour,
                minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()) // 24-hour format
        );
        timePickerDialog.show();
    }


    /**
     * Memorizza nel database la nuova patologia.
     */
    private void addNewPathology()
    {
        if(nameNewPathologyInput.getText().toString().isEmpty())
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.emptyNameNewPathologyTitle), getString(R.string.emptyNameNewPathology));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutNewPathology.setAlpha((float)0.5);
        String namePathologyAdd = nameNewPathologyInput.getText().toString();
        String priorityAdd="-";
        if(priorityNewMyPathologiesRadioGroup.getCheckedRadioButtonId()==-1)
        {
            priorityAdd = getString(R.string.emptyPriority);
        }
        else if(priorityNewMyPathologiesRadioGroup.getCheckedRadioButtonId() == R.id.myNewPathologiesLow)
        {
            priorityAdd = getString(R.string.priorityMyPathologiesValueLow);
        }
        else if(priorityNewMyPathologiesRadioGroup.getCheckedRadioButtonId() == R.id.myNewPathologiesMedium)
        {
            priorityAdd = getString(R.string.priorityMyPathologiesValueMedium);
        }
        else if(priorityNewMyPathologiesRadioGroup.getCheckedRadioButtonId() == R.id.myNewPathologiesHigh)
        {
            priorityAdd = getString(R.string.priorityMyPathologiesValueHigh);
        }
        String dateAdd = "-";
        if(dateNewPathologyInput.getText().toString().isEmpty())
        {
            dateAdd = getString(R.string.emptyDate);
        }
        else
        {
            dateAdd = dateNewPathologyInput.getText().toString();
        }
        String timeAdd = "-";
        if(timeNewPathologyInput.getText().toString().isEmpty())
        {
            timeAdd = getString(R.string.emptyTime);
        }
        else
        {
            timeAdd = timeNewPathologyInput.getText().toString();
        }
        String noteAdd = "-";
        if(doctorNotesNewPathology.getText().toString().isEmpty())
        {
            noteAdd = getString(R.string.emptyDoctorNotes);
        }
        else
        {
            noteAdd = doctorNotesNewPathology.getText().toString();
        }
        Patologia patologia = new Patologia(AccessUser.getUsername(), namePathologyAdd, priorityAdd, dateAdd, timeAdd, noteAdd);
        CompletableFuture<String> future = Dao.storePatology(patologia,getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutNewPathology.setAlpha((float)1.0);
                Toast.makeText(getActivity(),result, Toast.LENGTH_SHORT).show();
                if(result.equals(getString(R.string.insertPatologySuccessfull)))
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("backMyPathologies", true);
                    fragmentTransaction.replace(R.id.doctorFragmentView, DetailUserDoctorFragment.class, bundle);
                    fragmentTransaction.commit();
                }
            });
        });

    }


    /**
     * Apre il fragment "DetailUserDoctor" quando viene premuto il tasto indietro.
     */
    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
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