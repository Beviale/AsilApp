package uniba.roadhouse.asilapp.view.firstaccess;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.CountryService;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Country;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupPlaceFragment extends Fragment {
    AutoCompleteTextView citizenSelection;
    AutoCompleteTextView countrySelection;
    AutoCompleteTextView typeUserSelection;
    Button nextButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupPlaceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupDomicile.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupPlaceFragment newInstance(String param1, String param2) {
        SignupPlaceFragment fragment = new SignupPlaceFragment();
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
        return inflater.inflate(R.layout.signup_place_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressBar progressBar = getActivity().findViewById(R.id.progressBarFirstActivity);
        LinearLayout layoutSignupFragment = getActivity().findViewById(R.id.layoutSignupFragment);
        citizenSelection = view.findViewById(R.id.citizenSelection);
        countrySelection = view.findViewById(R.id.countrySelection);
        typeUserSelection = view.findViewById(R.id.typeUserSelection);
        nextButton = getActivity().findViewById(R.id.nextButton);
        if (allFieldsEmpty()) {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)(0.5));
        }

        citizenSelection.addTextChangedListener(textWatcher);
        countrySelection.addTextChangedListener(textWatcher);
        typeUserSelection.addTextChangedListener(textWatcher);


        List<String> typeUserString = new ArrayList<String>();
        typeUserString.add(getString(R.string.asylumUser));
        typeUserString.add(getString(R.string.internationalUser));
        ArrayAdapter<String> adapterTypeUser = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, typeUserString);
        typeUserSelection.setAdapter(adapterTypeUser);




        CountryService countryService = Country.RetrofitInstance.getRetrofitInstance().create(CountryService.class);
        Call<List<Country>> call = countryService.getAllCountries();
        progressBar.setVisibility(View.VISIBLE);
        layoutSignupFragment.setAlpha((float) 0.5);


        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful()) {
                    List<Country> countryList = response.body();
                    List<String> countryListString = new ArrayList<String>();
                    countryListString.add(getString(R.string.nullValue).toUpperCase());
                    for (Country country : countryList) {
                        countryListString.add(country.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, countryListString);
                    citizenSelection.setAdapter(adapter);
                    countrySelection.setAdapter(adapter);

                }
                else
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();

                    if(!Utility.isConnectedToInternet(getActivity()))
                    {
                        Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
                        FirstAccessActivity.dialogConnection=true;

                    }
                    else
                        Utility.showAlertDialog(getActivity(), getString(R.string.serverErrorTitle), getString(R.string.serverError));
                }
                progressBar.setVisibility(View.GONE);
                layoutSignupFragment.setAlpha((float) 1.0);
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                progressBar.setVisibility(View.GONE);
                layoutSignupFragment.setAlpha((float) 1.0);
                if(!Utility.isConnectedToInternet(getActivity()))
                {
                    FirstAccessActivity.dialogConnection=true;
                    Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
                }
                else
                    Utility.showAlertDialog(getActivity(), getString(R.string.serverErrorTitle), getString(R.string.serverError));

            }
        });

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
            if (!allFieldsEmpty()) {
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


    private boolean allFieldsEmpty(){
        boolean empty= (typeUserSelection.getText().toString().trim().isEmpty() ||
                citizenSelection.getText().toString().trim().isEmpty() ||
                countrySelection.getText().toString().trim().isEmpty());
        return empty;
    }
}
