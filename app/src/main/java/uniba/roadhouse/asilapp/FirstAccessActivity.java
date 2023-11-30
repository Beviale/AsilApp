package uniba.roadhouse.asilapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;

public class FirstAccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_access_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.primoAccessoFragmentView, FirstAccessFragment.class, null);
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