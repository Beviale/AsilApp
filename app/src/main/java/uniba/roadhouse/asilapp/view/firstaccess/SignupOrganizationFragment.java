package uniba.roadhouse.asilapp.view.firstaccess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okio.Timeout;
import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupOrganizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupOrganizationFragment extends Fragment {
    AutoCompleteTextView cityOrganizationSelection;
    AutoCompleteTextView nameOrganizationSelection;
    Button nextButton;
    ProgressBar progressBar;
    LinearLayout layoutOrganizationFragment;

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
        cityOrganizationSelection = getActivity().findViewById(R.id.cityOrganizationSelection);
        nameOrganizationSelection = getActivity().findViewById(R.id.nameOrganizationSelection);
        nextButton = getActivity().findViewById(R.id.nextButton);
        progressBar = getActivity().findViewById((R.id.progressBarFirstActivity));
        layoutOrganizationFragment = getActivity().findViewById(R.id.layoutOrganizationFragment);
        nameOrganizationSelection.setEnabled(false);

        cityOrganizationSelection.addTextChangedListener(textWatcher);
        nameOrganizationSelection.addTextChangedListener(textWatcher);

        if(!Utility.isConnectedToInternet(getActivity()))
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutOrganizationFragment.setAlpha((float)0.5);
        CompletableFuture<List<String>> future = Dao.getNomiCittaResidenze();
        List<String> allCity = new ArrayList<String>();
        future.thenAccept(result -> {
            allCity.addAll(result);
            progressBar.setVisibility(View.INVISIBLE);
            layoutOrganizationFragment.setAlpha(1);
            if(result.isEmpty())
            {
                Utility.showAlertDialog(getActivity(), getString(R.string.genericErrorTitle), getString(R.string.genericError));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allCity);
        cityOrganizationSelection.setAdapter(adapterCity);
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
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!cityOrganizationSelection.getText().toString().trim().isEmpty())
            {
                nameOrganizationSelection.setEnabled(true);
                nameOrganizationSelection.requestFocus();
                List<String> allOrganization = new ArrayList<String>();
                progressBar.setVisibility (View.VISIBLE);
                layoutOrganizationFragment.setAlpha((float)0.5);
                if(!Utility.isConnectedToInternet(getActivity()))
                {
                    Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }
                allOrganization=Dao.getNomiResidenze(cityOrganizationSelection.getText().toString());
                if(allOrganization.isEmpty())
                {
                    Utility.showAlertDialog(getActivity(), getString(R.string.genericErrorTitle), getString(R.string.genericError));
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }
                progressBar.setVisibility(View.GONE);
                layoutOrganizationFragment.setAlpha(1);
                ArrayAdapter<String> adapterOrganization = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allOrganization);
                nameOrganizationSelection.setAdapter(adapterOrganization);

            }
            if (!atLeastOneFieldIsEmpty()) {
                nextButton.setEnabled(true);
                nextButton.setAlpha(1);
            }
        }

    };


    private boolean atLeastOneFieldIsEmpty(){
        boolean empty= (cityOrganizationSelection.getText().toString().trim().isEmpty() ||
                nameOrganizationSelection.getText().toString().trim().isEmpty());
        return empty;
    }
}