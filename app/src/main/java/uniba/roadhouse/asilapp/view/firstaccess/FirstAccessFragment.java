package uniba.roadhouse.asilapp.view.firstaccess;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.view.home.HomeActivity;

/**
 * Fragment relativo alle schermata di login
 */
public class FirstAccessFragment extends Fragment {
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
     * TextView che avvia la registrazione dell'utente.
     */
    TextView registerLabel;

    /**
     * ProgressBar da mostrare durante la chiamata al database
     */
    ProgressBar progressBar;

    /**
     * Layout da oscurare durante la fase di login, mentre la progressBar è attiva.
     */
    LinearLayout layoutLogin;
    TextInputLayout usernameLayout;
    TextInputLayout passwordLayout;





    public FirstAccessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PrimoAccesso.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstAccessFragment newInstance() {
        FirstAccessFragment fragment = new FirstAccessFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.first_access_fragment, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //-------------RIFERIMENTI-------------------------

        // riferimento al campo username del login
        userNameInput = view.findViewById(R.id.userNameInput);
        // riferimento al campo password del login
        passwordInput = view.findViewById(R.id.passwordInput);
        // riferimento al bottone che avvia il login
        buttonLogin = view.findViewById(R.id.buttonLogin);
        // Riferimento alla TextView che avvia la registrazione dell'utente
        registerLabel = view.findViewById(R.id.registerLabel);
        // Riferimento alla ProgressBar da mostrare durante la chiamata al database
        progressBar = view.findViewById(R.id.progressBarFirstActivity);
        // Riferimento al layout da oscurare duranta la chiamata al database.
        layoutLogin = view.findViewById(R.id.layoutLogin);
        usernameLayout = view.findViewById(R.id.usernameLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);

        //funzione che sottilinea il testo di registrazione
        Utility.textViewUnderlineText(registerLabel, getString(R.string.loginRegistrationLabel));
    }



    @Override
    public void onStart() {
        super.onStart();

        //------LISTENER----------------------
        registerLabel.setOnClickListener(v->callRegisterFragment());
        buttonLogin.setOnClickListener(v->login());
    }


    /**
     * Avvia il login dell'utente. Se va a buon fine apre la HomeActivity.
     */
    private void login(){
        usernameLayout.setBoxStrokeColor(getContext().getColor(R.color.appMainColor));
        usernameLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.appMainColor)));
        passwordLayout.setBoxStrokeColor(getContext().getColor(R.color.appMainColor));
        passwordLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.appMainColor)));
        if (!Utility.isConnectedToInternet(getActivity())) {
            FirstAccessActivity.dialogConnection = true;
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
        CompletableFuture<String> future = Dao.loginUser(userNameInput.getText().toString(), passwordInput.getText().toString(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                layoutLogin.setAlpha(1);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();

                if(result==getString(R.string.loginCompleted))
                {
                    Access.setUsername(userNameInput.getText().toString());
                    Intent openHome = new Intent(getActivity(), HomeActivity.class);
                    startActivity(openHome);
                }
                else if (result==getString(R.string.noUserExists))
                {
                    usernameLayout.setBoxStrokeColor(getContext().getColor(R.color.textAlertColor));
                    usernameLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.textAlertColor)));
                    usernameLayout.requestFocus();
                }
                else if (result==getString(R.string.wrongPassword))
                {
                    passwordLayout.setBoxStrokeColor(getContext().getColor(R.color.textAlertColor));
                    passwordLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.textAlertColor)));
                    passwordLayout.requestFocus();
                }

            });

        });




    }


    /**
     * Se la connessione è assente, mostra un dialog.
     * Se la connessione è presente, apre il fragment di registrazione.
     */
    private void callRegisterFragment(){
        if(!Utility.isConnectedToInternet(getActivity())) {
            FirstAccessActivity.dialogConnection = true;
            Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
        }
        else
        {
            //prendo l'activity parent e richiamo il metodo per sostituire il fragment di login con quello di registrazione
            ((FirstAccessActivity) getActivity()).callRegisterFragment();
        }
    }
}