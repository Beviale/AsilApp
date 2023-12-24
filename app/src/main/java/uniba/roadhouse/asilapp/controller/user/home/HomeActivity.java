package uniba.roadhouse.asilapp.controller.user.home;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.user.signinSignup.SigninSingupActivity;


public class HomeActivity extends AppCompatActivity {
    Map<String,Integer> screenIcons;
    Map<String,Integer> screenIconsBg;
    Map<String,Class> screenFragments;
    Map<String,Integer> screenActiveMipmapIcons;
    Map<String,Integer> screenMipmapIcons;

    TextView homeText;
    ImageView noConnectionIconHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disattivo il tema scuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.home_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        noConnectionIconHome = findViewById(R.id.noConnectionIconHome);
        homeText=findViewById(R.id.homeScreenTextView);
        //callback chiamata quando premo il tasto back
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
        // Registra il metodo di callback relativo alla connessione.
        registerNetworkCallback();
        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(HomeActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
        }
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

        findViewById(R.id.user_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.userMenuScreen)));
        findViewById(R.id.home_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.homeMenuScreen)));
        findViewById(R.id.health_box_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.healthBoxMenuScreen)));
        findViewById(R.id.health_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.healthMenuScreen)));
        findViewById(R.id.position_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.positionMenuScreen)));

        findViewById(R.id.toolBarIconHomeActivity).setOnClickListener(v->{if(!(getSupportFragmentManager().findFragmentById(R.id.homeContainerView) instanceof HomeFragment)) changeScreen(getResources().getString(R.string.homeMenuScreen));});

        //avvio il fragment di home che schermata da aprire all'avvio dell'activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getFragments().size()==0)
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.homeContainerView, screenFragments.get(getResources().getString(R.string.homeMenuScreen)), null);
            fragmentTransaction.addToBackStack(getResources().getString(R.string.homeMenuScreen));
            fragmentTransaction.commit();
        }

        //------LISTENER-----------
        noConnectionIconHome.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
            Utility.showAlertDialog(HomeActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
        }
        });
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
                //elimino dal backstack il fragment attualmentein visione
                fragmentManager.popBackStack();

                //se la sezione del fragment da aprire è diversa da quella del fragment attuale, cambio il colore dell'icona nel menu
                if(prevScreenOpenSection!=currScreenOpenSection){
                    changeIcons(prevScreenOpenSection);
                }
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
     * Se la connessione è assente, mostra la relativa icona e il dialog solo se non è stato già mostrato in precedenza.
     * Se la connessione è presente, elimina l'icona.
     */
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noConnectionIconHome.setVisibility(View.GONE);
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

}