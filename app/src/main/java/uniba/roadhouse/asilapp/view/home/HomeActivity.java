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
import android.view.Window;
import android.widget.ImageView;

import com.google.android.material.datepicker.MaterialCalendar;

import uniba.roadhouse.asilapp.R;

public class HomeActivity extends AppCompatActivity {

    ImageView toolBarDownIconMedicalParameters;
    int currentSectionOpen=-1;
    Menu menuBottomBar;
    Toolbar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));

        bottomBar=findViewById(R.id.toolbarHomeActivityDown);
        setSupportActionBar(bottomBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        bottomBar.setTitle("");
        bottomBar.setSubtitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.botton_menu_home,menu);
        menuBottomBar=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id=item.getItemId();

        //se ho cliccato sul menu della salute, lancio il fragment ed illumino l'icona nel caso in cui non sia quela attualmente attiva
        if(id==R.id.healthMenuIcon){
            openHomeScreen(item,MedicalParametersFragment.class,R.mipmap.heart_pressed);
        }

        return true;

    }

    private void openHomeScreen(MenuItem item, Class fragmentToOpen, int icon_active){
        //apro il fragment che inidca la sezione cliccata da aprire
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.homeContainerView, fragmentToOpen, null);
        fragmentTransaction.commit();

        //se la schermata attuale non è quella che sto aprendo, allora imposto l'icona della schermata da aprire
        //come schermata attiva (è illuminata)
        if(currentSectionOpen!=item.getItemId()){
            item.setIcon(icon_active);
        }

        currentSectionOpen=item.getItemId();
    }
}