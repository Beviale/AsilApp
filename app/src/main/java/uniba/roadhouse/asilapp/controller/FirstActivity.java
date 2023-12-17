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
import uniba.roadhouse.asilapp.controller.patient.signinSignup.SigninSingupActivity;

public class FirstActivity extends AppCompatActivity {

    ConstraintLayout layoutFirstActivity;
    Button accessPatientButton;
    Button accessDoctorButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_first);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColorFirstActivity));
        layoutFirstActivity = findViewById(R.id.layoutFirstActivity);
        accessPatientButton = findViewById(R.id.accessPatientButton);
        accessDoctorButton = findViewById(R.id.accessDoctorButton);
    }

    @Override
    protected void onStart() {

        accessPatientButton.setOnClickListener(v->openAccessPatient());
        accessDoctorButton.setOnClickListener(v->openAccessDoctor());

        AnimationDrawable animationDrawable = (AnimationDrawable) layoutFirstActivity.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        super.onStart();
    }


    private void openAccessPatient()
    {
        Intent intent = new Intent(this, SigninSingupActivity.class);
        startActivity(intent);
    }

    private void openAccessDoctor()
    {
        Intent intent = new Intent(this, DoctorActivity.class);
        startActivity(intent);
    }
}