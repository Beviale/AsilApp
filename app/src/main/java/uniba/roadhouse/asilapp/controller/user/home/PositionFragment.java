package uniba.roadhouse.asilapp.controller.user.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.UserLogin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PositionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PositionFragment extends Fragment {
    /** TabLayout che consente di visuializzare MapFragment e MyRecidencyFragment. */
    private TabLayout tabLayoutPosition;

    /** Utente attualmente autenticato. */
    private Map<String, Object> utenteAttuale;

    /** Residenza attuale dell'utente attualmente autenticato. */
    private ResidenzaUtenteAttuale residenzaUtenteAttuale;
    private ProgressBar progressBar;
    /** Istanza corrente del PositionFragment usata per accedere all'utente da tutte le sotto-schermate. */
    public static PositionFragment Instance;

    public PositionFragment() {
        // Required empty public constructor
    }

    public static PositionFragment newInstance() {
        PositionFragment fragment = new PositionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_position, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayoutPosition = view.findViewById(R.id.tabLayoutPosition);
        progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupTabListener();
        Instance = this;
    }

    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);

        // Fetch dei dati utente
        progressBar.setVisibility(View.VISIBLE);
        fetchUtente();
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().findViewById(R.id.homeActivityProgressBar).setVisibility(View.GONE);
        super.onPause();
    }

    /**
     * Metodo per l'apertura del fragment relativo la mappa interattiva.
     */
    private void openMapFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.positionTabFragmentContainer, MapFragment.class, null);
        fragmentTransaction.commit();
    }

    /**
     * Metodo per l'apertura del fragment relativo la residenza attuale dell'utente autenticato.
     */
    private void openResidencyFragment(){
        if(getResidenzaUtenteAttuale()!=null)
        {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.positionTabFragmentContainer, MyRecidencyFragment.class, null);
            fragmentTransaction.commit();
        }
        else
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.homeContainerView, PositionFragment.class, null);
            fragmentTransaction.commit();
        }
    }

    /**
     * Classe di utility per la registrazione della residenza attuale dell'utente autenticato.
     * La uso per poter avere tutti i riferimenti necessari in un unico "luogo".
     */
    protected class ResidenzaUtenteAttuale{
        private final String cittaResidenza;
        private final String nomeResidenza;
        private final String descrizioneResidenza;
        private final Double latitudine;
        private final Double longitudine;

        ResidenzaUtenteAttuale(String cittaResidenza, String nomeResidenza, String descrizioneResidenza, Double latitudine, Double longitudine){
            this.cittaResidenza = cittaResidenza;
            this.nomeResidenza = nomeResidenza;
            this.descrizioneResidenza = descrizioneResidenza;
            this.latitudine = latitudine;
            this.longitudine = longitudine;
        }

        public String getCittaResidenza(){ return this.cittaResidenza; }
        public String getNomeResidenza(){ return this.nomeResidenza; }
        public String getDescrizioneResidenza(){ return this.descrizioneResidenza; }
        public Double getLatitudine(){ return this.latitudine; }
        public Double getLongitudine(){ return this.longitudine; }
    }

    /**
     * Eseguo il fetch dei dati dell'utente dal DB per poter trovare i dati relativi alla sua residenza.
     * Ottengo l'utente dai dati di login effettuati in modo da prendere l'utente attualmente autenticato.
     */
    private void fetchUtente() {
        // prendo il riferimento all'utente attuale
        CompletableFuture<Map<String, Object>> utenteFuture = Dao.getUserData(UserLogin.getUsername(), getActivity());
        utenteFuture.thenAccept(result -> getActivity().runOnUiThread(() -> {
            try{
                this.utenteAttuale = result;
                fetchResidenza(utenteAttuale.get("nomeResidenza").toString());
            }catch(Exception e){
                Activity activity = new HomeActivity();
                activity.onBackPressed();
            }
        }));
    }

    /**
     * Eseguo il fetch del nome della città in cui si trova la residenza di afferenza dell'utente al fine di
     * poter usare il dato nella query per mostrare la mappa all'utente.
     * @param nomeResidenza
     */
    private void fetchResidenza(final String nomeResidenza){
        // prendo il riferimento alla città di residenza
        CompletableFuture<String> cittaFuture = Dao.getCittaResidenza(nomeResidenza, getActivity());
        cittaFuture.thenAccept(result -> getActivity().runOnUiThread(() -> {
            try{
                fetchDatiResidenza(result, utenteAttuale.get("nomeResidenza").toString());
            }catch(Exception e){
                Activity activity = new HomeActivity();
                activity.onBackPressed();
            }
        }));
    }

    /**
     * Eseguo il fetch dei dati relativi alla residenza (coordinate e descrizione) al fine di poter popolare
     * le varie sezioni della schermata inerente la residenza attuale dell'utente.
     * @param cittaResidenza
     * @param nomeResidenza
     */
    private void fetchDatiResidenza(final String cittaResidenza, final String nomeResidenza){
        // prendo le coordinate della città di residenza
        CompletableFuture<Map<String, ?>> datiResidenzaFuture = Dao.getDatiResidenza(nomeResidenza, getActivity());
        datiResidenzaFuture.thenAccept(result -> getActivity().runOnUiThread(() -> {
            try{
                progressBar.setVisibility(View.GONE);
                Map<String, ?> datiResidenza = result;

                // prendo la lingua attuale del dispositivo (default inglese)
                String descrizioneResidenza;
                switch(Locale.getDefault().getLanguage()){
                    case "it":
                        descrizioneResidenza = (String) datiResidenza.get("descrizione_it");
                        break;
                    case "de":
                        descrizioneResidenza = (String) datiResidenza.get("descrizione_de");
                        break;
                    case "en":
                    default:
                        descrizioneResidenza = (String) datiResidenza.get("descrizione_en");
                        break;
                }

                this.residenzaUtenteAttuale = new ResidenzaUtenteAttuale(cittaResidenza, nomeResidenza, descrizioneResidenza, (Double) datiResidenza.get("latitudine"), (Double) datiResidenza.get("longitudine"));
                openMapFragment();
            }catch(Exception e){
                Activity activity = new HomeActivity();
                activity.onBackPressed();
            }
        }));
    }

    /**
     * Metodo per impostare i listener dei Tab del TabLayout.
     */
    private void setupTabListener(){
        tabLayoutPosition.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0){
                    openMapFragment();
                }
                else{
                    openResidencyFragment();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //do nothing
            }
        });
    }

    public ResidenzaUtenteAttuale getResidenzaUtenteAttuale(){
        return this.residenzaUtenteAttuale;
    }
    public Map<String, Object> getUtenteAttuale(){ return this.utenteAttuale; }

}