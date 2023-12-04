package uniba.roadhouse.asilapp.view.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;

import uniba.roadhouse.asilapp.R;

public class HomeActivity extends AppCompatActivity {

    ImageView toolBarDownIconMedicalParameters;
    Boolean medicalParamtersOpen=false;
    Toolbar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.appBarColor));
        bottomBar=findViewById(R.id.toolbarHomeActivityDown);
        setSupportActionBar(bottomBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        toolBarDownIconMedicalParameters.setOnClickListener(v->openMedicalParamters());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.botton_menu_home,menu);
        return true;
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