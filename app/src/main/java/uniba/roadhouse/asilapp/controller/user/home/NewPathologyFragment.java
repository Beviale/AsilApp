package uniba.roadhouse.asilapp.controller.user.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;


public class NewPathologyFragment extends Fragment {
    TextInputLayout nameNewPathologyLayout;
    TextInputEditText nameNewPathologyInput;
    TextInputLayout dateNewPathologyLayout;
    AutoCompleteTextView dateNewPathologyInput;
    TextInputLayout timeNewPathologyLayout;
    AutoCompleteTextView timeNewPathologyInput;
    RadioGroup priorityNewMyPathologiesRadioGroup;
    RadioButton myNewPathologiesLow;
    RadioButton myNewPathologiesMedium;
    RadioButton myNewPathologiesHigh;
    EditText doctorNotesNewPathology;
    Button buttonAddNewPathology;




    public NewPathologyFragment() {
    }


    public static NewPathologyFragment newInstance(String param1, String param2) {
        NewPathologyFragment fragment = new NewPathologyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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


    private void addNewPathology()
    {
        if(nameNewPathologyInput.getText().toString().isEmpty())
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.emptyNameNewPathologyTitle), getString(R.string.emptyNameNewPathology));
            return;
        }

    }


}