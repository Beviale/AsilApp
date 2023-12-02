package uniba.roadhouse.asilapp.view.firstaccess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import uniba.roadhouse.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupOrganizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupOrganizationFragment extends Fragment {
    AutoCompleteTextView cityOrganizationSelection;
    AutoCompleteTextView nameOrganizationSelection;
    Button nextButton;

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
        if (allFieldsEmpty()) {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)(0.5));
        }
        cityOrganizationSelection.addTextChangedListener(textWatcher);
        nameOrganizationSelection.addTextChangedListener(textWatcher);
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
        boolean empty= (cityOrganizationSelection.getText().toString().trim().isEmpty() ||
                nameOrganizationSelection.getText().toString().trim().isEmpty());
        return empty;
    }
}