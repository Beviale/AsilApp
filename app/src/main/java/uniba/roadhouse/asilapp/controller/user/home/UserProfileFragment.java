package uniba.roadhouse.asilapp.controller.user.home;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;


public class UserProfileFragment extends Fragment {

    TextView profileNameTitle;
    TextView profileName;
    TextView profileSurnameTitle;
    TextView profileSurname;
    TextView profileGenderTitle;
    TextView profileGender;
    TextView profileBirthDateTitle;
    TextView profileBirthDate;
    TextView profileCitizenTitle;
    TextView profileCitizen;
    TextView profileCountryTitle;
    TextView profileCountry;
    TextView profileResidenceTitle;
    TextView profileResidence;
    ImageView profileQRCode;
    ProgressBar progressBar;
    ConstraintLayout layoutUserProfile;

    /**
     * Indica se il fragment è stato aperto da un account dottore o meno.
     */
    private static Boolean openDoctor=false;

    Map<String, Object> DatiCorrenti;


    public UserProfileFragment() {
    }

    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments()!=null)
        {
            openDoctor = getArguments().getBoolean("doctor");
        }
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileNameTitle = view.findViewById(R.id.profileNameTitle);
        profileName = view.findViewById(R.id.profileName);
        profileSurnameTitle = view.findViewById(R.id.profileSurnameTitle);
        profileSurname = view.findViewById(R.id.profileSurname);
        profileGenderTitle = view.findViewById(R.id.profileGenderTitle);
        profileGender = view.findViewById(R.id.profileGender);
        profileBirthDateTitle = view.findViewById(R.id.profileBirthDateTitle);
        profileBirthDate = view.findViewById(R.id.profileBirthDate);
        profileCitizenTitle = view.findViewById(R.id.profileCitizenTitle);
        profileCitizen = view.findViewById(R.id.profileCitizen);
        profileCountryTitle = view.findViewById(R.id.profileCountryTitle);
        profileCountry = view.findViewById(R.id.profileCountry);
        profileResidenceTitle = view.findViewById(R.id.profileResidenceTitle);
        profileResidence = view.findViewById(R.id.profileResidence);
        profileQRCode = view.findViewById(R.id.imageQRCode);
        layoutUserProfile = view.findViewById(R.id.layoutUserProfile);
        if(openDoctor==false)
        {
            progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        }
        if(openDoctor==true)
        {
            progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        }

    }


    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        layoutUserProfile.setAlpha((float)0.5);
        CompletableFuture<Map<String, Object>> future = Dao.getUserData(Access.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                layoutUserProfile.setAlpha((float)1.0);
                progressBar.setVisibility(View.GONE);
                DatiCorrenti = result;
                profileName.setText(DatiCorrenti.get("nome").toString());
                profileSurname.setText(DatiCorrenti.get("cognome").toString());
                profileGender.setText(DatiCorrenti.get("sesso").toString());
                profileCitizen.setText(DatiCorrenti.get("cittadinanza").toString());
                profileCountry.setText(DatiCorrenti.get("paeseDiProvenienza").toString());
                profileResidence.setText(DatiCorrenti.get("nomeResidenza").toString());
                profileQRCode.setImageBitmap((Bitmap) DatiCorrenti.get("qrCode"));

            });
        });
        super.onStart();
    }


    @Override
    public void onResume() {
        if(openDoctor==false)
        {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(null);
        }
        else
        {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);
            toolbar.getMenu().clear();
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        }
        super.onResume();
    }
}