package uniba.roadhouse.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;

import uniba.roadhouse.asilapp.R;

public class SigninSignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        setContentView(R.layout.signin_singup_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.primoAccessoFragmentView, firstAccessFragment.class, null);
        fragmentTransaction.commit();
    }

    public void callRegisterFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.primoAccessoFragmentView, SignupFragment.class, null);
        fragmentTransaction.commit();
    }
}