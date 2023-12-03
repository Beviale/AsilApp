package uniba.roadhouse.asilapp.view.firstaccess;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.User;

/**
 * Fragment relativo alla schermata principale di registrazione.
 * */
public class SignupFragment extends Fragment {
    /**
     * Button che permette di passare da un fragment di compilazione di registrazione a un altro.
     */
    Button nextButton;

    /**
     * Lista di tutti i fragment di compilazione di registrazione.
     */
    private ArrayList<Class> screenFragments=new ArrayList<>(List.of(new Class[]{SignupNameSurnameFragment.class, SignupPlaceFragment.class, SignupOrganizationFragment.class,  SignupUsernamePasswordFragment.class}));



    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signup_fragment, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //------------RIFERIMENTI-------
        /**
         * Riferimento del bottone che consente di passare da un fragment di compilazione di registrazione a un altro
         */
        nextButton=view.findViewById(R.id.nextButton);



        // Si passa al primo fragment di compilazione di registrazione, ovvero quello relativo all'inserimento dei dati personali (nome, cognome, etc..)..
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.registerSwitchView, SignupNameSurnameFragment.class, null);
        fragmentTransaction.commit();
    }


    @Override
    public void onStart() {
        super.onStart();
        //---------LISTENER-------------
        nextButton.setOnClickListener(v->nextScreen());
    }

    /**
     * Salva i dati dell'attuale fragment di compilazione di registrazione e apre il successivo seguendo l'ordine della lista "screenFragments".
     */
    @SuppressLint("SuspiciousIndentation")
    private void nextScreen(){
        //prendo la classe dell'attuae fragment aperto
        Class currentScreen=getActivity().getSupportFragmentManager().findFragmentById(R.id.registerSwitchView).getClass();
        // Salvo i dati inseriti nel fragment di compilazione di registrazione attuale.
        saveData(currentScreen);
        //prendo il numero di chermata che esso rappresenta dalla lista dei fragment che compongono le schermate
        Integer currentScreenNumber=screenFragments.indexOf(currentScreen);

        //se non è l'ultima schermata
        if(currentScreenNumber<screenFragments.size()-1){
            //apro il fragment in rappresentanza della schermata successiva.
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(currentScreen.getName());
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            fragmentTransaction.replace(R.id.registerSwitchView, screenFragments.get(currentScreenNumber+1), null);
            fragmentTransaction.commit();
        }
    }



    /**
     * Salva i dati inseriti nel fragment di compilazione di registrazione attuale.
     * Utilizza la classe singleton "User".
     * @param currentScreen fragment attuale.
     */
    private void saveData(Class currentScreen)
    {
        switch (currentScreen.toString())
        {
            case "class uniba.roadhouse.asilapp.view.firstaccess.SignupNameSurnameFragment":
                TextInputEditText nameInputRegister = getActivity().findViewById(R.id.nameInputRegister);
                TextInputEditText surnameInputRegister = getActivity().findViewById(R.id.surnameInputRegister);
                AutoCompleteTextView genderSelection = getActivity().findViewById(R.id.genderSelection);
                AutoCompleteTextView birthDateSelection = getActivity().findViewById(R.id.birtDateSelection);

                User.setName(nameInputRegister.getText().toString());
                User.setSurname(surnameInputRegister.getText().toString());
                User.setGender(genderSelection.getText().toString());
                User.setBirthDate(birthDateSelection.getText().toString());
                break;
            case "class uniba.roadhouse.asilapp.view.firstaccess.SignupPlaceFragment":
                AutoCompleteTextView typeUserSelection = getActivity().findViewById(R.id.typeUserSelection);
                AutoCompleteTextView citizenSelection = getActivity().findViewById(R.id.citizenSelection);
                AutoCompleteTextView countrySelection = getActivity().findViewById(R.id.countrySelection);

                User.setTypeUser(typeUserSelection.getText().toString());
                User.setCitizen(citizenSelection.getText().toString());
                User.setCountry(countrySelection.getText().toString());
                break;
            case "class uniba.roadhouse.asilapp.view.firstaccess.SignupOrganizationFragment":
                AutoCompleteTextView cityOrganizationSelection = getActivity().findViewById(R.id.cityOrganizationSelection);
                AutoCompleteTextView nameOrganizationSelection = getActivity().findViewById(R.id.nameOrganizationSelection);

                User.setCityOrganization(cityOrganizationSelection.getText().toString());
                User.setNameOrganization(nameOrganizationSelection.getText().toString());
                break;
            case "class uniba.roadhouse.asilapp.view.firstaccess.SignupUsernamePasswordFragment":
                TextInputEditText usernameInputRegister = getActivity().findViewById(R.id.usernameInputRegister);
                TextInputEditText passwordInputRegister = getActivity().findViewById(R.id.passwordInputRegister);


                User.setUsername(usernameInputRegister.getText().toString());
                User.setPassword(passwordInputRegister.getText().toString());
                ProgressBar progressBar = getActivity().findViewById(R.id.progressBarFirstActivity);
                progressBar.setVisibility(View.VISIBLE);
                CompletableFuture<String> future = Dao.registerUser(User.getUsername(), User.getPassword(), User.getName(), User.getUsername(), User.getCitizen(), User.getGender(), User.getCountry(), User.getNameOrganization(), User.getTypeUser(), getActivity());
                future.thenAccept(result -> {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if(result.equals(getString(R.string.registrationComplete)))
                        {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.empty);
                            fragmentTransaction.disallowAddToBackStack();
                            fragmentTransaction.replace(R.id.primoAccessoFragmentView, SignupCompleteScreenFragment.class, null);
                            fragmentTransaction.commit();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                break;
        }

    }
}