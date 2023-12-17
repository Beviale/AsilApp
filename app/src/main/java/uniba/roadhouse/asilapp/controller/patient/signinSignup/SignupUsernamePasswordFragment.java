package uniba.roadhouse.asilapp.controller.patient.signinSignup;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * Fragment di compilazione di registrazione.
 * Permette l'inserimento delle credenziali di accessi quali username e password.
 */
public class SignupUsernamePasswordFragment extends Fragment {
    //--------------WIDGET USERNAME----------------------------
    /**
     * TextInputEditText relativo all'inserimento dell'username
     */
    TextInputEditText usernameInputSignup;
    /**
     * ProgressBar da mostrare mentre il DB verifica se esiste già un username uguale a quello inserito dall'utente.
     */
    ProgressBar progressBarUsername;
    /**
     * Layout relativo al campo username
     */
    TextInputLayout layoutUsernameSignup;
    /**
     * Icona che indica il risultato dell'username inserito (se è disponibile oppure no).
     */
    ImageView usernameResultImage;
    /**
     * Testo che indica il risultato dell'username inserito (se è disponibile oppure no
     */
    TextView usernameResultText;
    /**
     * Layout che contiene i risultati dell'username inserito sia come immagine che come testo.
     */
    LinearLayout layoutUsernameCheck;


    //--------------WIDGET PASSWORD----------------------------

    /**
     * Layout relativo all'inserimento della password
     */
    TextInputLayout layoutPasswordSignup;
    /**
     * Icona che indica il risultato della password inserita (se rispetta i criteri di sicurezza oppure no).
     */
    ImageView passwordResultImage;
    /**
     * TextInputEditText relativo all'inserimento della password.
     */
    TextInputEditText passwordInputSignup;
    /**
     * Testo che indica il risultato della password inserita (se rispetta i criteri di sicurezza oppure no).
     */
    TextView passwordResultText;
    /**
     * Layout che contiene i risultati della password inserita sia come immagine che come testo.
     */
    LinearLayout layoutPasswordCheck;






    /**
     * Bottone che permette di terminare la registrazione.
     */
    Button nextButton;



    /**
     * Indica che il campo username è compilato correttamente.
     */
    private static Boolean showNextButtonUsername=false;
    /**
     * Indica che il campo password è compilato correttamente.
     */
    private static Boolean showNextButtonPassword=false;






    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupUsernamePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterUsernamePassword.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupUsernamePasswordFragment newInstance(String param1, String param2) {
        SignupUsernamePasswordFragment fragment = new SignupUsernamePasswordFragment();
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
        return inflater.inflate(R.layout.signup_username_password_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //----------RIFERIMENTI----------------
        usernameInputSignup = view.findViewById(R.id.usernameInputSignup);
        passwordInputSignup = view.findViewById(R.id.passwordInputSignup);
        nextButton = getActivity().findViewById(R.id.nextButtonSignup);
        progressBarUsername = view.findViewById(R.id.progressBarUsernameSignup);
        layoutUsernameSignup = view.findViewById(R.id.layoutUsernameSignup);
        usernameResultImage = view.findViewById(R.id.usernameResultImageSignup);
        usernameResultText = view.findViewById(R.id.usernameResultTextSignup);
        layoutUsernameCheck = view.findViewById(R.id.layoutUsernameCheckSignup);
        layoutPasswordSignup = view.findViewById(R.id.layoutPasswordSignup);
        passwordResultImage = view.findViewById(R.id.passwordResultImageSignup);
        passwordResultText = view.findViewById(R.id.passwordResultTextSignup);
        layoutPasswordCheck = view.findViewById(R.id.layoutPasswordCheckSignup);


        // Aggiunto i rispettivi TextWatcher
        usernameInputSignup.addTextChangedListener(textWatcherUsername);
        passwordInputSignup.addTextChangedListener(textWatcherPassword);

        // Inizialmente il nextButton viene disattivato.
        nextButton.setEnabled(false);
        nextButton.setAlpha((float) (0.5));
    }



    @Override
    public void onStart() {
        super.onStart();
        //-----------LISTENER------------------
        usernameInputSignup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false)
                    checkUsername();
            }
        });

        passwordResultText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(passwordResultText.getText().toString().equals(getString(R.string.passwordRegexError)))
                    showInfoPasswordSecurity();
            }
        });

        passwordResultImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Drawable drawableResult = ContextCompat.getDrawable(getActivity(), R.mipmap.error);
                if(passwordResultImage.getDrawable().getConstantState().equals(drawableResult.getConstantState()))
                    showInfoPasswordSecurity();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        // Se sia il campo username che password sono compilati correttamente attivo il nextButton, altrimento lo disattivo.
        if (showNextButtonUsername==true && showNextButtonPassword==true) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1);
        }
        else
        {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)0.5);
        }

    }


    /**
     * Mostra un dialog che spiega all'utente quali sono i criteri di sicurezza per le password.
     */
    private void showInfoPasswordSecurity()
    {
        Utility.showAlertDialog(getActivity(), getString(R.string.passwordExplantationTitle), getString(R.string.passwordExplanation));
    }

    /**
     * Chiama il DB e verifica se l'username già esiste.
     * Fornisce il risultato all'utente.
     */
    private void checkUsername()
    {
        String usernameInserted = usernameInputSignup.getText().toString();
        layoutUsernameCheck.setVisibility(View.GONE);
        usernameResultImage.setVisibility(View.GONE);
        usernameResultText.setVisibility(View.GONE);
        progressBarUsername.setVisibility(View.VISIBLE);
        if(TextUtils.isEmpty(usernameInserted))
        {
            return;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutUsernameSignup.getLayoutParams();
        params.topMargin = 0;
        CompletableFuture<Boolean> future = Dao.checkUsernameIsAvailable(usernameInserted, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBarUsername.setVisibility(View.GONE);
                layoutUsernameCheck.setVisibility(View.VISIBLE);
                usernameResultImage.setVisibility(View.VISIBLE);
                if (result == true) {
                    showNextButtonUsername=true;
                    if(showNextButtonPassword==true)
                    {
                        nextButton.setEnabled(true);
                        nextButton.setAlpha(1);
                    }
                    usernameResultText.setVisibility(View.VISIBLE);
                    usernameResultText.setText(getString(R.string.usernameAvailable));
                    usernameResultImage.setImageResource(R.mipmap.verified);
                } else {
                    showNextButtonUsername=false;
                    nextButton.setEnabled(false);
                    nextButton.setAlpha((float)0.5);
                    usernameResultText.setVisibility(View.VISIBLE);
                    usernameResultText.setText(getString(R.string.userAlreadyExists));
                    usernameResultImage.setImageResource(R.mipmap.error);
                }
            });
        });

    }








    TextWatcher textWatcherPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Chiama checkRegexPassword();
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s) {
            String passwordInserted = passwordInputSignup.getText().toString();
            layoutPasswordCheck.setVisibility(View.VISIBLE);
            passwordResultImage.setVisibility(View.VISIBLE);
            passwordResultText.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutPasswordSignup.getLayoutParams();
            params.topMargin = 0;
            if(Utility.checkRegexPassword(passwordInserted)==true)
            {
                passwordResultText.setText(getString(R.string.passwordRegexOk));
                passwordResultImage.setImageResource(R.mipmap.verified);
                showNextButtonPassword=true;
                if(showNextButtonUsername==true)
                {
                    nextButton.setEnabled(true);
                    nextButton.setAlpha(1);
                }
            }
            else
            {
                Utility.textViewUnderlineText(passwordResultText,getString(R.string.passwordRegexError));
                passwordResultImage.setClickable(true);
                passwordResultImage.setImageResource(R.mipmap.error);
                showNextButtonPassword=false;
                nextButton.setEnabled(false);
                nextButton.setAlpha((float)0.5);
            }

        }
    };




    TextWatcher textWatcherUsername = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)0.5);

        }
    };
}