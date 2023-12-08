package uniba.roadhouse.asilapp.view.home;

import static android.content.Context.MODE_APPEND;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
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
        exitAccountButton = view.findViewById(R.id.exitAccountButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        settingsLayout = view.findViewById(R.id.settingsLayout);
        valueRatingApp = view.findViewById(R.id.valueRatingApp);
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        Float ratingSharedPref = sh.getFloat("ratingApp", (float)0);
        ratingApp.setRating(ratingSharedPref);
        valueRatingApp.setText(String.valueOf(ratingApp.getRating()));
        homeActivityProgressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        editProfileButton.setEnabled(false);
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        settingsLayout.setAlpha((float)0.5);
        getData();

    }


    @Override
    public void onStart() {
        super.onStart();
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
    }

    private void getData()
    {
        CompletableFuture<Map<String, Object>> future = Dao.getUserData(Access.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                homeActivityProgressBar.setVisibility(View.GONE);
                settingsLayout.setAlpha((float)1);
                if(result!=null)
                    Log.d("aa", String.valueOf(result.size()));
            });
        });
    }

    private void exitAccount()
    {
        Dao.logOutUser(getActivity());
        Intent intent = new Intent(getActivity(), SigninSingupActivity.class);
        startActivity(intent);
    }
}