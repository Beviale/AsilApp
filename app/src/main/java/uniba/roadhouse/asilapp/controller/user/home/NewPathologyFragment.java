package uniba.roadhouse.asilapp.controller.user.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import uniba.roadhouse.asilapp.R;


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
        super.onViewCreated(view, savedInstanceState);
    }
}