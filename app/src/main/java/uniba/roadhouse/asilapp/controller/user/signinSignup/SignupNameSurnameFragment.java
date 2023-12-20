package uniba.roadhouse.asilapp.controller.user.signinSignup;

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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import uniba.roadhouse.asilapp.R;

/**
 * Fragment di compilazione di registrazione.
 * Permette l'inserimento dei dati personali quali nome, cognome, sesso e data di nascita.
 */
public class SignupNameSurnameFragment extends Fragment {
    /**
     * Input relativo all'inserimento del nome.
     */
    TextInputEditText nameInputSingup;
    /**
     * Input relativo all'inserimento del cognome.
     */
    TextInputEditText surnameInputSignup;
    /**
     * AutoCompletTextView relativo alla selezione del sesso.
     */
    AutoCompleteTextView genderSelectionSignup;
    /**
     * AutoCompleteTextView relativo alla selezione della data di nascita.
     */
    AutoCompleteTextView birthDateSelectionSignup;
    /**
     * Bottone che consente il passaggio al fragment di compilazione di registrazione successivo.
     */
    Button nextButton;
    /**
     * Layout relativo all'inserimento della data di nascita.
     */
    TextInputLayout birthDateSelectionLayout;





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

        //-----------RIFERIMENTI----------------
        nameInputSingup = view.findViewById(R.id.nameInputSignup);
        surnameInputSignup = view.findViewById(R.id.surnameInputSignup);
        genderSelectionSignup = view.findViewById(R.id.genderSelectionSignup);
        birthDateSelectionSignup = view.findViewById(R.id.birtDateSelectionSignup);
        nextButton = getActivity().findViewById(R.id.nextButtonSignup);
        birthDateSelectionLayout = view.findViewById(R.id.birtDateSelectionLayoutSignup);


        // Aggiungo il TextWatcher
        nameInputSingup.addTextChangedListener(textWatcher);
        surnameInputSignup.addTextChangedListener(textWatcher);
        genderSelectionSignup.addTextChangedListener(textWatcher);
        birthDateSelectionSignup.addTextChangedListener(textWatcher);

        // Disattivo il pulsante di next se almeno uno dei campi risulta vuoto.
        if (atLeastOneFieldIsEmpty()) {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)(0.5));
        }
    }




    @Override
    public void onStart() {
        super.onStart();

        // -----------------LISTENER----------------------
        // Avvio del dialog di selezione della data di nascita.
        birthDateSelectionSignup.setOnClickListener(v->showDatePickerDialog(birthDateSelectionSignup));
        birthDateSelectionLayout.setEndIconOnClickListener(v-> showDatePickerDialog(birthDateSelectionSignup));
    }



    @Override
    public void onResume() {
        super.onResume();
        // Attivo il pulsante se tutti i campi sono pieni.
        if (!atLeastOneFieldIsEmpty()) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1);
        }
        // Riempio l'AutoCompeleteTextView relativo all'inserimento del sesso.
        String[] gender = {getString(R.string.male), getString(R.string.female), getString(R.string.otherGender)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, gender);
        genderSelectionSignup.setAdapter(adapter);

    }

    // Fa selezionare all'utente una data di nascita e la scrive nella AutoCompleteTextView passata in input.
    private void showDatePickerDialog(AutoCompleteTextView birthDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme);
        Calendar currentDate = Calendar.getInstance();
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                int age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR);
                // Verifica se l'anniversario è già avvenuto quest'anno
                if (currentDate.get(Calendar.DAY_OF_YEAR) < selectedDate.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                if(age<18)
                {
                    Toast.makeText(getActivity(),getString(R.string.ageLess18), Toast.LENGTH_SHORT).show();
                    birthDate.setText("");

                    return;
                }

                birthDate.setText(new StringBuilder().append(dayOfMonth).append("/").append(month + 1).append("/").append(year));

            }
        });
        datePickerDialog.show();
    }



    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Attiva o disattiva il pulsante di next.
         * @param s testo appena modificato.
         */
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

    /**
     * Verifica se almeno un campo di compilazione è vuoto.
     * @return true se almeno un campo è vuoto, false altrimenti.
     */
    private boolean atLeastOneFieldIsEmpty(){
        boolean empty= (nameInputSingup.getText().toString().trim().isEmpty() ||
                surnameInputSignup.getText().toString().trim().isEmpty() ||
                genderSelectionSignup.getText().toString().trim().isEmpty() ||
                birthDateSelectionSignup.getText().toString().trim().isEmpty());
        return empty;
    }


}
