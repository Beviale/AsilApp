package uniba.roadhouse.asilapp.controller.doctor;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.patient.signinSignup.SigninSingupActivity;


public class SigninDoctorFragment extends Fragment {

    /**
     * Bottone per effettuare il login.
     */
    Button buttonLogin;
    /**
     * TextInputEditText per l'inserimento dell'username ai fini del login.
     */
    TextInputEditText userNameInput;
    /**
     * TextInputEditText per l'inserimento della password ai fini del login.
     */
    TextInputEditText passwordInput;

    /**
     * ProgressBar da mostrare durante la chiamata al database.
     */
    ProgressBar progressBar;

    /**
     * Layout da oscurare durante la fase di login, mentre la progressBar è attiva.
     */
    LinearLayout layoutLogin;
    /**
     * Layout per l'inserimento dell'username.
     */
    TextInputLayout usernameLayout;
    /**
     * Layout per l'inserimento della password.
     */
    TextInputLayout passwordLayout;

    /**
     * Bottone che permette l'accesso diretto al professore con credenziali già impostate.
     */
    Button loginProfessore;



    public SigninDoctorFragment() {
    }

    public static SigninDoctorFragment newInstance(String param1, String param2) {
        SigninDoctorFragment fragment = new SigninDoctorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin_doctor, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-------------RIFERIMENTI-------------------------

        // riferimento al campo username del login
        userNameInput = view.findViewById(R.id.usernameInputSigninDoctor);
        // riferimento al campo password del login
        passwordInput = view.findViewById(R.id.passwordInputSigninDoctor);
        // riferimento al bottone che avvia il login
        buttonLogin = view.findViewById(R.id.buttonSignin);
        // Riferimento alla ProgressBar da mostrare durante la chiamata al database
        progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        // Riferimento al layout da oscurare duranta la chiamata al database.
        layoutLogin = view.findViewById(R.id.layoutFragmentSigninDoctor);
        // Riferimento al layout del campo relativo all'inserimento dell'username.
        usernameLayout = view.findViewById(R.id.usernameLayoutSigninDoctor);
        // Riferimento al layout del campo relativo all'inserimento della password..
        passwordLayout = view.findViewById(R.id.passwordLayoutSigninDoctor);
        loginProfessore = view.findViewById(R.id.buttonProfessorSigninDoctor);
        buttonLogin = view.findViewById(R.id.buttonSigninDoctor);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        //------LISTENER----------------------
        buttonLogin.setOnClickListener(v->login());
        loginProfessore.setOnClickListener(v->loginDirettoProfessore());
        super.onStart();
    }

    private void login()
    {
        usernameLayout.setBoxStrokeColor(getContext().getColor(R.color.appMainColor));
        usernameLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.appMainColor)));
        passwordLayout.setBoxStrokeColor(getContext().getColor(R.color.appMainColor));
        passwordLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.appMainColor)));
        if (!Utility.isConnectedToInternet(getActivity())) {
            SigninSingupActivity.dialogConnection = true;
            Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitleLogin), getString(R.string.noConnectionLogin));
            return;
        }
        String username=userNameInput.getText().toString();
        String password=passwordInput.getText().toString();
        if(username.isEmpty() || password.isEmpty()) {
            if(username.isEmpty() && password.isEmpty()) {
                Toast.makeText(getActivity(),getString(R.string.usernameAndPasswordEmpty), Toast.LENGTH_SHORT).show();

                return;
            }
            if(username.isEmpty()) {
                usernameLayout.setBoxStrokeColor(getContext().getColor(R.color.textAlertColor));
                usernameLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.textAlertColor)));
                usernameLayout.requestFocus();
                Toast.makeText(getActivity(),getString(R.string.usernameEmpty), Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.isEmpty()) {
                Toast.makeText(getActivity(),getString(R.string.passwordEmpty), Toast.LENGTH_SHORT).show();
                passwordLayout.setBoxStrokeColor(getContext().getColor(R.color.textAlertColor));
                passwordLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.textAlertColor)));
                passwordLayout.requestFocus();
                return;
            }

        }

    }

    private void loginDirettoProfessore()
    {
        userNameInput.setText("asilapp");
        passwordInput.setText("Asilapp@1");
    }
}