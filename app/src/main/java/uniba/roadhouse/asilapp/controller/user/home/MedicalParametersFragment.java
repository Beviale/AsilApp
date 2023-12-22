package uniba.roadhouse.asilapp.controller.user.home;

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

import com.google.android.material.tabs.TabLayout;

import uniba.roadhouse.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicalParametersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicalParametersFragment extends Fragment {


    TabLayout tabLayoutMedical;

    /**
     * Indica se il fragment è stato aperto in quanto l'utente ha premuto il tasto indietro dal fragment MyPathologies.
     */
    private static Boolean openBackMyPathologies = false;





    public MedicalParametersFragment() {
    }


    public static MedicalParametersFragment newInstance(String param1, String param2) {
        MedicalParametersFragment fragment = new MedicalParametersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
        {
            openBackMyPathologies = getArguments().getBoolean("backHealthHistory");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.medical_parameters_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        openHealthHistory(fragmentManager);
        tabLayoutMedical = view.findViewById(R.id.tabLayoutMedical);
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        tabLayoutMedical.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0)
                {
                    openHealthHistory(fragmentManager);
                }
                else
                {
                    openMyPahtologies(fragmentManager);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onPause() {
        getActivity().findViewById(R.id.homeActivityProgressBar).setVisibility(View.GONE);
        super.onPause();
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);

        if(openBackMyPathologies==true)
        {
            openBackMyPathologies=false;
            openHealthHistory(getActivity().getSupportFragmentManager());
            TabLayout.Tab tab = tabLayoutMedical.getTabAt(1);
            tab.select();
        }
        else
        {
            openHealthHistory(getActivity().getSupportFragmentManager());
            TabLayout.Tab tab = tabLayoutMedical.getTabAt(0);
            tab.select();
        }
        super.onResume();
    }



    private void openHealthHistory(FragmentManager fragmentManager)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.medicalTabFragmentContainer,HealthHistoryFragment.class, null);
        fragmentTransaction.commit();
    }
    private void openMyPahtologies(FragmentManager fragmentManager)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.medicalTabFragmentContainer, MyPathologiesFragment.class, null);
        fragmentTransaction.commit();
    }



}
