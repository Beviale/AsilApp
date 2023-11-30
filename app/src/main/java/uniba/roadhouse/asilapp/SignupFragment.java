package uniba.roadhouse.view;

<<<<<<< HEAD
=======
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
>>>>>>> parent of f959be8 (Schermate di registrazione)
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD

import java.util.ArrayList;
import java.util.List;

import uniba.roadhouse.asilapp.R;
=======
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
>>>>>>> parent of f959be8 (Schermate di registrazione)

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

<<<<<<< HEAD
    //lista delle classi dei fragment che rappresenteranno le schermate dell'app
    private ArrayList<Class> screenFragments=new ArrayList<>(List.of(new Class[]{SignupNameSurnameFragment.class, SignupUsernamePasswordFragment.class}));
=======
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
>>>>>>> parent of f959be8 (Schermate di registrazione)

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        return inflater.inflate(R.layout.signup_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
<<<<<<< HEAD
        fragmentTransaction.add(R.id.registerSwitchView, SignupNameSurnameFragment.class, null);
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
            fragmentTransaction.replace(R.id.primoAccessoFragmentView, SignupCompleteScreenFragment.class, null);
            fragmentTransaction.commit();
            Log.d("COMPLETE","Reg Complete");
        }

    }

=======
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.registerSwitchView, RegisterNameUsername.class, null);
        fragmentTransaction.commit();
    }

>>>>>>> parent of f959be8 (Schermate di registrazione)

}