package uniba.roadhouse.asilapp.controller.user.signinSignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.controller.user.home.HomeActivity;
import uniba.roadhouse.asilapp.model.dao.Dao;


/**
 * Activity relativa alle schermate di login e registrazione.
 */
public class SigninSingupActivity extends AppCompatActivity {
    /**
     * Icona di connessione assente.
     */
    ImageView noConnectionIcon;
    /**
     * Se true significa che il dialog di connessione assente è stato già mostrato almeno 1 volta.
     */
    public static Boolean dialogConnection=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disattivo il tema scuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.signin_singnup_activity);

        // Renndo di colore blu la StatusBar.
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        // Riferimento all'icona di connessione assente.
        noConnectionIcon = findViewById(R.id.noConnectionIconSigninSignupActivity);
        // Se non c'è connessione, rendo visibile l'icona di connessione assente e mostro il dialog all'utente.
        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(SigninSingupActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            noConnectionIcon.setVisibility(View.VISIBLE);
            dialogConnection = true;
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Map<String,String> verifyLogged= Dao.checkIsLogged(this);
        // Se l'utente risulta già loggato, salvo il suo username e passo direttamente ad HomeActivity.
        if(verifyLogged.get("username")!="")
        {
            AccessUser.setUsername(verifyLogged.get("username"));
            AccessUser.setNome(verifyLogged.get("nome"));
            Intent openHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(openHome);
        }
        verifyConnectionEachMilliseconds(4000);
        noConnectionIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Utility.showAlertDialog(SigninSingupActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            }
        });

        // Se l'utente non risulta loggato, apro il fragment di login.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.signinFragmentView, SiginFragment.class, null);
        fragmentTransaction.commit();
    }
    /**
     * Sostituisce il fragment di login con quello di registrazione.
     */
    public void callRegisterFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.signinFragmentView, SignupFragment.class, null);
        fragmentTransaction.commit();
    }



    /**
     * Verifica la presenza di connessione ogni delay millisecondi.
     * Se la connessione è assente, mostra la relativa icona e il dialog solo se non è stato già mostrato in precedenza.
     * Se la connessione è presente, elimina l'icona.
     * @param delay ogni quanto la connessione deve essere verificata. Espresso in millisecondi.
     */
    private void verifyConnectionEachMilliseconds(int delay)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!Utility.isConnectedToInternet(SigninSingupActivity.this))
                {
                    if(dialogConnection==false)
                    {
                        Utility.showAlertDialog(SigninSingupActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
                    }
                    noConnectionIcon.setVisibility(View.VISIBLE);
                    handler.postDelayed(this, delay);
                    dialogConnection=true;
                }
                else
                {
                    noConnectionIcon.setVisibility(View.INVISIBLE);
                    handler.postDelayed(this, delay);

                }
            }
        }, delay);

    }
}