package uniba.roadhouse.asilapp.controller.doctor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.tabs.TabLayout;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.user.home.HealthBoxFragment;
import uniba.roadhouse.asilapp.controller.user.home.HealthHistoryFragment;
import uniba.roadhouse.asilapp.controller.user.home.MyPathologiesFragment;
import uniba.roadhouse.asilapp.controller.user.home.UserProfileFragment;
import uniba.roadhouse.asilapp.model.dao.Access;


public class DetailUserDoctorFragment extends Fragment {
    Toolbar toolbar;
    TabLayout tabLayoutUserDoctor;
    ProgressBar progressBarDoctorActivty;



    public DetailUserDoctorFragment() {
        // Required empty public constructor
    }


    public static DetailUserDoctorFragment newInstance(String param1, String param2) {
        DetailUserDoctorFragment fragment = new DetailUserDoctorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_user_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //----------RIFERIMENTI-------------------
        toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);
        tabLayoutUserDoctor = view.findViewById(R.id.tabLayoutUserDoctor);
        progressBarDoctorActivty = getActivity().findViewById(R.id.progressBarDoctorActivty);


        openUserProfile(getActivity().getSupportFragmentManager());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        //-------------LISTENER----------------
        tabLayoutUserDoctor.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if(position==0)
                {
                    openUserProfile(fragmentManager);
                }
                else if(position==1)
                {
                    openHealthHistory(fragmentManager);
                }
                else if(position==2)
                {
                    openHealthPathologies(fragmentManager);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
            super.onStart();
    }

    @Override
    public void onResume() {
        toolbar.getMenu().clear();
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
        super.onResume();
    }

    @Override
    public void onPause() {
        progressBarDoctorActivty.setVisibility(View.GONE);
        super.onPause();
    }

    private void openUserProfile(FragmentManager fragmentManager)
    {
        Access.setUsername("asilapp");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("doctor", true);
        fragmentTransaction.replace(R.id.userDoctorTabFragmentContainer, UserProfileFragment.class, bundle);
        fragmentTransaction.commit();
    }

    private void openHealthHistory(FragmentManager fragmentManager)
    {
        Access.setUsername("asilapp");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("doctor", true);
        fragmentTransaction.replace(R.id.userDoctorTabFragmentContainer, HealthHistoryFragment.class, bundle);
        fragmentTransaction.commit();
    }

    private void openHealthPathologies(FragmentManager fragmentManager)
    {
        Access.setUsername("asilapp");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("doctor", true);
        fragmentTransaction.replace(R.id.userDoctorTabFragmentContainer, MyPathologiesFragment.class, bundle);
        fragmentTransaction.commit();

    }


}