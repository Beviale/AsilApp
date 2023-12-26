package uniba.roadhouse.asilapp.controller.doctor;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.user.signinSignup.SigninSingupActivity;
import uniba.roadhouse.asilapp.model.dao.AccessDoctor;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * Schermata che consente al dottore di autenticarsi all'app.
 */
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
     * Layout dell'intero fragment.
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

    /**
     * Effettuata la chiamata al database per la verifica delle credenziali inserite dal dottore.
     * Se giuste, viene effettuato il login e salvato il JWT.
     * Se sbagliate, viene mostrato un toast contenente il tipo di errore.
     * Se non vi è connessione, viene mostrata una fienstra di dialogo.
     */
    private void login()
    {
        usernameLayout.setBoxStrokeColor(getContext().getColor(R.color.appMainColor));
        usernameLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.appMainColor)));
        passwordLayout.setBoxStrokeColor(getContext().getColor(R.color.appMainColor));
        passwordLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.appMainColor)));
        if (!Utility.isConnectedToInternet(getActivity())) {
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

        progressBar.setVisibility(View.VISIBLE);
        layoutLogin.setAlpha((float)0.5);
        CompletableFuture<Map<String,String>> future = Dao.loginDoctor(userNameInput.getText().toString(), passwordInput.getText().toString(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                layoutLogin.setAlpha(1);
                Toast.makeText(getActivity(), result.get("esito"), Toast.LENGTH_SHORT).show();
                if(result.get("esito")==getString(R.string.loginCompleted))
                {
                    AccessDoctor.setUsername(userNameInput.getText().toString());
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransactin = fragmentManager.beginTransaction();
                    fragmentTransactin.replace(R.id.doctorFragmentView, HomeDoctorFragment.class, null);
                    fragmentTransactin.commit();
                }
                else if (result.get("esito")==getString(R.string.noDoctorExists))
                {
                    usernameLayout.setBoxStrokeColor(getContext().getColor(R.color.textAlertColor));
                    usernameLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.textAlertColor)));
                    usernameLayout.requestFocus();
                }
                else if (result.get("esito")==getString(R.string.wrongPassword))
                {
                    passwordLayout.setBoxStrokeColor(getContext().getColor(R.color.textAlertColor));
                    passwordLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.textAlertColor)));
                    passwordLayout.requestFocus();
                }

            });
        });
    }


    /**
     * Inserisce le credenziali predefinite per l'accesso di default e chiama il metodo login();
     */
    private void loginDirettoProfessore()
    {
        userNameInput.setText("doctor");
        passwordInput.setText("Asilapp@1");
        login();
    }
}