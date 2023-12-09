package uniba.roadhouse.asilapp.view.home;

import static android.content.Context.MODE_APPEND;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.api.Distribution;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.view.signinSignup.SigninSingupActivity;


public class SettingsFragment extends Fragment {
    TextInputEditText changePasswordInput;
    AutoCompleteTextView cityModify;
    AutoCompleteTextView nameOrganizationModify;
    Button editProfileButton;
    ProgressBar homeActivityProgressBar;
    ConstraintLayout settingsLayout;
    RatingBar ratingApp;
    TextView valueRatingApp;
    Button exitAccountButton;
    LinearLayout layoutPasswordCheckModify;
    ImageView passwordResultImageModify;
    TextView passwordResultTextModify;



    public SettingsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //----------RIFERIMENTI-----------------
        changePasswordInput = view.findViewById(R.id.changePasswordInput);
        cityModify = view.findViewById(R.id.cityModify);
        nameOrganizationModify = view.findViewById(R.id.nameOrganizationModify);
        ratingApp = view.findViewById(R.id.ratingApp);
        layoutPasswordCheckModify = view.findViewById(R.id.layoutPasswordCheckModify);
        passwordResultImageModify = view.findViewById(R.id.passwordResultImageModify);
        passwordResultTextModify = view.findViewById(R.id.passwordResultTextModify);
        exitAccountButton = view.findViewById(R.id.exitAccountButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        settingsLayout = view.findViewById(R.id.settingsLayout);
        valueRatingApp = view.findViewById(R.id.valueRatingApp);
        homeActivityProgressBar = getActivity().findViewById(R.id.homeActivityProgressBar);


        // Recupero la valutazione dell'app se già precedentemente memorizzata.
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        Float ratingSharedPref = sh.getFloat("ratingApp", (float)0);
        ratingApp.setRating(ratingSharedPref);
        valueRatingApp.setText(String.valueOf(ratingApp.getRating()));


        editProfileButton.setEnabled(false);
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        settingsLayout.setAlpha((float)0.5);
        getData();

    }


    @Override
    public void onStart() {
        super.onStart();
        //-----------------LISTENER--------------
        ratingApp.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
               valueRatingApp.setText(String.valueOf(rating));
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putFloat("ratingApp", rating);
                myEdit.commit();
            }
        });

        exitAccountButton.setOnClickListener(v->exitAccount());
        changePasswordInput.addTextChangedListener(textWatcherPassword);
        passwordResultTextModify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(passwordResultTextModify.getText().toString().equals(getString(R.string.passwordRegexError)))
                    showInfoPasswordSecurity();
            }
        });


        passwordResultImageModify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Drawable drawableResult = ContextCompat.getDrawable(getActivity(), R.mipmap.error);
                if(passwordResultImageModify.getDrawable().getConstantState().equals(drawableResult.getConstantState()))
                    showInfoPasswordSecurity();
            }
        });
    }


    /**
     *
     */
    private void getData()
    {
        CompletableFuture<Map<String, Object>> future = Dao.getUserData(Access.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                homeActivityProgressBar.setVisibility(View.GONE);
                settingsLayout.setAlpha((float)1);
                if(result!=null) {
                    //nameOrganizationModify.setText(result.get("nomeResidenza").toString());
                }
            });
        });
    }


    /**
     * Effettua l'uscita dall'account eliminando il JWT.
     */
    private void exitAccount()
    {
        Dao.logOutUser(getActivity());
        Intent intent = new Intent(getActivity(), SigninSingupActivity.class);
        startActivity(intent);
    }




    TextWatcher textWatcherPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Verifica in real-time se la password inserita dall'utente rispetta o meno i criteri di sicurezza.
         * @param s, password appena modificata
         */
        @Override
        public void afterTextChanged(Editable s) {
            layoutPasswordCheckModify.setVisibility(View.VISIBLE);
            passwordResultImageModify.setVisibility(View.VISIBLE);
            passwordResultTextModify.setVisibility(View.VISIBLE);
            if(Utility.checkRegexPassword(changePasswordInput.getText().toString())==true)
            {
                passwordResultTextModify.setText(getString(R.string.passwordRegexOk));
                passwordResultImageModify.setImageResource(R.mipmap.verified);
            }
            else
            {
                Utility.textViewUnderlineText(passwordResultTextModify,getString(R.string.passwordRegexError));
                passwordResultImageModify.setClickable(true);
                passwordResultImageModify.setImageResource(R.mipmap.error);
            }
        }
    };

    /**
     * Mostra un dialog che spiega all'utente quali sono i criteri di sicurezza per le password.
     */
    private void showInfoPasswordSecurity()
    {
        Utility.showAlertDialog(getActivity(), getString(R.string.passwordExplantationTitle), getString(R.string.passwordExplanation));
    }

}