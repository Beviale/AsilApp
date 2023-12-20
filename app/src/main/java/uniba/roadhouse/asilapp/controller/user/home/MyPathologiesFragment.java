package uniba.roadhouse.asilapp.controller.user.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uniba.roadhouse.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPathologiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPathologiesFragment extends Fragment {

    ConstraintLayout diabetisLayout;
    ConstraintLayout dermatitisLayout;
    ConstraintLayout bronchitisLayout;
    FloatingActionButton openNewPathology;



    public MyPathologiesFragment() {
    }


    public static MyPathologiesFragment newInstance(String param1, String param2) {
        MyPathologiesFragment fragment = new MyPathologiesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_pathologies_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //---------RIFERIMENTI------------
        diabetisLayout = view.findViewById(R.id.diabetisLayout);
        dermatitisLayout = view.findViewById(R.id.dermatitisLayout);
        bronchitisLayout = view.findViewById(R.id.bronchitisLayout);
        openNewPathology = view.findViewById(R.id.buttonOpenNewPathology);
    }

    @Override
    public void onStart() {
        super.onStart();
        //--------------LISTENER--------------
        diabetisLayout.setOnClickListener(v->openDetailFragment(getString(R.string.diabete)));
        dermatitisLayout.setOnClickListener(v->openDetailFragment(getString(R.string.dermatitis)));
        bronchitisLayout.setOnClickListener(v->openDetailFragment(getString(R.string.brochitis)));
        openNewPathology.setOnClickListener(v->openNewPathologyFragment());
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);
        super.onResume();
    }

    private void openDetailFragment(String itemClicked)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("itemClicked", itemClicked);
        fragmentTransaction.addToBackStack(getResources().getString(R.string.healthMenuScreen));
        fragmentTransaction.replace(R.id.homeContainerView, DetailMyPathologiesFragment.class, bundle);
        fragmentTransaction.commit();
    }

    private void openNewPathologyFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(getResources().getString(R.string.healthMenuScreen));
        fragmentTransaction.replace(R.id.homeContainerView, NewPathologyFragment.class, null);
        fragmentTransaction.commit();

    }
}