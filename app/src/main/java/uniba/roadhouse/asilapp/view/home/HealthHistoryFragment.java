package uniba.roadhouse.asilapp.view.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uniba.roadhouse.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthHistoryFragment extends Fragment {
    ConstraintLayout bodyTemperatureView;
    ConstraintLayout bloodPressureView;
    ConstraintLayout weightView;
    ConstraintLayout bpmView;
    ConstraintLayout tremblingView;
    ConstraintLayout glucoseView;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HealthHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HealthHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthHistoryFragment newInstance(String param1, String param2) {
        HealthHistoryFragment fragment = new HealthHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.health_history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //-----------RIFERIMENTI-----------
        bodyTemperatureView = view.findViewById(R.id.bodyTemperatureView);
        bloodPressureView = view.findViewById(R.id.bloodPressureView);
        weightView = view.findViewById(R.id.weightView);
        bpmView = view.findViewById(R.id.bpmView);
        tremblingView = view.findViewById(R.id.tremblingView);
        glucoseView = view.findViewById(R.id.glucoseView);
    }

    @Override
    public void onStart() {
        super.onStart();
        //-----------LISTENER--------------
        bodyTemperatureView.setOnClickListener(v->openDetailFragment("temperature"));
        bloodPressureView.setOnClickListener(v->openDetailFragment("bloodPressure"));
        weightView.setOnClickListener(v->openDetailFragment("weight"));
        bpmView.setOnClickListener(v->openDetailFragment("bpm"));
        tremblingView.setOnClickListener(v->openDetailFragment("treambling"));
        glucoseView.setOnClickListener(v->openDetailFragment("glucose"));

    }

    private void openDetailFragment(String clicked)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("itemClicked", clicked);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.homeContainerView, DetailHealthHistoryFragment.class, bundle);
        fragmentTransaction.commit();
    }
}