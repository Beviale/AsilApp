package uniba.roadhouse.asilapp.view.signinSignup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * Fragment di compilazione di registrazione.
 * Permette l'inserimento della città e del nome della struttura di accoglienza.
 */
public class SignupOrganizationFragment extends Fragment {
    /**
     * AutoCompelteTextView relativo alla selezione della città.
     */
    AutoCompleteTextView cityOrganizationSelection;
    /**
     * AutoCompleteTextView relativo alla selezione della struttura di accoglienza.
     */
    AutoCompleteTextView nameOrganizationSelection;
    /**
     * Bottine che permette di passare al fragmente di compilazione di registrazione successivo.
     */
    Button nextButton;
    /**
     * ProgressBar da mostrare durante le chiamate al database.
     */
    ProgressBar progressBar;
    /**
     * Layout del fragment principale di registrazione.
     */
    LinearLayout layoutFragmentSignup;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupOrganizationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupOrganizationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupOrganizationFragment newInstance(String param1, String param2) {
        SignupOrganizationFragment fragment = new SignupOrganizationFragment();
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
        return inflater.inflate(R.layout.signup_organization_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //------------RIFERIMENTI-------------
        cityOrganizationSelection = view.findViewById(R.id.cityOrganizationSelectionSignup);
        nameOrganizationSelection = view.findViewById(R.id.nameOrganizationSelectionSignup);
        nextButton = getActivity().findViewById(R.id.nextButtonSignup);
        progressBar = getActivity().findViewById((R.id.progressBarSigninSignupActivity));
        layoutFragmentSignup = getActivity().findViewById(R.id.layoutFragmentSignup);
        nameOrganizationSelection.setEnabled(false);

        // Aggiunto il TextWatcher
        cityOrganizationSelection.addTextChangedListener(textWatcher);
        nameOrganizationSelection.addTextChangedListener(textWatcher);


        // Disattivo il pulsante di next se almeno uno dei campi risulta vuoto.
        if (atLeastOneFieldIsEmpty()) {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)0.5);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!atLeastOneFieldIsEmpty()) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1);
        }
        getCityFromDB();
        if(!cityOrganizationSelection.getText().toString().isEmpty())
        {
            getNameOrganizationFromDB(cityOrganizationSelection.getText().toString());
        }
    }

    /**
     * Prende dal database tutte le città che hanno almeno una struttura di accoglienza e riempie l'AutoComplteteTextView apposita.
     */
    private void getCityFromDB()
    {
        if(!Utility.isConnectedToInternet(getActivity()))
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutFragmentSignup.setAlpha((float)0.5);
        CompletableFuture<List<String>> future = Dao.getNomiCittaResidenze();
        List<String> allCity = new ArrayList<String>();
        future.thenAccept(result -> {
            allCity.addAll(result);
            progressBar.setVisibility(View.INVISIBLE);
            layoutFragmentSignup.setAlpha(1);
            if(result.isEmpty())
            {
                Utility.showAlertDialog(getActivity(), getString(R.string.genericErrorTitle), getString(R.string.genericError));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allCity);
        cityOrganizationSelection.setAdapter(adapterCity);
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
         * @param s, città appena inserita.
         */
        @Override
        public void afterTextChanged(Editable s) {
           getNameOrganizationFromDB(s.toString());
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
     * Prende dal database tutti i nomi delle strutture di accoglienza che si trvano nelle città s e riempie l'AutoComplteteTextView apposita.
     * @param city, città inserita  dall'utente.
    */
    private void getNameOrganizationFromDB(String city)
    {
        if(!cityOrganizationSelection.getText().toString().trim().isEmpty())
        {
            nameOrganizationSelection.setEnabled(true);
            nameOrganizationSelection.requestFocus();
            List<String> allOrganization = new ArrayList<String>();
            progressBar.setVisibility (View.VISIBLE);
            layoutFragmentSignup.setAlpha((float)0.5);
            if(!Utility.isConnectedToInternet(getActivity()))
            {
                Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
            CompletableFuture<List<String>> future = Dao.getNomiResidenze(city);
            future.thenAccept(result -> {
                allOrganization.addAll(result);
                progressBar.setVisibility(View.INVISIBLE);
                layoutFragmentSignup.setAlpha(1);
                if(allOrganization.isEmpty())
                {
                    Utility.showAlertDialog(getActivity(), getString(R.string.genericErrorTitle), getString(R.string.genericError));
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }

            });
            ArrayAdapter<String> adapterOrganization = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allOrganization);
            nameOrganizationSelection.setAdapter(adapterOrganization);

        }
    }


    private boolean atLeastOneFieldIsEmpty(){
        boolean empty= (cityOrganizationSelection.getText().toString().trim().isEmpty() ||
                nameOrganizationSelection.getText().toString().trim().isEmpty());
        return empty;
    }
}