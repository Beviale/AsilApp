package uniba.roadhouse.asilapp.view.firstaccess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import uniba.roadhouse.asilapp.model.dao.Country;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupPlaceFragment extends Fragment {

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
        ProgressBar progressBar = view.findViewById(R.id.progressBarCountry);
        LinearLayout layoutSignupFragment = getActivity().findViewById(R.id.layoutSignupFragment);
        Button nextButton = getActivity().findViewById(R.id.nextButton);
        nextButton.setEnabled(false);
        AutoCompleteTextView citizenSelection = view.findViewById(R.id.citizenSelection);
        AutoCompleteTextView countrySelection = view.findViewById(R.id.countrySelection);


        CountryService countryService = Country.RetrofitInstance.getRetrofitInstance().create(CountryService.class);
        Call<List<Country>> call = countryService.getAllCountries();
        Call<List<Country>> call2 = countryService.getAllCountries();
        progressBar.setVisibility(View.VISIBLE);
        layoutSignupFragment.setAlpha((float) 0.5);


        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful()) {
                    List<Country> countryList = response.body();
                    List<String> countryListString = new ArrayList<String>();
                    for (Country country : countryList) {
                        countryListString.add(country.getName());

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, countryListString);
                    citizenSelection.setAdapter(adapter);
                    countrySelection.setAdapter(adapter);
                    progressBar.setVisibility(View.INVISIBLE);
                    layoutSignupFragment.setAlpha((float) 1.0);
                    nextButton.setEnabled(true);
                    

                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                // Handle failure
            }
        });

    }
}
