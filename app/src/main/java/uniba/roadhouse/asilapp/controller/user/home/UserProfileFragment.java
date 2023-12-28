package uniba.roadhouse.asilapp.controller.user.home;

import android.app.Activity;
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
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * Schermata che permette la visualizzazione dei dati (anagrafici e non) di un utente.
 * E' accessibile sia da account utente che dottore.
 */
public class UserProfileFragment extends Fragment {
    /**
     * Label del nome dell'utente.
     */
    TextView profileNameTitle;
    /**
     * Nome dell'utente.
     */
    TextView profileName;
    /**
     * Label del cognome dell'utente.
     */
    TextView profileSurnameTitle;
    /**
     * Cognome dell'utente.
     */
    TextView profileSurname;
    /**
     * Label del genere dell'utente.
     */
    TextView profileGenderTitle;
    /**
     * Genere dell'utente.
     */
    TextView profileGender;
    /**
     * Label della data di nascita dell'utente.
     */
    TextView profileBirthDateTitle;
    /**
     * Data di nascita dell'utente.
     */
    TextView profileBirthDate;
    /**
     * Label della cittadinanza dell'utente.
     */
    TextView profileCitizenTitle;
    /**
     * Cittadinanza dell'utente..
     */
    TextView profileCitizen;
    /**
     * Label del paese di provenienza dell'utente.
     */
    TextView profileCountryTitle;
    /**
     * Paese di provenienza dell'utente.
     */
    TextView profileCountry;
    /**
     * Label del nome della struttura di accoglienza dell'utente.
     */
    TextView profileResidenceTitle;
    /**
     * Struttura di accoglienza dell'utente.
     */
    TextView profileResidence;
    /**
     * Contenitore del QRCode che identifica univocamente l'utente.
     */
    ImageView profileQRCode;
    /**
     * ProgressBad da mostrare mentre i dati vengono caricati dal database.
     */
    ProgressBar progressBar;
    /**
     * Layout dell'intero fragment.
     */
    ConstraintLayout layoutUserProfile;
    /**
     * Label per il dottore dell'utente.
     */
    TextView profileDoctorTitle;
    /**
     * Dottore dell'utente.
     */
    TextView profileDoctor;

    /**
     * Indica se il fragment è stato aperto da un account dottore o meno.
     */
    private Boolean openDoctor=false;
    /**
     * Mappa restituitda dal Dao nell'esecuzione della query per il caricamento dei dati dal database.
     */
    Map<String, Object> currentData;


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
        //-----------RIFERIMENTI------------------
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


    /**
     * Recupera dal database tutti i dati dell'utente e setta i vari campi.
     */
    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        CompletableFuture<Map<String, Object>> future = Dao.getUserData(UserLogin.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                try{
                    layoutUserProfile.setAlpha((float)1.0);
                    progressBar.setVisibility(View.GONE);
                    currentData = result;
                    profileName.setText(currentData.get("nome").toString());
                    profileSurname.setText(currentData.get("cognome").toString());
                    profileGender.setText(currentData.get("sesso").toString());
                    profileBirthDate.setText((currentData.get("dataNascita").toString()));
                    profileCitizen.setText(currentData.get("cittadinanza").toString());
                    profileCountry.setText(currentData.get("paeseDiProvenienza").toString());
                    profileResidence.setText(currentData.get("nomeResidenza").toString());
                    profileQRCode.setImageBitmap((Bitmap) currentData.get("qrCode"));
                    profileDoctor.setText(currentData.get("dottore").toString());
                }catch (Exception e){
                    Activity activity = new HomeActivity();
                    activity.onBackPressed();
                }
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
        // Attivo la condivisione dei dati nell'action overflow.
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


    /**
     * Apre il dialog che consente la condivisione dei dati dell'utente.
     * Permette di selezionare e condividere tutti i dati o una parte (è possibile selezionarli singolarmente).
     */
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