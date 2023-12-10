package uniba.roadhouse.asilapp.view.home;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialCalendar;
import com.google.firebase.Timestamp;

import java.sql.Time;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.controller.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.model.dao.Misurazione;


public class HomeActivity extends AppCompatActivity {
    Map<String,Integer> screenIcons;
    Map<String,Integer> screenIconsBg;
    Map<String,Class> screenFragments;
    Map<String,Integer> screenActiveMipmapIcons;
    Map<String,Integer> screenMipmapIcons;

    TextView homeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disattivo il tema scuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.home_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        homeText=findViewById(R.id.homeScreenTextView);
        //callback chiamata quando premo il tasto back
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
         /*String lorem = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
       Misurazione misurazione = new Misurazione();
        misurazione.setUsername(Access.getUsername());
        misurazione.setTipo(TipoMisurazioneEnum.PRESSIONESANGUIGNA);
        misurazione.setData(Timestamp.now());
        misurazione.setValore(null);
        misurazione.setValoreMax((double)102.0);
        misurazione.setValoreMin((double)85.0);
        misurazione.setNotaMedico(lorem);
        misurazione.setValutazione("Buono \uD83D\uDFE2");
       CompletableFuture<String> future = Dao.storeMisuration(misurazione, this);
        future.thenAccept(result -> {
            this.runOnUiThread(() -> {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            });
        });*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarHome);
        toolbar.inflateMenu(R.menu.menu_home_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.settings)
                {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.homeContainerView);
                    if(!(currentFragment instanceof SettingsFragment))
                    {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.homeContainerView, SettingsFragment.class, null);
                        fragmentTransaction.commit();
                    }

                }

                return true;
            }
        });
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
            put(getResources().getString(R.string.settingsMenuScreen),R.id.settings_icon);
        }};

        screenActiveMipmapIcons=new HashMap<String, Integer>() {{
            put(getResources().getString(R.string.userMenuScreen), R.mipmap.user_icon_active);
            put(getResources().getString(R.string.homeMenuScreen),R.mipmap.home_icon_active);
            put(getResources().getString(R.string.healthMenuScreen), R.mipmap.hearth_icon_active);
            put(getResources().getString(R.string.healthBoxMenuScreen),R.mipmap.health_box_icon_active);
            put(getResources().getString(R.string.settingsMenuScreen),R.mipmap.settings_icon_active);
        }};

        screenMipmapIcons=new HashMap<String, Integer>() {{
            put(getResources().getString(R.string.userMenuScreen), R.mipmap.user_icon);
            put(getResources().getString(R.string.homeMenuScreen),R.mipmap.home_icon);
            put(getResources().getString(R.string.healthMenuScreen), R.mipmap.hearth_icon);
            put(getResources().getString(R.string.healthBoxMenuScreen),R.mipmap.health_box_icon);
            put(getResources().getString(R.string.settingsMenuScreen),R.mipmap.settings_icon);
        }};

        screenIconsBg=new HashMap<String, Integer>() {{
            put(getResources().getString(R.string.userMenuScreen), R.id.user_icon_bg);
            put(getResources().getString(R.string.homeMenuScreen),R.id.home_icon_bg);
            put(getResources().getString(R.string.healthMenuScreen), R.id.health_icon_bg);
            put(getResources().getString(R.string.healthBoxMenuScreen),R.id.health_box_icon_bg);
            put(getResources().getString(R.string.settingsMenuScreen),R.id.settings_icon_bg);
        }};

        screenFragments=new HashMap<String, Class>() {{
            put(getResources().getString(R.string.userMenuScreen), UserProfileFragment.class);
            put(getResources().getString(R.string.homeMenuScreen), HomeFragment.class);
            put(getResources().getString(R.string.healthMenuScreen), MedicalParametersFragment.class);
            put(getResources().getString(R.string.healthBoxMenuScreen), HealthBoxFragment.class);
            put(getResources().getString(R.string.settingsMenuScreen), SettingsFragment.class);
        }};

        findViewById(R.id.user_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.userMenuScreen)));
        findViewById(R.id.home_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.homeMenuScreen)));
        findViewById(R.id.health_box_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.healthBoxMenuScreen)));
        findViewById(R.id.health_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.healthMenuScreen)));
        findViewById(R.id.settings_icon_layout).setOnClickListener(v->changeScreen(getResources().getString(R.string.settingsMenuScreen)));

        findViewById(R.id.toolBarIconHomeActivity).setOnClickListener(v->changeScreen(getResources().getString(R.string.homeMenuScreen)));

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

    private void changeScreen(String screen){
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




}