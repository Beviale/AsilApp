package uniba.roadhouse.asilapp.controller.user.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Misurazione;

/**
 * Fragment che consente di visualizzare le ultime registrazioni dei vari parametri.
 */
public class HealthHistoryFragment extends Fragment {

    // Cliccabili dall'utente
    /**
     * View cliccabile relativa alla temperatura corporea.
     */
    ConstraintLayout bodyTemperatureView;
    /**
     * View cliccabile relativa alla pressione sanguignea.
     */
    ConstraintLayout bloodPressureView;
    /**
     * View cliccabile relativa al peso corporeo.
     */
    ConstraintLayout weightView;
    /**
     * View cliccabile relativa al battito cardiaco.
     */
    ConstraintLayout bpmView;
    /**
     * View cliccabile relativa al tremolio.
     */
    ConstraintLayout tremblingView;
    /**
     * View cliccabile relativo al glucosio.
     */
    ConstraintLayout glucoseView;
    /**
     * Layout che consente di effettuare il refresh della pagina.
     */
    SwipeRefreshLayout swipereFreshLayout;


    /**
     * ProgressBar di HomeActivity
     */
    ProgressBar homeActivityProgressBar;

    /**
     * View che viene cliccata dall'utente quando seleziona il menu contestuale. Permette di effettuare la condivisione dei dati.
     */
    View viewClickedContext;

    //Valutazioni
    /**
     * Valutazione della temperatura
     */
    TextView evalutationHealthHistoryTemperature;
    /**
     * Valutazione della pressione
     */
    TextView evalutationHealthHistoryBloodPressure;
    /**
     * Valutazione del peso
     */
    TextView evalutationHealthHistoryWeight;
    /**
     * Valutazione del battito cardiaco
     */
    TextView evalutationHealthHistoryBPM;
    /**
     * Valutazione del tremolio.
     */
    TextView evalutationHealthHistoryTrembling;
    /**
     * Valutazione del glucosio
     */
    TextView evalutationHealthHistoryGlucose;

    // Valore
    /**
     * Valore registrato per la temperatura.
     */
    TextView resultTemperature;
    /**
     * Valore registrato per la pressione massima.
     */
    TextView resultBloodPressureMax;
    /**
     * Valore registrato per la pressione minima
     */
    TextView resultBloodPressureMin;
    /**
     * Valore registrato per il peso
     */
    TextView resultWeight;
    /**
     * Valore registrato per il battito cardiaco
     */
    TextView resultBPM;
    /**
     * Valore registrato per il tremolio
     */
    TextView resultTrembling;
    /**
     * Valore registrato per il glucosio.
     */
    TextView resultGlucose;



    // ID
    /**
     * id della misurazione relativa alla temperatura
     */
    private int idTemperature;
    /**
     * id della misurazione relativa alla pressione
     */
    private int idBloodPressure;
    /**
     * id della misurazione relativa al peso
     */
    private int idWeight;
    /**
     * id della misurazione relativa al battito cardiaco
     */
    private int idBPM;
    /**
     * id della misurazione relativa al tremolio
     */
    private int idTrembling;
    /**
     * id della misurazione relativa al glucosio
     */
    private int idGlucose;




    public HealthHistoryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HealthHistoryFragment newInstance(String param1, String param2) {
        HealthHistoryFragment fragment = new HealthHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        bodyTemperatureView.setOnClickListener(v->openDetailFragment(idTemperature, false));
        bloodPressureView.setOnClickListener(v->openDetailFragment(idBloodPressure, false));
        weightView.setOnClickListener(v->openDetailFragment(idWeight, false));
        bpmView.setOnClickListener(v->openDetailFragment(idBPM, false));
        tremblingView.setOnClickListener(v->openDetailFragment(idTrembling, false));
        glucoseView.setOnClickListener(v->openDetailFragment(idGlucose, false));
        // Utilizzo del menu contestuale per la condivisione dei dati.
        registerForContextMenu(bodyTemperatureView);
        registerForContextMenu(bloodPressureView);
        registerForContextMenu(weightView);
        registerForContextMenu(bpmView);
        registerForContextMenu(tremblingView);
        registerForContextMenu(glucoseView);

        // Permette di aggiornare i dati dal database facendo una pull to refresh
        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipereFreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onPause() {
        homeActivityProgressBar.setVisibility(View.GONE);
        super.onPause();
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);
        super.onResume();
    }

    /**
     * Apre il fragment relativo alla singola misurazione.
     * @param id, identificativo della misurazione da visualizzare.
     * @param share, se true indica la volontà di avviare il menu di convidivisione all'apertura del nuovo fragment.
     */
    private void openDetailFragment(int id, Boolean share)
    {
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        CompletableFuture<Map<String, ?>> future = Dao.getMisuration(id, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                homeActivityProgressBar.setVisibility(View.GONE);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("itemClicked", ((Misurazione) result.get("misurazione")).getTipo().toString());
                bundle.putInt("id", id);
                bundle.putBoolean("share", share);
                fragmentTransaction.addToBackStack(getResources().getString(R.string.healthMenuScreen));
                fragmentTransaction.replace(R.id.homeContainerView, DetailHealthHistoryFragment.class, bundle);
                fragmentTransaction.commit();

            });
        });

    }


    /**
     * Acquisice dal database i dati di tutte le ultime misurazioni e compila le view apposite.
     */
    private void getData()
    {
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        swipereFreshLayout.setAlpha((float)0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getAllLastMisurationsUsername(Access.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                homeActivityProgressBar.setVisibility(View.GONE);
                swipereFreshLayout.setAlpha((float)1);
                //Disattivo la temperatura
                bodyTemperatureView.setEnabled(false);
                bodyTemperatureView.setAlpha((float)0.5);
                evalutationHealthHistoryTemperature.setText(getString(R.string.notRegistered));
                // Disattivo la pressione sanguigna
                bloodPressureView.setEnabled(false);
                bloodPressureView.setAlpha((float)0.5);
                evalutationHealthHistoryBloodPressure.setText(getString(R.string.notRegistered));
                // Disattivo il peso
                weightView.setEnabled(false);
                weightView.setAlpha((float)0.5);
                evalutationHealthHistoryWeight.setText(getString(R.string.notRegistered));
                // Disattivo il battito cardiaco
                bpmView.setEnabled(false);
                bpmView.setAlpha((float)0.5);
                evalutationHealthHistoryBPM.setText(getString(R.string.notRegistered));
                // Disattivo il tremolio
                tremblingView.setEnabled(false);
                tremblingView.setAlpha((float)0.5);
                evalutationHealthHistoryTrembling.setText(getString(R.string.notRegistered));
                // Disattivo il glucosio
                glucoseView.setEnabled(false);
                glucoseView.setAlpha((float)0.5);
                evalutationHealthHistoryGlucose.setText(getString(R.string.notRegistered));
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
                                bodyTemperatureView.setEnabled(true);
                                bodyTemperatureView.setAlpha((float)1.0);
                                break;
                            case PRESSIONESANGUIGNA:
                                evalutationHealthHistoryBloodPressure.setText(((Misurazione) result.get(key)).getValutazione());
                                resultBloodPressureMax.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValoreMax()))).concat("/"));
                                resultBloodPressureMin.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValoreMin()))));
                                idBloodPressure = ((Misurazione) result.get(key)).getId();
                                bloodPressureView.setEnabled(true);
                                bloodPressureView.setAlpha((float)1.0);
                                break;
                            case PESO:
                                evalutationHealthHistoryWeight.setText(((Misurazione) result.get(key)).getValutazione());
                                resultWeight.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idWeight = ((Misurazione) result.get(key)).getId();
                                weightView.setEnabled(true);
                                weightView.setAlpha((float)1.0);
                                break;
                            case BATTITOCARDIACO:
                                evalutationHealthHistoryBPM.setText(((Misurazione) result.get(key)).getValutazione());
                                resultBPM.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idBPM = ((Misurazione) result.get(key)).getId();
                                bpmView.setEnabled(true);
                                bpmView.setAlpha((float)1.0);
                                break;
                            case TREMOLIO:
                                evalutationHealthHistoryTrembling.setText(((Misurazione) result.get(key)).getValutazione());
                                resultTrembling.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idTrembling = ((Misurazione) result.get(key)).getId();
                                tremblingView.setEnabled(true);
                                tremblingView.setAlpha((float)1.0);
                                break;
                            case GLUCOSIO:
                                evalutationHealthHistoryGlucose.setText(((Misurazione) result.get(key)).getValutazione());
                                resultGlucose.setText(String.valueOf((int)(Math.round(((Misurazione)result.get(key)).getValore()))));
                                idGlucose = ((Misurazione) result.get(key)).getId();
                                glucoseView.setEnabled(true);
                                glucoseView.setAlpha((float)1.0);
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
        inflater.inflate(R.menu.share_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if((viewClickedContext.equals(getActivity().findViewById(R.id.bodyTemperatureView))))
        {
            openDetailFragment(idTemperature, true);
        }
        if((viewClickedContext.equals(getActivity().findViewById(R.id.bloodPressureView))))
        {
            openDetailFragment(idBloodPressure, true);
        }
        if((viewClickedContext.equals(getActivity().findViewById(R.id.weightView))))
        {
            openDetailFragment(idWeight, true);
        }
        if((viewClickedContext.equals(getActivity().findViewById(R.id.bpmView))))
        {
            openDetailFragment(idBPM, true);
        }
        if((viewClickedContext.equals(getActivity().findViewById(R.id.tremblingView))))
        {
            openDetailFragment(idTrembling, true);
        }
        if((viewClickedContext.equals(getActivity().findViewById(R.id.glucoseView))))
        {
            openDetailFragment(idGlucose, true);
        }
        return super.onContextItemSelected(item);
    }
}