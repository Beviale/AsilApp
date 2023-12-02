package uniba.roadhouse.asilapp.view.firstaccess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.User;
import uniba.roadhouse.asilapp.view.home.HomeActivity;

public class FirstAccessActivity extends AppCompatActivity {
    public static Boolean dialogConnection=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_access_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        ImageView noConnectionIcon = findViewById(R.id.noConnectionIcon);


        if (!Utility.isConnectedToInternet(this)) {
            Utility.showAlertDialog(FirstAccessActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            noConnectionIcon.setVisibility(View.VISIBLE);
            dialogConnection=true;
        }


        final Handler handler = new Handler();
        final int delay = 4000; // 1000 milliseconds == 1 second
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

        noConnectionIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Utility.showAlertDialog(FirstAccessActivity.this, getString(R.string.noConnectionTitle), getString(R.string.noConnection));
            }
        });


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.primoAccessoFragmentView, FirstAccessFragment.class, null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Dao.checkIsLogged(this))
        {
            User.setUsername("ciao");
            Intent openHome = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(openHome);
        }
    }

    public void callRegisterFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.primoAccessoFragmentView, SignupFragment.class, null);
        fragmentTransaction.commit();
    }
}