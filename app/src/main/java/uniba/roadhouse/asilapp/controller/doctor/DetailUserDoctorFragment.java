package uniba.roadhouse.asilapp.controller.doctor;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import uniba.roadhouse.asilapp.controller.user.home.HealthHistoryFragment;
import uniba.roadhouse.asilapp.controller.user.home.MyPathologiesFragment;
import uniba.roadhouse.asilapp.controller.user.home.UserProfileFragment;


/**
 * Schermata contenente il TabLayout relativo all'utente selezionato dal Dottore.
 */
public class DetailUserDoctorFragment extends Fragment {
    Toolbar toolbar;
    /**
     * TabLayout per la selezione dei vari fragment da visualizzare.
     */
    TabLayout tabLayoutUserDoctor;
    /**
     * ProgressBar da mostrare durante il caricamento dei dati dal database.
     */
    ProgressBar progressBarDoctorActivty;
    /**
     * Indica se il fragment è stato aperto in quanto l'utente ha premuto il tasto indietro dal fragment HealthHistory.
     */
    private Boolean openBackHealthHistory = false;
    /**
     * Indica se il fragment è stato aperto in quanto l'utente ha premuto il tasto indietro dal fragment MyPathologies.
     */
    private Boolean openBackMyPathologies = false;



    public DetailUserDoctorFragment() {
    }


    public static DetailUserDoctorFragment newInstance(String param1, String param2) {
        DetailUserDoctorFragment fragment = new DetailUserDoctorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
        {
            openBackHealthHistory = getArguments().getBoolean("backHealthHistory");
            openBackMyPathologies = getArguments().getBoolean("backMyPathologies");
        }
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);


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

        openUserProfile();
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
                    openUserProfile();
                }
                else if(position==1)
                {
                    openHealthHistory();
                }
                else if(position==2)
                {
                    openMyPathologies();
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
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });

        if(openBackHealthHistory==true)
        {
            openBackHealthHistory=false;
            openHealthHistory();
            TabLayout.Tab tab = tabLayoutUserDoctor.getTabAt(1);
            tab.select();
        }
        else if(openBackMyPathologies==true)
        {
            openBackMyPathologies=false;
            openMyPathologies();
            TabLayout.Tab tab = tabLayoutUserDoctor.getTabAt(2);
            tab.select();

        }
        else
        {
            openUserProfile();
            TabLayout.Tab tab = tabLayoutUserDoctor.getTabAt(0);
            tab.select();
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        progressBarDoctorActivty.setVisibility(View.GONE);
        super.onPause();
    }


    /**
     * Apre il fragment "UserProfileFragment" passando, tramite bundle, la chiave "doctor" come true.
     */
    private void openUserProfile()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("doctor", true);
        fragmentTransaction.replace(R.id.userDoctorTabFragmentContainer, UserProfileFragment.class, bundle);
        fragmentTransaction.commit();
    }


    /**
     * Apre il fragment "openHealthHistory" passando, tramite bundle, la chiave "doctor" come true.
     */
    private void openHealthHistory()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("doctor", true);
        fragmentTransaction.replace(R.id.userDoctorTabFragmentContainer, HealthHistoryFragment.class, bundle);
        fragmentTransaction.commit();
    }


    /**
     * Apre il fragment "openMyPatholgogies" passando, tramite bundle, la chiave "doctor" come true.
     */
    private void openMyPathologies()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("doctor", true);
        fragmentTransaction.replace(R.id.userDoctorTabFragmentContainer, MyPathologiesFragment.class, bundle);
        fragmentTransaction.commit();

    }


    /**
     * Apre il fragment "HomeDoctorFragment" quando viene premuto il tasto indietro.
     */
    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.doctorFragmentView, HomeDoctorFragment.class, null);
            fragmentTransaction.commit();
        }
    };


}