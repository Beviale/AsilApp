package uniba.roadhouse.asilapp.controller.user.home;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Misurazione;

/**
 * Activity contenente tutti i vari fragment accessibili da un account utente.
 */
public class HomeActivity extends AppCompatActivity {
    Map<String,Integer> screenIcons;
    Map<String,Integer> screenIconsBg;
    Map<String,Class> screenFragments;
    Map<String,Integer> screenActiveMipmapIcons;
    Map<String,Integer> screenMipmapIcons;
    /**
     * Banner da mostrare se vi è una misurazione pendenente da memorizzare nel database.
     */
    TextView textBannerOnePendingMisuration;
    /**
     * Testo della toolbar;
     */
    TextView homeText;
    /**
     * Icona della toolbar che segnala l'assenza di connessione ad Internet.
     */
    ImageView noConnectionIconHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        // Disattivo il tema scuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Coloro la statusBar
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        // RIFERIMENTI
        noConnectionIconHome = findViewById(R.id.noConnectionIconHome);
        homeText=findViewById(R.id.homeScreenTextView);
        textBannerOnePendingMisuration = findViewById(R.id.textBannerOnePendingMisuration);
        //callback chiamata quando premo il tasto back
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
        // Registra il metodo di callback relativo alla connessione.
        registerNetworkCallback();
        // Se non c'è connessione, mostro un messaggio di dialog.
        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(HomeActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
        }
        // Verifico se vi è una misurazione pendente.
        verifyPendingMisuration();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //imposto le map che contengono per ogni schermata, l'id dell'icona nella barra inferioe, l'id del cerchio in bg e la classe del fragment
        //da aprire al click sull'icona
        screenIcons=new HashMap<String, Integer>() {{
            put(getResources().getString(R.string.userMenuScreen), R.id.user_icon);
            put(getResources().getString(R.string.homeMenuScreen),R.id.home_icon);
            put(getResources().getString(R.string.healthMenuScreen), R.id.health_icon);
            put(getResources().getString(R.string.healthBoxMenuScreen),R.id.health_box_icon);
            put(getResources().getString(R.string.positionMenuScreen),R.id.position_icon);
        }};

        screenActiveMipmapIcons=new HashMap<String, Integer>() {{
            put(getResources().getString(R.string.userMenuScreen), R.mipmap.user_icon_active);
            put(getResources().getString(R.string.homeMenuScreen),R.mipmap.home_icon_active);
            put(getResources().getString(R.string.healthMenuScreen), R.mipmap.hearth_icon_active);
            put(getResources().getString(R.string.healthBoxMenuScreen),R.mipmap.health_box_icon_active);
            put(getResources().getString(R.string.positionMenuScreen),R.mipmap.location_icon_active);
        }};

        screenMipmapIcons=new HashMap<String, Integer>() {{
            put(getResources().getString(R.string.userMenuScreen), R.mipmap.user_icon);
            put(getResources().getString(R.string.homeMenuScreen),R.mipmap.home_icon);
            put(getResources().getString(R.string.healthMenuScreen), R.mipmap.hearth_icon);
            put(getResources().getString(R.string.healthBoxMenuScreen),R.mipmap.health_box_icon);
            put(getResources().getString(R.string.positionMenuScreen),R.mipmap.location_icon);
        }};

        screenIconsBg=new HashMap<String, Integer>() {{
            put(getResources().getString(R.string.userMenuScreen), R.id.user_icon_bg);
            put(getResources().getString(R.string.homeMenuScreen),R.id.home_icon_bg);
            put(getResources().getString(R.string.healthMenuScreen), R.id.health_icon_bg);
            put(getResources().getString(R.string.healthBoxMenuScreen),R.id.health_box_icon_bg);
            put(getResources().getString(R.string.positionMenuScreen),R.id.position_icon_bg);
        }};

        screenFragments=new HashMap<String, Class>() {{
            put(getResources().getString(R.string.userMenuScreen), UserProfileFragment.class);
            put(getResources().getString(R.string.homeMenuScreen), HomeFragment.class);
            put(getResources().getString(R.string.healthMenuScreen), MedicalParametersFragment.class);
            put(getResources().getString(R.string.healthBoxMenuScreen), HealthBoxFragment.class);
            put(getResources().getString(R.string.positionMenuScreen), PositionFragment.class);
        }};
        // LISTENER
        findViewById(R.id.user_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.userMenuScreen)));
        findViewById(R.id.home_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.homeMenuScreen)));
        findViewById(R.id.health_box_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.healthBoxMenuScreen)));
        findViewById(R.id.health_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.healthMenuScreen)));
        findViewById(R.id.position_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.positionMenuScreen)));
        findViewById(R.id.toolBarIconHomeActivity).setOnClickListener(v->{if(!(getSupportFragmentManager().findFragmentById(R.id.homeContainerView) instanceof HomeFragment)) changeScreen(getResources().getString(R.string.homeMenuScreen));});
        noConnectionIconHome.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Utility.showAlertDialog(HomeActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            }
        });
        textBannerOnePendingMisuration.setOnClickListener(v->openDialogOnePendingRequest());

        //avvio il fragment di home che schermata da aprire all'avvio dell'activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getFragments().size()==0)
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.homeContainerView, screenFragments.get(getResources().getString(R.string.homeMenuScreen)), null);
            fragmentTransaction.addToBackStack(getResources().getString(R.string.homeMenuScreen));
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkCallback();
        super.onDestroy();
    }

    //callback chiamata quando si preme il tasto back fisico
    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            try{
                //quando premo il back..
                FragmentManager fragmentManager = getSupportFragmentManager();
                //verifico se sta solo un fragment nel backstack, cioè sto alla home
                if(fragmentManager.getBackStackEntryCount()==1){
                    //in quetso caso chiudo l'activiy, in quanto ho premuto back e non ho altri fragment (shermate) da mostrare
                    finishAffinity();
                }else{  //se ho un fragment prima di quello attualmente visibile
                    //prendo il nome della sezione del fragment attuale con cui lo ho memorizzato
                    String currScreenOpenSection=fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
                    //prendo il nome della sezione del fragment che devo aprira andando back
                    String prevScreenOpenSection=fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-2).getName();
                    //elimino dal backstack il fragment attualment ein visione
                    fragmentManager.popBackStack();

                    //se la sezione del fragment da aprire è diversa da quella del fragment attuale, cambio il colore dell'icona nel menu
                    if(prevScreenOpenSection!=currScreenOpenSection){
                        changeIcons(prevScreenOpenSection);
                    }
                }
            }catch (Exception e){
                recreate();
            }
        }
    };

    public void changeScreen(String screen){
        FragmentManager fragmentManager = getSupportFragmentManager();

        //apro il fragment che inidca la sezione cliccata da aprire
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, screenFragments.get(screen), null);
        fragmentTransaction.addToBackStack(screen);
        fragmentTransaction.commit();

        //cambio l'icona attivando quella cliccata
        changeIcons(screen);

    }

    private void changeIcons(String newScreen){

        for(String key:screenIcons.keySet()){
            findViewById(screenIconsBg.get(key)).setVisibility(View.INVISIBLE);
            ((ImageView) findViewById(screenIcons.get(key))).setImageResource(screenMipmapIcons.get(key));
        }
        findViewById(screenIconsBg.get(newScreen)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(screenIcons.get(newScreen))).setImageResource(screenActiveMipmapIcons.get(newScreen));
        homeText.setText(newScreen);
    }


    /**
     * Verifica la presenza di connessione in maniera costante.
     * Se la connessione è assente, mostra la relativa icona nella toolbar.
     * Se la connessione è presente, elimina l'icona nella toolbar e verifica se vi è una misurazione pendentente memorizzata nelle Shared Preferences. In tal caso, la invia subito al database.
     */
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {  //se sta connessione
                    noConnectionIconHome.setVisibility(View.GONE);
                    //verifico se nelle shared preferences ho una misurazione pendente da memorizzare
                    SharedPreferences sharedPref = getSharedPreferences("misurazione", MODE_PRIVATE);
                    String valutazione = sharedPref.getString("valutazione","NO");
                    String valore = sharedPref.getString("valore","NO");
                    String valoreMax = sharedPref.getString("valoreMax","NO");
                    String valoreMin = sharedPref.getString("valoreMin","NO");
                    String data = sharedPref.getString("data","NO");
                    String tipo = sharedPref.getString("tipo","NO");
                    String notaMedico = sharedPref.getString("notaMedico","NO");

                    if(!valutazione.equals("NO") && !valoreMin.equals("NO") && !valoreMin.equals("NO") && !valoreMax.equals("NO")
                    && !valore.equals("NO") && !data.equals("NO") && !tipo.equals("NO") && !notaMedico.equals("NO")){
                        //se ho preso correttamente tutti i dati dalla shared preferences della misurazione
                        //memorizzo la misurazione sul db
                        CompletableFuture<String> future = Dao.storeMisuration(new Misurazione(UserLogin.getUsername(),valutazione,Double.parseDouble(valore),Double.parseDouble(valoreMax),Double.parseDouble(valoreMin),new Timestamp(new Date(Long.parseLong(data)*1000)), TipoMisurazioneEnum.valueOf(tipo),notaMedico),getApplicationContext());
                        future.thenAccept(result -> {
                            runOnUiThread(() -> {
                                textBannerOnePendingMisuration.setVisibility(View.GONE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove("valutazione");
                                editor.apply();
                                //quando ho memorizzato la misurazione mostro l'esito della computazione
                                Toast.makeText(getApplicationContext(),getString(R.string.misurationStoredDB), Toast.LENGTH_LONG).show();
                            });});
                    }
                }
            });
            super.onAvailable(network);
        }

        @Override
        public void onLost(@NonNull Network network) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noConnectionIconHome.setVisibility(View.VISIBLE);
                }
            });

            super.onLost(network);
        }
    };

    /**
     * Registra il metodo di callback relativo alla verifica della connessione.
     */
    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        }
    }

    /**
     * Deregistra il metodo di callback relativo alla verifica della connessione.
     */
    private void unregisterNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    /**
     * Mostra la finestra di dialog relativa alla misurazione pendenente.
     */
    private void openDialogOnePendingRequest()
    {
        Utility.showAlertDialog(this, getString(R.string.onePendingRequestInfoTitle), getString(R.string.onePendingRequestInfo));
    }

    /**
     * Se vi è una misurazione pendente, rende visibile il relativo banner.
     */
    private void verifyPendingMisuration()
    {
        SharedPreferences sharedPref = getSharedPreferences("misurazione", MODE_PRIVATE);
        String valutazione = sharedPref.getString("valutazione","NO");
        if(!(valutazione.equals("NO")))
        {
            textBannerOnePendingMisuration.setVisibility(View.VISIBLE);
        }
    }


}