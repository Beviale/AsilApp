package uniba.roadhouse.asilapp.controller.user.signinSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;


/**
 * Activity relativa alle schermate di login e registrazione.
 */
public class SigninSingupActivity extends AppCompatActivity {
    /**
     * Icona di connessione assente.
     */
    ImageView noConnectionIcon;
    /**
     * Icona dell'app presente sulla toolbar;
     */
    ImageView toolBarIconSigninSisngupActivity;


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
        // Riferimento all'icona dell'app nella toolbar;
        toolBarIconSigninSisngupActivity = findViewById(R.id.toolBarIconSigninSisngupActivity);
        // Registra il metodo di callback relativo alla verifica della connessione.
        registerNetworkCallback();
        // Se non c'è connessione, rendo visibile l'icona di connessione assente e mostro il dialog all'utente.
        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(SigninSingupActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            noConnectionIcon.setVisibility(View.VISIBLE);
        }
        openSigninFragment();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //-----------------LISTENER--------------
        noConnectionIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Utility.showAlertDialog(SigninSingupActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            }
        });
        toolBarIconSigninSisngupActivity.setOnClickListener(v->openSigninFragment());
    }

    /**
     * Deregistra il metodo di callback relativo alla verifica della connessione.
     */
    @Override
    protected void onDestroy() {
        unregisterNetworkCallback();
        super.onDestroy();
    }




    /**
     * Verifica la presenza di connessione in maniera costante.
     * Se la connessione è assente, mostra la relativa icona e il dialog solo se non è stato già mostrato in precedenza.
     * Se la connessione è presente, elimina l'icona.
     */
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noConnectionIcon.setVisibility(View.GONE);
                }
            });
            super.onAvailable(network);
        }

        @Override
        public void onLost(@NonNull Network network) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noConnectionIcon.setVisibility(View.VISIBLE);
                }
            });
            super.onLost(network);
        }
    };


    /**
     * Registra il metodo di callback relativo alla verifica della connessione.
     */
    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
        }
    }

    /**
     * Deregistra il metodo di callback relativo alla verifica della connessione.
     */
    private void unregisterNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }


    /**
     * Apre il fragment di login.
     */
    private void openSigninFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.signinFragmentView, SigninFragment.class, null);
        fragmentTransaction.commit();

    }
}