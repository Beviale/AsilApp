package uniba.roadhouse.asilapp.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import java.util.Map;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.doctor.DoctorActivity;
import uniba.roadhouse.asilapp.controller.user.home.HomeActivity;
import uniba.roadhouse.asilapp.controller.user.signinSignup.SigninSingupActivity;
import uniba.roadhouse.asilapp.model.dao.DoctorLogin;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;

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
        super.onStart();
        // Verifico se un l'utente/dottore risulta già loggato.
        Map<String,String> verifyLogged= Dao.checkIsLogged(this);
        // Se l'utente/dottore risulta già loggato
        if(verifyLogged.get("username")!="")
        {
            // Se si tratta di un utente, salvo l'username, il nome e il tipo e apro HomeActivity.
            if(verifyLogged.get("tipo").equals("UTENTE")){
                UserLogin.setTipoAsiloProtezione(verifyLogged.get("tipoAsiloProtezione"));
                UserLogin.setUsername(verifyLogged.get("username"));
                UserLogin.setNome(verifyLogged.get("nome"));
                Intent openHome = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(openHome);
            }else{
                // Se si tratta di un dottore, apro DoctorActivity passandogli come parametro nell'extra un logged true.
                // In questo modo DoctorActivity re-nindirizzerà il dottore direttamente alla home.
                DoctorLogin.setUsername(verifyLogged.get("username"));
                Intent openHome = new Intent(getApplicationContext(), DoctorActivity.class);
                openHome.putExtra("logged",true);
                startActivity(openHome);
            }
        }
        //----------LISTENER---------------------------
        accessPatientButton.setOnClickListener(v->openAccessPatient());
        accessDoctorButton.setOnClickListener(v->openAccessDoctor());
        // Animazione di background
        AnimationDrawable animationDrawable = (AnimationDrawable) layoutFirstActivity.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
    }


    /**
     * Apre l'activity di accesso come utente, ovvero "SigninSignupActivity"
     */
    private void openAccessPatient()
    {
        Intent intent = new Intent(this, SigninSingupActivity.class);
        startActivity(intent);
    }

    /**
     * Apre l'activity di accesso come dottore, ovvero "DoctorActivity".
     */
    private void openAccessDoctor()
    {
        Intent intent = new Intent(this, DoctorActivity.class);
        startActivity(intent);
    }
}