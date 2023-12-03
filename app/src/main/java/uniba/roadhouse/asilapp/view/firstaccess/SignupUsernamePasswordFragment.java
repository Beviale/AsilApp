package uniba.roadhouse.asilapp.view.firstaccess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupUsernamePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupUsernamePasswordFragment extends Fragment {
    TextInputEditText usernameInputRegister;
    TextInputEditText passwordInputRegister;
    Button nextButton;
    ProgressBar progressBarUsername;
    TextInputLayout layoutUsernameReigster;
    ImageView usernameResult;
    TextView usernameResultText;
    LinearLayout layoutUsernameCheck;

    //
    TextInputLayout layoutPasswordReigster;
    ImageView passwordResult;
    TextView passwordResultText;
    LinearLayout layoutPasswordCheck;

    private static Boolean showNextButtonUsername=false;
    private static Boolean showNextButtonPassword=false;






    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupUsernamePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterUsernamePassword.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupUsernamePasswordFragment newInstance(String param1, String param2) {
        SignupUsernamePasswordFragment fragment = new SignupUsernamePasswordFragment();
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
        return inflater.inflate(R.layout.signup_username_password_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameInputRegister = view.findViewById(R.id.usernameInputRegister);
        passwordInputRegister = view.findViewById(R.id.passwordInputRegister);
        nextButton = getActivity().findViewById(R.id.nextButton);

        progressBarUsername = view.findViewById(R.id.progressBarUsername);
        layoutUsernameReigster = view.findViewById(R.id.layoutUsernameReigster);
        usernameResult = view.findViewById(R.id.usernameResult);
        usernameResultText = view.findViewById(R.id.usernameResultText);
        layoutUsernameCheck = view.findViewById(R.id.layoutUsernameCheck);

        layoutPasswordReigster = view.findViewById(R.id.layoutPasswordReigster);
        passwordResult = view.findViewById(R.id.passwordResult);
        passwordResultText = view.findViewById(R.id.passwordResultText);
        layoutPasswordCheck = view.findViewById(R.id.layoutPasswordCheck);

        usernameInputRegister.addTextChangedListener(textWatcherUsername);
        passwordInputRegister.addTextChangedListener(textWatcherPassword);

        nextButton.setEnabled(false);
        nextButton.setAlpha((float) (0.5));
    }


    @Override
    public void onResume() {
        super.onResume();
        if (showNextButtonUsername==true && showNextButtonPassword==true) {
            nextButton.setEnabled(true);
            nextButton.setAlpha(1);
        }
        else
        {
            nextButton.setEnabled(false);
            nextButton.setAlpha((float)0.5);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    TextWatcher textWatcherUsername = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String usernameInserted = usernameInputRegister.getText().toString();
            Log.d("stringa", usernameInserted);

            layoutUsernameCheck.setVisibility(View.GONE);
            usernameResult.setVisibility(View.GONE);
            usernameResultText.setVisibility(View.GONE);
            progressBarUsername.setVisibility(View.VISIBLE);
            if(TextUtils.isEmpty(usernameInserted))
            {
                return;
            }
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutUsernameReigster.getLayoutParams();
            params.topMargin = 0;
            CompletableFuture<Boolean> future = Dao.checkUsernameIsAvailable(usernameInserted, getActivity());
            future.thenAccept(result -> {
                getActivity().runOnUiThread(() -> {
                progressBarUsername.setVisibility(View.GONE);
                layoutUsernameCheck.setVisibility(View.VISIBLE);
                    usernameResult.setVisibility(View.VISIBLE);
                    if (result == true) {
                        showNextButtonUsername=true;
                        if(showNextButtonPassword==true)
                        {
                            nextButton.setEnabled(true);
                            nextButton.setAlpha(1);
                        }
                        usernameResultText.setVisibility(View.VISIBLE);
                        usernameResultText.setText(getString(R.string.usernameAvailable));
                        usernameResult.setImageResource(R.mipmap.verified);
                    } else {
                        showNextButtonUsername=false;
                        nextButton.setEnabled(false);
                        nextButton.setAlpha((float)0.5);
                        usernameResultText.setVisibility(View.VISIBLE);
                        usernameResultText.setText(getString(R.string.userAlreadyExists));
                        usernameResult.setImageResource(R.mipmap.error);
                    }
                });
            });

        }
    };






    TextWatcher textWatcherPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String passwordInserted = passwordInputRegister.getText().toString();
            layoutPasswordCheck.setVisibility(View.VISIBLE);
            passwordResult.setVisibility(View.VISIBLE);
            passwordResultText.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutPasswordReigster.getLayoutParams();
            params.topMargin = 0;
            if(checkRegexPassword(passwordInserted)==true)
            {
                passwordResultText.setText(getString(R.string.passwordRegexOk));
                passwordResult.setImageResource(R.mipmap.verified);
                showNextButtonPassword=true;
                if(showNextButtonUsername==true)
                {
                    nextButton.setEnabled(true);
                    nextButton.setAlpha(1);
                }
            }
            else
            {
                Utility.textViewUnderlineText(passwordResultText,getString(R.string.passwordRegexError));
                passwordResult.setClickable(true);
                passwordResultText.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        Utility.showAlertDialog(getActivity(), getString(R.string.passwordExplantationTitle), getString(R.string.passwordExplanation));
                    }
                });
                passwordResult.setImageResource(R.mipmap.error);
                showNextButtonPassword=false;
                nextButton.setEnabled(false);
                nextButton.setAlpha((float)0.5);
            }

        }
    };

    private static Boolean checkRegexPassword(String password)
    {
       return Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", password);
    }
}