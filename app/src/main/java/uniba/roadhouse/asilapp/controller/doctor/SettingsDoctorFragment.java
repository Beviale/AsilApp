
package uniba.roadhouse.asilapp.controller.doctor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.FirstActivity;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * Schermata impostazione dell'account Dottore.
 */
public class SettingsDoctorFragment extends Fragment {


    /**
     * RatingBar per la valutazione complessiva dell'app da parte del dottore.
     */
    RatingBar ratingApp;
    /**
     * Testo che rappresenta in formato numerico la valutazione complessiva dell'app da parte del dottore.
     */
    TextView valueRatingApp;
    /**
     * Botton che consente l'uscita dall'account corrente.
     */
    Button exitAccountButton;
    /**
     * Toolbar dell'activity.
     */
    Toolbar toolbar;





    public SettingsDoctorFragment() {
        // Required empty public constructor
    }


    public static SettingsDoctorFragment newInstance(String param1, String param2) {
        SettingsDoctorFragment fragment = new SettingsDoctorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-------------RIFERIMENTI------------
        valueRatingApp = view.findViewById(R.id.valueRatingAppDoctor);
        ratingApp = view.findViewById(R.id.ratingAppDoctor);
        exitAccountButton = view.findViewById(R.id.exitAccountButtonDoctor);
        toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);

        // Recupero la valutazione dell'app se già precedentemente memorizzata. Utilizzo le Shared Prefeences.
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        Float ratingSharedPref = sh.getFloat("ratingAppDoctor", (float)0);
        ratingApp.setRating(ratingSharedPref);
        valueRatingApp.setText(String.valueOf(ratingApp.getRating()));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        //---------------LISTENER---------------
        exitAccountButton.setOnClickListener(v->exitAcctount());
        ratingApp.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // Memorizzo in locale la nuova valutazione utilizzando le Shared Preferences.
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valueRatingApp.setText(String.valueOf(rating));
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putFloat("ratingAppDoctor", rating);
                myEdit.commit();
            }
        });
        super.onStart();
    }


    @Override
    public void onResume() {
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
        super.onResume();
    }

    /**
     * Consente l'uscita dall'account Dottore corrente.
     * Apre FirstActivity.
     */
    private void exitAcctount()
    {
        Dao.logOutUser(getActivity());
        Intent intent = new Intent(getActivity(), FirstActivity.class);
        startActivity(intent);
    }


}