package uniba.roadhouse.asilapp.view.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.controller.Utility;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Misurazione;

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
    ProgressBar homeActivityProgressBar;
    SwipeRefreshLayout swipereFreshLayout;
    Button share;
    View viewClickedContext;

    //Valutazione
    TextView evalutationHealthHistoryTemperature;
    TextView evalutationHealthHistoryBloodPressure;
    TextView evalutationHealthHistoryWeight;
    TextView evalutationHealthHistoryBPM;
    TextView evalutationHealthHistoryTrembling;
    TextView evalutationHealthHistoryGlucose;

    // Valore
    TextView resultTemperature;
    TextView resultBloodPressureMax;
    TextView resultBloodPressureMin;
    TextView resultWeight;
    TextView resultBPM;
    TextView resultTrembling;
    TextView resultGlucose;
    // ID
    private int idTemperature;
    private int idBloodPressure;
    private int idWeight;
    private int idBPM;
    private int idTrembling;
    private int idGlucose;




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
        fragment.setArguments(args);
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
        swipereFreshLayout = view.findViewById(R.id.swipereFreshLayout);
        homeActivityProgressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        swipereFreshLayout.setAlpha((float)0.5);
        // Valutazioni
        evalutationHealthHistoryTemperature = view.findViewById(R.id.evalutationHealthHistoryTemperature);
        evalutationHealthHistoryBloodPressure = view.findViewById(R.id.evalutationHealthHistoryBloodPressure);
        evalutationHealthHistoryWeight = view.findViewById(R.id.evalutationHealthHistoryWeight);
        evalutationHealthHistoryBPM = view.findViewById(R.id.evalutationHealthHistoryBPM);
        evalutationHealthHistoryTrembling = view.findViewById(R.id.evalutationHealthHistoryTrembling);
        evalutationHealthHistoryGlucose = view.findViewById(R.id.evalutationHealthHistoryGlucose);
        // Valori
        resultTemperature = view.findViewById(R.id.resultTemperature);
        resultBloodPressureMax = view.findViewById(R.id.resultBloodPressureMax);
        resultBloodPressureMin = view.findViewById(R.id.resultBloodPressureMin);
        resultWeight = view.findViewById(R.id.resultWeight);
        resultBPM = view.findViewById(R.id.resultBPM);
        resultTrembling = view.findViewById(R.id.resultTrembling);
        resultGlucose = view.findViewById(R.id.resultGlucose);

        getData();
        }

    @Override
    public void onStart() {
        super.onStart();
        //-----------LISTENER--------------
        bodyTemperatureView.setOnClickListener(v->openDetailFragment(getString(R.string.temperatureHealthHistory), idTemperature));
        bloodPressureView.setOnClickListener(v->openDetailFragment(getString(R.string.bloodPressureHealthHistory), idBloodPressure));
        weightView.setOnClickListener(v->openDetailFragment(getString(R.string.weightHealthHistory), idWeight));
        bpmView.setOnClickListener(v->openDetailFragment(getString(R.string.bpmHealthHistory), idBPM));
        tremblingView.setOnClickListener(v->openDetailFragment(getString(R.string.tremblingHealthHistory), idTrembling));
        glucoseView.setOnClickListener(v->openDetailFragment(getString(R.string.glucoseHealthHistory), idGlucose));
        registerForContextMenu(bodyTemperatureView);
        registerForContextMenu(bloodPressureView);
        registerForContextMenu(weightView);
        registerForContextMenu(bpmView);
        registerForContextMenu(tremblingView);
        registerForContextMenu(glucoseView);
        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
               swipereFreshLayout.setRefreshing(false);
            }
        });

    }

    private void openDetailFragment(String clicked, int id)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("itemClicked", clicked);
        bundle.putInt("id", id);
        bundle.putBoolean("share", false);
        fragmentTransaction.addToBackStack(getResources().getString(R.string.healthMenuScreen));
        fragmentTransaction.replace(R.id.homeContainerView, DetailHealthHistoryFragment.class, bundle);
        fragmentTransaction.commit();
    }


    private void getData()
    {
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        swipereFreshLayout.setAlpha((float)0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getAllLastMisurationsUsername(Access.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                homeActivityProgressBar.setVisibility(View.GONE);
                swipereFreshLayout.setAlpha((float)1);
                for(String key: result.keySet())
                {
                    if(!key.equals("esito"))
                    {
                        switch(TipoMisurazioneEnum.valueOf(key))
                        {
                            case TEMPERATURA:
                                evalutationHealthHistoryTemperature.setText(((Misurazione) result.get(key)).getValutazione());
                                resultTemperature.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))).concat("°"));
                                idTemperature = ((Misurazione) result.get(key)).getId();
                                break;
                            case PRESSIONE:
                                evalutationHealthHistoryBloodPressure.setText(((Misurazione) result.get(key)).getValutazione());
                                resultBloodPressureMax.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValoreMax()))).concat("/"));
                                resultBloodPressureMin.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValoreMin()))));
                                idBloodPressure = ((Misurazione) result.get(key)).getId();
                                break;
                            case PESO:
                                evalutationHealthHistoryWeight.setText(((Misurazione) result.get(key)).getValutazione());
                                resultWeight.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idWeight = ((Misurazione) result.get(key)).getId();
                                break;
                            case BATTITOCARDIACO:
                                evalutationHealthHistoryBPM.setText(((Misurazione) result.get(key)).getValutazione());
                                resultBPM.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idBPM = ((Misurazione) result.get(key)).getId();
                                break;
                            case TREMOLIO:
                                evalutationHealthHistoryTrembling.setText(((Misurazione) result.get(key)).getValutazione());
                                resultTrembling.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idTrembling = ((Misurazione) result.get(key)).getId();
                                break;
                            case GLUCOSIO:
                                evalutationHealthHistoryGlucose.setText(((Misurazione) result.get(key)).getValutazione());
                                resultGlucose.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idGlucose = ((Misurazione) result.get(key)).getId();
                                break;
                        }
                    }
                    else if(!result.get(key).equals(getActivity().getString(R.string.misurationGetSuccessfully)))
                    {
                        getActivity().onBackPressed();
                        Toast.makeText(getActivity(), result.get(key).toString(), Toast.LENGTH_LONG).show();
                    }
                }

            });
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        viewClickedContext = v;
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.health_history_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }
}