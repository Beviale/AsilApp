package uniba.roadhouse.asilapp.view.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
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

        //imposto l'attuale schermata aperta ad una schermata non home
        currentSectionOpen=getResources().getString(R.string.userMenuScreen);

        //avvio il fragment di home che schermata da aprire all'avvio dell'activity
        changeScreen(getResources().getString(R.string.homeMenuScreen));
    }

    private void changeScreen(String screen){
        //apro il fragment che inidca la sezione cliccata da aprire
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, screenFragments.get(screen), null);
        fragmentTransaction.commit();

        //se la schermata attuale non è quella che sto aprendo, allora imposto l'icona della schermata da aprire
        //come schermata attiva (è illuminata)
        if(currentSectionOpen!=screen){
            findViewById(screenIconsBg.get(screen)).setVisibility(View.VISIBLE);
            findViewById(screenIconsBg.get(currentSectionOpen)).setVisibility(View.INVISIBLE);
        }

        currentSectionOpen=screen;
        homeText.setText(screen);

    }
}