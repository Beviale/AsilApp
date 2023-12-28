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
 * Fragment del profilo sanitario.
 * Contiene un tabLayout che permette di visualizzare due fragment molto importanti: HealthHistoryFragment e MyPathologiesFragment, rispettivamente relativi allo storico salute e alle patologie.
 * Può essere aprto sia tramite account utente che dottore.
 */
public class MedicalParametersFragment extends Fragment {
    /**
     * TabLayout che consente di visuializzare HealthHistoryFragment e MyPathologiesFragment.
     */
    TabLayout tabLayoutMedical;
    /**
     * Indica se il fragment è stato aperto in quanto l'utente ha premuto il tasto indietro dal fragment MyPathologies. [Account Dottore]
     */
    private Boolean openBackMyPathologies = false;


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
        openHealthHistory();
        //---------RIFERIMENTI----------------------
        tabLayoutMedical = view.findViewById(R.id.tabLayoutMedical);
    }

    @Override
    public void onStart() {
        super.onStart();
        //--------------LISTENER----------------
        tabLayoutMedical.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0)
                {
                    openHealthHistory();
                }
                else
                {
                    openMyPahtologies();
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
            openHealthHistory();
            TabLayout.Tab tab = tabLayoutMedical.getTabAt(1);
            tab.select();
        }
        else
        {
            openHealthHistory();
            TabLayout.Tab tab = tabLayoutMedical.getTabAt(0);
            tab.select();
        }
        super.onResume();
    }


    /**
     * Apre il fragment "HealthHistoryFragment", ossia lo storico salute.
     */
    private void openHealthHistory()
    {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.medicalTabFragmentContainer, HealthHistoryFragment.class, null);
        fragmentTransaction.commit();
    }


    /**
     * Apre il fragment "MyPathologiesFragment", ossia quello relativo alle patologie.
     */
    private void openMyPahtologies()
    {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.medicalTabFragmentContainer, MyPathologiesFragment.class, null);
        fragmentTransaction.commit();
    }

}
