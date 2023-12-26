package uniba.roadhouse.asilapp.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.doctor.DoctorActivity;
import uniba.roadhouse.asilapp.controller.user.signinSignup.SigninSingupActivity;

/**
 * Activity che viene mostrata quando viene avviata l'app e non vi è un JWT memorizzato, ovvero nessun utente o dottore risulta loggato.
 */
public class FirstActivity extends AppCompatActivity {

    /**
     * Layout dell'intera activity.
     */
    ConstraintLayout layoutFirstActivity;
    /**
     * Button che consente l'accesso come utente.
     */
    Button accessPatientButton;
    /**
     * Button che consente l'accesso come dottore.
     */
    Button accessDoctorButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disattivo la modalità scura
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_first);
        // Color la statusBar.
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColorFirstActivity));
        //---------RIFERIMENTI---------------
        layoutFirstActivity = findViewById(R.id.layoutFirstActivity);
        accessPatientButton = findViewById(R.id.accessPatientButton);
        accessDoctorButton = findViewById(R.id.accessDoctorButton);
    }

    @Override
    protected void onStart() {
        //----------LISTENER---------------------------
        accessPatientButton.setOnClickListener(v->openAccessPatient());
        accessDoctorButton.setOnClickListener(v->openAccessDoctor());
        // Animazione di background
        AnimationDrawable animationDrawable = (AnimationDrawable) layoutFirstActivity.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        super.onStart();
    }


    /**
     * Apre l'activity di accesso come utente "SigninSignupActivity"
     */
    private void openAccessPatient()
    {
        Intent intent = new Intent(this, SigninSingupActivity.class);
        startActivity(intent);
    }

    /**
     * Apre l'activity di accesso come dottore "DoctorActivity".
     */
    private void openAccessDoctor()
    {
        Intent intent = new Intent(this, DoctorActivity.class);
        startActivity(intent);
    }
}