package uniba.roadhouse.asilapp.controller.patient.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.FirstActivity;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.controller.patient.signinSignup.SigninSingupActivity;

/**
 * Schermata delle impostazioni dell'app.
 */
public class SettingsFragment extends Fragment {
    /**
     * TextInputEditText relativa alla modifica della password.
     */
    TextInputEditText changePasswordInput;
    /**
     * AutoCompelteTextView relativa alla modifica della città.
     */
    AutoCompleteTextView cityModify;
    /**
     * AutoCompletTextView relativa alla modifica del nome della struttura di accoglienza.,
     */
    AutoCompleteTextView nameOrganizationModify;
    /**
     * Bottone che avvia la modifica dei dati.
     */
    Button editProfileButton;
    /**
     * PorgressBar da mostrare durante la connessione al database.
     */
    ProgressBar homeActivityProgressBar;
    /**
     * Layout dell'intera schermata impostazioni.
     */
    ConstraintLayout settingsLayout;
    /**
     * RatingBar per la valutazione complessiva dell'app da parte dell'utente.
     */
    RatingBar ratingApp;
    /**
     * Testo che rappresenta in formato numerico la valutazione complessiva dell'app da parte dell'utente.
     */
    TextView valueRatingApp;
    /**
     * Bottonc che consente l'uscita dall'account corrente.
     */
    Button exitAccountButton;
    /**
     * Layout che contiene l'immagine e il testo relativi al check della password inserita.
     */
    LinearLayout layoutPasswordCheckModify;
    /**
     * Immagine relativa al check della password inserita.
     */
    ImageView passwordResultImageModify;
    /**
     * Testo relativo al check della password inserita.
     */
    TextView passwordResultTextModify;


    /**
     * se true indica che la password è valida per essere modificata effettivamente nel database, altrimenti false.
     */
    private static Boolean passwordChanged=false;



    /**
     * Nome corrente della struttura di accoglienza.
     */
    private static String currentNameOrganization;
    /**
     * città corrente della struttura di accoglienza.
     */
    private static String currentCityOrganization;



    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        editProfileButton.setOnClickListener(v->applyChanges());

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

        // Aggiungo i TextWatcher
        cityModify.addTextChangedListener(textWatcherCity);
        nameOrganizationModify.addTextChangedListener(textWacherName);
        changePasswordInput.addTextChangedListener(textWatcherPassword);
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
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
     * Recupero dal database i dati correnti.
     */
    private void getData()
    {
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        settingsLayout.setAlpha((float)0.5);
        CompletableFuture<Map<String, Object>> future = Dao.getUserData(Access.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                if(result!=null) {
                    currentNameOrganization =  result.get("nomeResidenza").toString();
                    nameOrganizationModify.setText(currentNameOrganization);
                    CompletableFuture<String> futureCity = Dao.getCittaResidenza(currentNameOrganization, getActivity());
                    futureCity.thenAccept(resultCity -> {
                        getActivity().runOnUiThread(() -> {
                            homeActivityProgressBar.setVisibility(View.GONE);
                            settingsLayout.setAlpha((float)1);
                            currentCityOrganization = resultCity;
                            cityModify.setText(currentCityOrganization);
                            loadAllCity();
                            Editable editable = new SpannableStringBuilder(currentCityOrganization);
                            textWatcherCity.afterTextChanged(editable);

                        });
                    });

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
        Intent intent = new Intent(getActivity(), FirstActivity.class);
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
            if(s.toString().isEmpty())
            {
                passwordResultImageModify.setVisibility(View.GONE);
                passwordResultTextModify.setVisibility(View.GONE);
                passwordChanged=false;
                if(!nameOrganizationModify.equals(currentNameOrganization))
                {
                    editProfileButton.setEnabled(true);
                }
                return;
            }
            layoutPasswordCheckModify.setVisibility(View.VISIBLE);
            passwordResultImageModify.setVisibility(View.VISIBLE);
            passwordResultTextModify.setVisibility(View.VISIBLE);
            if(Utility.checkRegexPassword(changePasswordInput.getText().toString())==true)
            {
                passwordResultTextModify.setText(getString(R.string.passwordRegexOk));
                passwordResultImageModify.setImageResource(R.mipmap.verified);
                passwordChanged=true;
                editProfileButton.setEnabled(true);
            }
            else
            {
                Utility.textViewUnderlineText(passwordResultTextModify,getString(R.string.passwordRegexError));
                passwordResultImageModify.setClickable(true);
                passwordChanged=false;
                passwordResultImageModify.setImageResource(R.mipmap.error);
                editProfileButton.setEnabled(false);
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


    /**
     * Applica le modifiche effettuate dall'utente.
     */
    private void applyChanges()
    {
        if(passwordChanged==true && (!currentNameOrganization.equals(nameOrganizationModify.getText().toString())) && (!changePasswordInput.getText().toString().isEmpty()))
        {
            homeActivityProgressBar.setVisibility(View.VISIBLE);
            settingsLayout.setAlpha((float)0.5);
            CompletableFuture<String> future = Dao.editResidenzaUtente(Access.getUsername(), nameOrganizationModify.getText().toString(), getActivity());
            future.thenAccept(result -> {
                getActivity().runOnUiThread(() -> {
                    CompletableFuture<String> futurePassword = Dao.editPasswordUtente(Access.getUsername(), changePasswordInput.getText().toString(), getActivity());
                    futurePassword.thenAccept(resultPassword -> {
                        getActivity().runOnUiThread(() -> {
                            homeActivityProgressBar.setVisibility(View.GONE);
                            settingsLayout.setAlpha((float)1);
                            if(result.equals(getString(R.string.changeResidenzaSuccessfully)) && resultPassword.equals(getString(R.string.editPasswordSuccessfull)))
                            {
                                Toast.makeText(getActivity(), getString(R.string.allApplyCHanged), Toast.LENGTH_LONG).show();
                                exitAccount();
                            }
                            else if(result.equals(getString(R.string.changeResidenzaSuccessfully)))
                            {
                                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                                Toast.makeText(getActivity(), resultPassword, Toast.LENGTH_LONG).show();
                                editProfileButton.setEnabled(false);
                            }
                            else if(resultPassword.equals(getString(R.string.editPasswordSuccessfull)))
                            {
                                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                                Toast.makeText(getActivity(), resultPassword, Toast.LENGTH_LONG).show();
                                exitAccount();
                            }

                        });
                    });
                });
            });
        }

        else if(passwordChanged==true)
        {
            homeActivityProgressBar.setVisibility(View.VISIBLE);
            settingsLayout.setAlpha((float)0.5);
            CompletableFuture<String> future = Dao.editPasswordUtente(Access.getUsername(), changePasswordInput.getText().toString(), getActivity());
            future.thenAccept(result -> {
                getActivity().runOnUiThread(() -> {
                    homeActivityProgressBar.setVisibility(View.GONE);
                    settingsLayout.setAlpha((float)1);
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    exitAccount();
                });
            });

        }
        else if(!currentNameOrganization.equals(nameOrganizationModify.getText().toString()))
        {
            homeActivityProgressBar.setVisibility(View.VISIBLE);
            settingsLayout.setAlpha((float)0.5);
            CompletableFuture<String> future = Dao.editResidenzaUtente(Access.getUsername(), nameOrganizationModify.getText().toString(), getActivity());
            future.thenAccept(result -> {
                getActivity().runOnUiThread(() -> {
                    homeActivityProgressBar.setVisibility(View.GONE);
                    settingsLayout.setAlpha((float)1);
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    if(result.equals(getString(R.string.changeResidenzaSuccessfully)))
                    {
                        editProfileButton.setEnabled(false);
                        currentNameOrganization = nameOrganizationModify.getText().toString();
                    }
                });
            });

        }

    }





    TextWatcher textWatcherCity = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Ogni volta che la città viene modificata, automaticamente viene aggiornata la lista delle strutture di accoglienza.
         * @param s, città appena selezionata
         */
        @Override
        public void afterTextChanged(Editable s) {
            homeActivityProgressBar.setEnabled(true);
            settingsLayout.setAlpha((float)0.5);
            CompletableFuture<List<String>> future = Dao.getNomiResidenze(s.toString());
            List<String> allOrganization = new ArrayList<String>();
            future.thenAccept(result -> {
                getActivity().runOnUiThread(() -> {
                    homeActivityProgressBar.setEnabled(false);
                    settingsLayout.setAlpha((float)1);
                    allOrganization.addAll(result);
                    if(!allOrganization.contains(nameOrganizationModify.getText().toString()))
                    {
                        nameOrganizationModify.setText(allOrganization.get(0));
                    }
                    ArrayAdapter<String> adapterOrganization = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allOrganization);
                    nameOrganizationModify.setAdapter(adapterOrganization);

                });
            });
        }
    };


    /**
     * Prende dal database tutte le città che hanno almeno una struttura di accoglienza e le inserisce nella view apposita.
     */
    private void loadAllCity()
    {
        homeActivityProgressBar.setEnabled(true);
        settingsLayout.setAlpha((float)0.5);
        CompletableFuture<List<String>> future = Dao.getNomiCittaResidenze();
        List<String> allCity = new ArrayList<String>();
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                homeActivityProgressBar.setEnabled(false);
                settingsLayout.setAlpha((float)1);
                allCity.addAll(result);
            });
        });
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allCity);
        cityModify.setAdapter(adapterCity);
    }


    TextWatcher textWacherName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Attiva il bottone di modifica se il nome della struttura è diverso dal corrente e la password o è vuota oppure rispetta i criteri.
         * @param s, nome della struttura di accoglienza selezionata
         */
        @Override
        public void afterTextChanged(Editable s) {
            if(!s.toString().equals(currentNameOrganization))
            {
                if(passwordChanged==true || changePasswordInput.getText().toString().isEmpty())
                {
                    editProfileButton.setEnabled(true);
                }
            }
            else
            {
                editProfileButton.setEnabled(false);
            }

        }
    };
}