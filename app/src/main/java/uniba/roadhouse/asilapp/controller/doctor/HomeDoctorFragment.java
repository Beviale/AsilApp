package uniba.roadhouse.asilapp.controller.doctor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.user.home.SettingsFragment;


public class HomeDoctorFragment extends Fragment {
    Toolbar toolbarDoctorActivity;
    TextView textToolbarDoctor;





    public HomeDoctorFragment() {
        // Required empty public constructor
    }


    public static HomeDoctorFragment newInstance(String param1, String param2) {
        HomeDoctorFragment fragment = new HomeDoctorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //--------RIFERIMENTI------------
        toolbarDoctorActivity = getActivity().findViewById(R.id.toolbarDoctorActivity);
        textToolbarDoctor = getActivity().findViewById(R.id.textToolbarDoctor);
        textToolbarDoctor.setText(getString(R.string.homeMenuScreen));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        toolbarDoctorActivity.setNavigationIcon(null);
        toolbarDoctorActivity.inflateMenu(R.menu.menu_home_activity);
        toolbarDoctorActivity.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.settings)
                {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(getString(R.string.settingsMenuScreen));
                    fragmentTransaction.replace(R.id.doctorFragmentView, SettingsFragment.class, null);
                    fragmentTransaction.commit();
                }

                return true;
            }
        });
        super.onResume();
    }
}