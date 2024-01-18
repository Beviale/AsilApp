package uniba.roadhouse.asilapp.controller.doctor;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.checkerframework.checker.guieffect.qual.UIType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.user.home.HealthHistoryFragment;
import uniba.roadhouse.asilapp.controller.user.home.HomeActivity;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.DoctorLogin;
import uniba.roadhouse.asilapp.model.dao.UserLogin;

/**
 * Activity relativa all'account dottore. Viene aperta quando, da FirstActivity, si seleziona l'accesso come account dottore.
 */
public class DoctorActivity extends AppCompatActivity {
    /**
     * Icona dell'app situata nella toolbar.
     */
    ImageView toolBarIconDoctorActivity;
    /**
     * ProgressBar dell'intera activity.
     */
    ProgressBar progressBarDoctorActivty;

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
        progressBarDoctorActivty = findViewById(R.id.progressBarDoctorActivty);
        // verifico se il dottore è loggato
        if(getIntent().getBooleanExtra("logged",false))
        {
            openHomeFragment();
        }
        else
        {
            openSigningFragment();
        }
    }


    @Override
    protected void onStart() {
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

    public void qrcode()
    {
        ScanOptions scanCamera = new ScanOptions();
        scanCamera.setOrientationLocked(true);
        scanCamera.setCaptureActivity(CaptAct.class);
        scanResult.launch(scanCamera);
    }

    // Gestisco il risultato del qrcode.
    ActivityResultLauncher<ScanOptions> scanResult = registerForActivityResult(new ScanContract(), res->{
        if(res.getContents() != null){
            progressBarDoctorActivty.setVisibility(View.VISIBLE);
            CompletableFuture<List<String>> future = Dao.getAllDoctorsPatients(DoctorLogin.getUsername(), this);
            future.thenAccept(patients -> {
                this.runOnUiThread(() -> {
                    progressBarDoctorActivty.setVisibility(View.GONE);
                    if (patients.contains(res.getContents())) {
                        UserLogin.setUsername(res.getContents());
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        if (!(fragmentManager.findFragmentById(R.id.doctorFragmentView) instanceof SigninDoctorFragment)) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.doctorFragmentView, DetailUserDoctorFragment.class, null);
                            fragmentTransaction.commit();
                        }
                    } else {
                        Utility.showAlertDialog(this, getString(R.string.CameraScanErrorTitle), getString(R.string.CameraScanError));
                    }
                });
            });
        }
        Utility.showAlertDialog(this, getString(R.string.CameraScanErrorTitle), getString(R.string.CameraScanError));
    });

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