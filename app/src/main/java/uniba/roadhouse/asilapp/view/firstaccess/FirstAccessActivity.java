package uniba.roadhouse.asilapp.view.firstaccess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.view.home.HomeActivity;


/**
 * Activity relativa alle schermate di login e registrazione.
 */
public class FirstAccessActivity extends AppCompatActivity {
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
        setContentView(R.layout.first_access_activity);
        // Renndo di colore blu la StatusBar.
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        // Riferimento all'icona di connessione assente.
        noConnectionIcon = findViewById(R.id.noConnectionIcon);
        // Se non c'è connessione, rendo visibile l'icona di connessione assente e mostro il dialog all'utente.
        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(FirstAccessActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            noConnectionIcon.setVisibility(View.VISIBLE);
            dialogConnection = true;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Se l'utente risulta già loggato, salvo il suo username e pass direttamente ad HomeActivity.
        if(Dao.checkIsLogged(this))
        {
            Access.setUsername("ciao");
            Intent openHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(openHome);
        }
        verifyConnectionEachMilliseconds(4000);
        noConnectionIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Utility.showAlertDialog(FirstAccessActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            }
        });

        // Se l'utente non risulta loggato, apro il fragment di login.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.primoAccessoFragmentView, FirstAccessFragment.class, null);
        fragmentTransaction.commit();
    }
    /**
     * Sostituisce il fragment di login con quello di registrazione.
     */
    public void callRegisterFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.primoAccessoFragmentView, SignupFragment.class, null);
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
                if (!Utility.isConnectedToInternet(FirstAccessActivity.this))
                {
                    if(dialogConnection==false)
                    {
                        Utility.showAlertDialog(FirstAccessActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
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