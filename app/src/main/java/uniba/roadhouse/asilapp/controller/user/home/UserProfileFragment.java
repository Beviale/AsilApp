package uniba.roadhouse.asilapp.controller.user.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
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
    TextView profileDoctorTitle;
    TextView profileDoctor;

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
        profileDoctor = view.findViewById(R.id.profileDoctor);
        profileDoctorTitle = view.findViewById(R.id.profileDoctorTitle);
        if(openDoctor==false)
        {
            progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        }
        if(openDoctor==true)
        {
            progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        }
        layoutUserProfile.setAlpha((float)0.5);


    }


    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        CompletableFuture<Map<String, Object>> future = Dao.getUserData(AccessUser.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                layoutUserProfile.setAlpha((float)1.0);
                progressBar.setVisibility(View.GONE);
                DatiCorrenti = result;
                profileName.setText(DatiCorrenti.get("nome").toString());
                profileSurname.setText(DatiCorrenti.get("cognome").toString());
                profileGender.setText(DatiCorrenti.get("sesso").toString());
                profileBirthDate.setText((DatiCorrenti.get("dataNascita").toString()));
                profileCitizen.setText(DatiCorrenti.get("cittadinanza").toString());
                profileCountry.setText(DatiCorrenti.get("paeseDiProvenienza").toString());
                profileResidence.setText(DatiCorrenti.get("nomeResidenza").toString());
                profileQRCode.setImageBitmap((Bitmap) DatiCorrenti.get("qrCode"));
                profileDoctor.setText(DatiCorrenti.get("dottore").toString());

            });
        });
        super.onStart();
    }


    @Override
    public void onResume() {
        Toolbar toolbar;
        if(openDoctor==false)
        {
            toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(null);
        }
        else
        {
            toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        }
        toolbar.inflateMenu(R.menu.share_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openDialogShareUserProfile();
                return true;
            }
        });
        super.onResume();
    }

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
    }


    private void openDialogShareUserProfile()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyleShare);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_user_profile, null);
        CheckBox checkBoxName = view.findViewById(R.id.dialogShareUserProfileName);
        CheckBox checkBoxSurname = view.findViewById(R.id.dialogShareUserProfileSurname);
        CheckBox checkBoxGender = view.findViewById(R.id.dialogShareUserProfileGender);
        CheckBox checkBoxBirthDate = view.findViewById(R.id.dialogShareUserBirthDate);
        CheckBox checkBoxCititzen = view.findViewById(R.id.dialogShareUserProfileCitizen);
        CheckBox checkBoxCountry = view.findViewById(R.id.dialogShareUserProfileCountry);
        CheckBox checkBoxOrganization = view.findViewById(R.id.dialogShareUserProfileOrganization);
        CheckBox checkBoxDoctor = view.findViewById(R.id.dialogShareUserProfileDoctor);
        CheckBox checkBoxSelectAll = view.findViewById(R.id.dialogShareUserProfileSelectAll);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(checkBoxName);
        checkBoxes.add(checkBoxSurname);
        checkBoxes.add(checkBoxGender);
        checkBoxes.add(checkBoxBirthDate);
        checkBoxes.add(checkBoxCititzen);
        checkBoxes.add(checkBoxCountry);
        checkBoxes.add(checkBoxOrganization);
        checkBoxes.add(checkBoxDoctor);
        checkBoxes.add(checkBoxSelectAll);
        Utility.colorAllCheckbox(checkBoxes, getActivity());

        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   if(checkBoxSelectAll.isChecked())
                   {
                       checkBoxName.setChecked(true);
                       checkBoxSurname.setChecked(true);
                       checkBoxGender.setChecked(true);
                       checkBoxBirthDate.setChecked(true);
                       checkBoxCititzen.setChecked(true);
                       checkBoxCountry.setChecked(true);
                       checkBoxOrganization.setChecked(true);
                       checkBoxDoctor.setChecked(true);
                   }
                   if(!checkBoxSelectAll.isChecked())
                   {
                       checkBoxName.setChecked(false);
                       checkBoxSurname.setChecked(false);
                       checkBoxGender.setChecked(false);
                       checkBoxBirthDate.setChecked(false);
                       checkBoxCititzen.setChecked(false);
                       checkBoxCountry.setChecked(false);
                       checkBoxOrganization.setChecked(false);
                       checkBoxDoctor.setChecked(false);
                   }
               }
           }
        );

        builder.setView(view)
                .setTitle(getString(R.string.titleShareDialogPrivacy))
                .setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String share = new String();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        if(checkBoxName.isChecked())
                            share = share.concat(getString(R.string.profileUserName).concat(": ").concat(profileName.getText().toString().concat("\n")));
                        if(checkBoxSurname.isChecked())
                            share = share.concat(getString(R.string.profileUserSurname).concat(": ").concat(profileSurname.getText().toString()).concat("\n"));
                        if(checkBoxGender.isChecked())
                            share = share.concat(getString(R.string.profileUserGender).concat(": ").concat(profileGender.getText().toString()).concat("\n"));
                        if(checkBoxBirthDate.isChecked())
                            share = share.concat(getString(R.string.profileUserBirthDate).concat(": ").concat(profileBirthDate.getText().toString()).concat("\n"));
                        if(checkBoxCititzen.isChecked())
                            share = share.concat(getString(R.string.profileUserCitizen).concat(": ").concat(profileCitizen.getText().toString()).concat("\n"));
                        if(checkBoxCountry.isChecked())
                            share = share.concat(getString(R.string.profileUserCountry).concat(": ").concat(profileCountry.getText().toString()).concat("\n"));
                        if(checkBoxOrganization.isChecked())
                            share = share.concat(getString(R.string.profileUserResidence).concat(": ").concat(profileResidence.getText().toString()).concat("\n"));
                        if(checkBoxDoctor.isChecked())
                            share = share.concat(getString(R.string.doctor).concat(": ").concat(profileDoctor.getText().toString()));
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, share);
                        startActivity(intent);

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle negative button click
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}