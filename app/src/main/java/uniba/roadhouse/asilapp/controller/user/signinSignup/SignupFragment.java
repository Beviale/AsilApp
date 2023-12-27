package uniba.roadhouse.asilapp.controller.user.signinSignup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.UserSignup;

/**
 * Fragment relativo alla schermata principale di registrazione.
 * */
public class SignupFragment extends Fragment {
    /**
     * Button che permette di passare da un fragment di compilazione di registrazione a un altro.
     */
    Button nextButton;
    /**
     * ProgressBar da mostrare mentre l'utente sta effettuando la registrazione con tutti i campi compilati.
     */
    ProgressBar progressBar;

    /**
     * Lista di tutti i fragment di compilazione di registrazione.
     */
    private ArrayList<Class> screenFragments=new ArrayList<>(List.of(new Class[]{SignupNameSurnameFragment.class, SignupPlaceOriginFragment.class, SignupOrganizationFragment.class,  SignupUsernamePasswordFragment.class}));



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
         // Riferimento del bottone che consente di passare da un fragment di compilazione di registrazione a un altro
        nextButton=view.findViewById(R.id.nextButtonSignup);
        // Riferimento alla progressBar che viene visualizzata durante l'ultimo step di registrazione.
        progressBar = getActivity().findViewById(R.id.progressBarSigninSignupActivity);


        // Si passa al primo fragment di compilazione di registrazione, ovvero quello relativo all'inserimento dei dati personali (nome, cognome, etc..)..
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.signupSwitchView, SignupNameSurnameFragment.class, null);
        fragmentTransaction.commit();
    }


    @Override
    public void onStart() {
        super.onStart();
        //---------LISTENER-------------
        nextButton.setOnClickListener(v->nextScreen());
    }

    /**
     * Salva i dati dell'attuale fragment di compilazione di registrazione (chiamando saveData()) e apre il successivo seguendo l'ordine della lista "screenFragments".
     */
    private void nextScreen(){
        //prendo la classe dell'attuae fragment aperto
        Class currentScreen=getActivity().getSupportFragmentManager().findFragmentById(R.id.signupSwitchView).getClass();
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
            fragmentTransaction.replace(R.id.signupSwitchView, screenFragments.get(currentScreenNumber+1), null);
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
            case "class uniba.roadhouse.asilapp.controller.user.signinSignup.SignupNameSurnameFragment":
                TextInputEditText nameInputRegister = getActivity().findViewById(R.id.nameInputSignup);
                TextInputEditText surnameInputRegister = getActivity().findViewById(R.id.surnameInputSignup);
                AutoCompleteTextView genderSelection = getActivity().findViewById(R.id.genderSelectionSignup);
                AutoCompleteTextView birthDateSelection = getActivity().findViewById(R.id.birtDateSelectionSignup);
                UserSignup.setName(nameInputRegister.getText().toString());
                UserSignup.setSurname(surnameInputRegister.getText().toString());
                UserSignup.setGender(genderSelection.getText().toString());
                UserSignup.setBirthDate(birthDateSelection.getText().toString());
                break;
            case "class uniba.roadhouse.asilapp.controller.user.signinSignup.SignupPlaceOriginFragment":
                AutoCompleteTextView typeUserSelection = getActivity().findViewById(R.id.typeUserSelectionSignup);
                AutoCompleteTextView citizenSelection = getActivity().findViewById(R.id.citizenSelectionSignup);
                AutoCompleteTextView countrySelection = getActivity().findViewById(R.id.countrySelectionSignup);

                UserSignup.setTypeUser(typeUserSelection.getText().toString());
                UserSignup.setCitizen(citizenSelection.getText().toString());
                UserSignup.setCountry(countrySelection.getText().toString());
                break;
            case "class uniba.roadhouse.asilapp.controller.user.signinSignup.SignupOrganizationFragment":
                AutoCompleteTextView cityOrganizationSelection = getActivity().findViewById(R.id.cityOrganizationSelectionSignup);
                AutoCompleteTextView nameOrganizationSelection = getActivity().findViewById(R.id.nameOrganizationSelectionSignup);

                UserSignup.setCityOrganization(cityOrganizationSelection.getText().toString());
                UserSignup.setNameOrganization(nameOrganizationSelection.getText().toString());
                break;
            case "class uniba.roadhouse.asilapp.controller.user.signinSignup.SignupUsernamePasswordFragment":
                TextInputEditText usernameInputRegister = getActivity().findViewById(R.id.usernameInputSignup);
                TextInputEditText passwordInputRegister = getActivity().findViewById(R.id.passwordInputSignup);


                UserSignup.setUsername(usernameInputRegister.getText().toString());
                UserSignup.setPassword(passwordInputRegister.getText().toString());
                progressBar.setVisibility(View.VISIBLE);
                CompletableFuture<String> future = Dao.registerUser(UserSignup.getUsername(), UserSignup.getPassword(), UserSignup.getName(), UserSignup.getUsername(), UserSignup.getCitizen(), UserSignup.getGender(), UserSignup.getCountry(), UserSignup.getNameOrganization(), UserSignup.getTypeUser(), UserSignup.getBirthDate(), getActivity());
                future.thenAccept(result -> {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if(result.equals(getString(R.string.registrationComplete)))
                        {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.empty);
                            fragmentTransaction.disallowAddToBackStack();
                            fragmentTransaction.replace(R.id.signinFragmentView, SignupCompleteScreenFragment.class, null);
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