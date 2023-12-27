package uniba.roadhouse.asilapp.controller.doctor;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.user.home.HealthHistoryFragment;
import uniba.roadhouse.asilapp.controller.user.home.HomeActivity;

/**
 * Activity relativa all'account dottore. Viene aperta quando, da FirstActivity, si seleziona l'accesso come account dottore.
 */
public class DoctorActivity extends AppCompatActivity {
    /**
     * Icona dell'app situata nella toolbar.
     */
    ImageView toolBarIconDoctorActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disattivo il tema scuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_doctor);
        // Renndo di colore blu la StatusBar.
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        // Se non c'è connessione, mostro il dialog all'utente.
        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
        }
        //RIFERIMENTI
        toolBarIconDoctorActivity = findViewById(R.id.toolBarIconDoctorActivity);
        openSigningFragment();
    }


    @Override
    protected void onStart() {
        //verifico se il dottore è loggato
        if(getIntent().getBooleanExtra("logged",false)){
            //se il dottore è loggato lo porto alla home
            openHomeFragment();
        }
        // LISTENER
        toolBarIconDoctorActivity.setOnClickListener(v->openHomeFragment());
        super.onStart();
    }

    /**
     * Apre il fragment di login per l'account dottore (SigninDoctorFragment).
     */
    private void openSigningFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.doctorFragmentView, SigninDoctorFragment.class, null);
        fragmentTransaction.commit();
    }

    /**
     * Apre il fragment "HomeDoctorFragment" se il fragment attuale non è quello di login (SinginDoctorFragment).
     */
    private void openHomeFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(!(fragmentManager.findFragmentById(R.id.doctorFragmentView) instanceof SigninDoctorFragment))
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.doctorFragmentView, HomeDoctorFragment.class, null);
            fragmentTransaction.commit();
        }
    }

}