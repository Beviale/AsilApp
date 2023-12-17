package uniba.roadhouse.asilapp.controller.doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.util.Map;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.patient.home.HomeActivity;
import uniba.roadhouse.asilapp.controller.patient.signinSignup.SiginFragment;
import uniba.roadhouse.asilapp.controller.patient.signinSignup.SigninSingupActivity;
import uniba.roadhouse.asilapp.controller.patient.signinSignup.SignupFragment;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;

public class DoctorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disattivo il tema scuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_doctor);

        // Renndo di colore blu la StatusBar.
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        // Se non c'è connessione, rendo visibile l'icona di connessione assente e mostro il dialog all'utente.
        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
        }
        openSigningFragment();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void openSigningFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.doctorFragmentView, SigninDoctorFragment.class, null);
        fragmentTransaction.commit();

    }


}