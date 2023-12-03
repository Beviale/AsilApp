package uniba.roadhouse.asilapp.view.firstaccess;

import android.content.Intent;
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
    LinearLayout layoutSignupFragment;





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
        userNameInput = getActivity().findViewById(R.id.userNameInput);
        // riferimento al campo password del login
        passwordInput = getActivity().findViewById(R.id.passwordInput);
        // riferimento al bottone che avvia il login
        buttonLogin = getActivity().findViewById(R.id.buttonLogin);
        // Riferimento alla TextView che avvia la registrazione dell'utente
        registerLabel = getActivity().findViewById(R.id.registerLabel);
        // Riferimento alla ProgressBar da mostrare durante la chiamata al database
        progressBar = getActivity().findViewById(R.id.progressBarFirstActivity);


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
                Toast.makeText(getActivity(),getString(R.string.usernameEmpty), Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.isEmpty()) {
                Toast.makeText(getActivity(),getString(R.string.passwordEmpty), Toast.LENGTH_SHORT).show();
                return;
            }

        }

        progressBar.setVisibility(View.VISIBLE);

        CompletableFuture<String> future = Dao.loginUser(userNameInput.getText().toString(), passwordInput.getText().toString(), getActivity());

        if(loginResult==getString(R.string.loginCompleted))
        {
            Toast.makeText(getActivity(),getString(R.string.successfulLogin), Toast.LENGTH_SHORT).show();
            Access.setUsername(userNameInput.getText().toString());
            Intent openHome = new Intent(getActivity(), HomeActivity.class);
            startActivity(openHome);
        }
        else
        {
            Toast.makeText(getActivity(),loginResult, Toast.LENGTH_SHORT).show();
        }
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