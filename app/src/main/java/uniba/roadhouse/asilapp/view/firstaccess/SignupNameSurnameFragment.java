package uniba.roadhouse.asilapp.view.firstaccess;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import uniba.roadhouse.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupNameSurnameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupNameSurnameFragment extends Fragment {
    TextInputEditText nameInputRegister;
    TextInputEditText surnameInputRegister;
    AutoCompleteTextView genderSelection;
    AutoCompleteTextView birthDateSelection;
    Button nextButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupNameSurnameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterNameUsername.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupNameSurnameFragment newInstance(String param1, String param2) {
        SignupNameSurnameFragment fragment = new SignupNameSurnameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signup_name_surname_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameInputRegister = getActivity().findViewById(R.id.nameInputRegister);
        surnameInputRegister = getActivity().findViewById(R.id.surnameInputRegister);
        // Popolazione dell'AutocompleteTextView relativo alla scelta del sesso
        genderSelection = view.findViewById(R.id.genderSelection);
        String[] gender = {getString(R.string.male), getString(R.string.female), getString(R.string.otherGender)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, gender);
        genderSelection.setAdapter(adapter);


        // AutocompelteTextView relativo alla data di nascita
        birthDateSelection = view.findViewById(R.id.birtDateSelection);
        TextInputLayout birthDateSelectionLayout = view.findViewById(R.id.birtDateSelectionLayout);

        birthDateSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(birthDateSelection);

            }
        });

        birthDateSelectionLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(birthDateSelection);

            }
        });

        nextButton = getActivity().findViewById(R.id.nextButton);
        nameInputRegister.addTextChangedListener(textWatcher);
        surnameInputRegister.addTextChangedListener(textWatcher);
        genderSelection.addTextChangedListener(textWatcher);
        birthDateSelection.addTextChangedListener(textWatcher);
        if (atLeastOneFieldIsEmpty()) {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)(0.5));
        }



    }


    // Fa selezionare all'utente una data di nascita e la scrive nella AutoCompleteTextView passata in input.
    private void showDatePickerDialog(AutoCompleteTextView bornDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                bornDate.setText(new StringBuilder().append(dayOfMonth).append("/").append(month + 1).append("/").append(year));

            }
        });
        datePickerDialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!atLeastOneFieldIsEmpty()) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1);
        }

    }



    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!atLeastOneFieldIsEmpty()) {
                nextButton.setEnabled(true);
                nextButton.setAlpha(1);
            }
            else
            {
                nextButton.setEnabled(false);
                nextButton.setAlpha((float)(0.5));
            }
        }

    };


    private boolean atLeastOneFieldIsEmpty(){
        boolean empty= (nameInputRegister.getText().toString().trim().isEmpty() ||
                surnameInputRegister.getText().toString().trim().isEmpty() ||
                genderSelection.getText().toString().trim().isEmpty() ||
                birthDateSelection.getText().toString().trim().isEmpty());
        return empty;
    }


}
