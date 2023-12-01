package uniba.roadhouse.asilapp.view.firstaccess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Dao;

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