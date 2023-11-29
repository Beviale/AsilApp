package uniba.roadhouse.asilapp;

import android.content.Intent;
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
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    //lista delle classi dei fragment che rappresenteranno le schermate dell'app
    private ArrayList<Class> screenFragments=new ArrayList<>(List.of(new Class[]{RegisterNameSurname.class, RegisterUsernamePassword.class}));

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
    public void onResume() {
        super.onResume();
        getView().findViewById(R.id.nextButton).setOnClickListener(v->nextScreen());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.registerSwitchView, RegisterNameSurname.class, null);
        fragmentTransaction.commit();
    }

    private void nextScreen(){
        //prendo la classe dell'attuae fragment aperto
        Class currentScreen=getActivity().getSupportFragmentManager().findFragmentById(R.id.registerSwitchView).getClass();
        //prendo il numero di chermata che esso rappresenta dalla lista dei fragment che compongono le schermate
        Integer currentScreenNumber=screenFragments.indexOf(currentScreen);
        Log.d("ll",currentScreenNumber.toString());

        //se non è l'ultima schermata
        if(currentScreenNumber<screenFragments.size()-1){
            //apro il fragment in rappresentanza della schermata successiva.
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(currentScreen.getName());
            fragmentTransaction.replace(R.id.registerSwitchView, screenFragments.get(currentScreenNumber+1), null);
            fragmentTransaction.commit();
        }

        //se ho premuto "Prossimo" mentre è aperta l'ultima schermata, allora ho completato la registrazione e mostro a posto del
        //fragment attuale, nell'activity Main, il fragment che mostra la scritta di Registrazione Completata
        if(currentScreenNumber==screenFragments.size()-1){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.primoAccessoFragmentView, RegisterCompleteScreen.class, null);
            fragmentTransaction.commit();
            Log.d("COMPLETE","Reg Complete");
        }

    }


}