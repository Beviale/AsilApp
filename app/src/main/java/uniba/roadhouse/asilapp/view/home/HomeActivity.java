package uniba.roadhouse.asilapp.view.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import uniba.roadhouse.asilapp.R;

public class HomeActivity extends AppCompatActivity {

    ImageView toolBarDownIconMedicalParameters;
    Boolean medicalParamtersOpen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        toolBarDownIconMedicalParameters = findViewById(R.id.toolBarDownIconMedicalParameters);
    }

    @Override
    protected void onStart() {
        super.onStart();
        toolBarDownIconMedicalParameters.setOnClickListener(v->openMedicalParamters());
    }

    private void openMedicalParamters()
    {
        if(medicalParamtersOpen==false)
        {
            toolBarDownIconMedicalParameters.setImageResource(R.mipmap.heart_pressed);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.homeContainerView, MedicalParametersFragment.class, null);
            fragmentTransaction.commit();
            medicalParamtersOpen=true;
        }
        else
        {
            toolBarDownIconMedicalParameters.setImageResource(R.mipmap.heart);
        }
    }
}