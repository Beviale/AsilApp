package uniba.roadhouse.asilapp.view.home;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialCalendar;

import java.util.HashMap;
import java.util.Map;

import uniba.roadhouse.asilapp.R;

public class HomeActivity extends AppCompatActivity {
    Map<String,Integer> screenIcons;
    Map<String,Integer> screenIconsBg;
    Map<String,Class> screenFragments;
    Map<String,Integer> screenActiveMipmapIcons;
    Map<String,Integer> screenMipmapIcons;

    //stringa che indica l'attuale schermata (fragment) aperta
    String currentSectionOpen;

    TextView homeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        homeText=findViewById(R.id.homeScreenTextView);

        //callback chiamata quando premo il tasto back
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

        if(savedInstanceState!=null){
            currentSectionOpen=savedInstanceState.getString("currentSection");
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


        //imposto l'attuale schermata aperta ad una schermata non home
        currentSectionOpen=getResources().getString(R.string.userMenuScreen);

        //avvio il fragment di home che schermata da aprire all'avvio dell'activity
        changeScreen(getResources().getString(R.string.homeMenuScreen));
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
                //prendo il nome e classe con cui ho messo nel backstack il fragment che si aprirà alla pressione del back
                String prevScreenOpen=fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-2).getName();
                Class prevScreenClass=fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-2).getClass();
                //elimino dal backstack il fragment attualmentein visione
                fragmentManager.popBackStack(currentSectionOpen,FragmentManager.POP_BACK_STACK_INCLUSIVE);

                //se la scehrmata precedente nonha un nome, allora la apro semplicemente, senza cambiare le icone del menu e il loro colore
                if(prevScreenOpen==null){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.homeContainerView, prevScreenClass, null);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{  //se invece la rpecedente scherata ha un nome, allora cambio il colore dell'icona e vado a tale schermata
                    //cambio il colore dell'icona, mettendo ad attiva il fragment da aprire e disattiva quello che stava aperto fino a questo momento
                    changeIcons(prevScreenOpen);
                }
            }
        }
    };

    private void changeScreen(String screen){
        //apro il fragment che inidca la sezione cliccata da aprire
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, screenFragments.get(screen), null);
        fragmentTransaction.addToBackStack(screen);
        fragmentTransaction.commit();

        //se la schermata attuale non è quella che sto aprendo, allora imposto l'icona della schermata da aprire
        //come schermata attiva (è illuminata)
        if(currentSectionOpen!=screen) {
            changeIcons(screen);
        }

    }

    private void changeIcons(String screen){
        findViewById(screenIconsBg.get(screen)).setVisibility(View.VISIBLE);
        findViewById(screenIconsBg.get(currentSectionOpen)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(screenIcons.get(screen))).setImageResource(screenActiveMipmapIcons.get(screen));
        ((ImageView) findViewById(screenIcons.get(currentSectionOpen))).setImageResource(screenMipmapIcons.get(currentSectionOpen));
        currentSectionOpen=screen;
        homeText.setText(screen);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("currentSection",currentSectionOpen);
    }
}