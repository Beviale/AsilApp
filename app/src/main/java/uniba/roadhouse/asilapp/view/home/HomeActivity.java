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

import com.google.android.material.datepicker.MaterialCalendar;

import java.util.HashMap;
import java.util.Map;

import uniba.roadhouse.asilapp.R;

public class HomeActivity extends AppCompatActivity {
    Map<String,Integer> screenIcons=new HashMap<String, Integer>() {{
        put("user", R.id.user_icon);
        put("health", R.id.health_icon);
        put("health box",R.id.health_box_icon);
    }};
    Map<String,Integer> screenIconsBg=new HashMap<String, Integer>() {{
        put("user", R.id.user_icon_bg);
        put("health", R.id.health_icon_bg);
        put("health box",R.id.health_box_icon_bg);
    }};
    Map<String,Class> screenFragments=new HashMap<String, Class>() {{
        put("user", UserProfileFragment.class);
        put("health", MedicalParametersFragment.class);
        put("health box", HealthBoxFragment.class);
    }};
    String currentSectionOpen="user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.user_icon_layout).setOnClickListener(v->changeScreen("user"));
        findViewById(R.id.health_box_icon_layout).setOnClickListener(v->changeScreen("health box"));
        findViewById(R.id.health_icon_layout).setOnClickListener(v->changeScreen("health"));
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
    }
}