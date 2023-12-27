package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.doctor.NewPathologyFragment;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Patologia;

/**
 * Schermata che contiene l'elenco di tutte le patologie associate all'utente e registrate dal dottore.
 * Può essere aperto sia tramite account utente che dottore.
 * L'utente può solo visualizzare l'elenco delle patologie, mentre il dottore può anche eseguire azioni di rimozione e aggiunta.
 */
public class MyPathologiesFragment extends Fragment {
    /**
     * Bottone che consente l'aggiunta di una nuova patologia. [Account dottore]
     */
    FloatingActionButton openNewPathology;
    /**
     * Layout dell'intero fragment.
     */
    LinearLayout linearLayoutMyPathologies;
    /**
     * ProgressBar da mostrare durante il caricamento delle patologie dal database.
     */
    ProgressBar progressBar;
    /**
     * Consente lo "swipe-to-refresh" dell'intero fragment.
     */
    SwipeRefreshLayout swipereFreshMyPathologies;

    /**
     * Indica se il fragment è stato aperto con un account dottore.
     */
    private static Boolean openDoctor=false;

    /**
     * Le chiavi sono le view (ovvero le patologie) mentre i valori i rispettivi nomi.
     */
    private static HashMap<View, String> mapViewNamePathology;

    /**
     * View (ovvero patologia) cliccata dall'utente o dal dottore tramite menu contestuale.
     */
    private static View pathologyClicked;



    public MyPathologiesFragment() {
    }


    public static MyPathologiesFragment newInstance(String param1, String param2) {
        MyPathologiesFragment fragment = new MyPathologiesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments()!=null)
        {
            openDoctor = getArguments().getBoolean("doctor");
        }
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
        linearLayoutMyPathologies = view.findViewById(R.id.linearLayoutMyPathologies);
        swipereFreshMyPathologies = view.findViewById(R.id.swipereFreshMyPathologies);
        openNewPathology = view.findViewById(R.id.buttonOpenNewPathology);
        if(openDoctor==false)
        {
            progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        }
        if(openDoctor==true)
        {
            progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        }
        // Attivo il pulsante di aggiunta patologia per l'account dottore.
        if(openDoctor==true)
        {
            openNewPathology.setVisibility((View.VISIBLE));
        }
        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
        //--------------LISTENER--------------
        openNewPathology.setOnClickListener(v->openNewPathologyFragment());
        swipereFreshMyPathologies.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                linearLayoutMyPathologies.removeAllViews();
                swipereFreshMyPathologies.setRefreshing(false);
            }
        });
    }


    @Override
    public void onResume() {
        if(openDoctor==false)
        {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(null);
        }
        if(openDoctor==true)
        {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);
            toolbar.getMenu().clear();
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
       progressBar.setVisibility(View.GONE);

    }


    /**
     * Apre il fragment "DetailMyPathologiesFragment" che consente la visualizzazione in dettaglio di una specifica patologia,
     * @param share, se all'apertura del fragment bisogna mostrare la finestra di dialog relativa alla condivisione dei dati.
     * @param nomePatologia, nome della patologia da visualizzare.
     */
    private void openDetailFragment(Boolean share, String nomePatologia)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(openDoctor==false)
        {
            fragmentTransaction.addToBackStack(getResources().getString(R.string.healthMenuScreen));
            Bundle bundle = new Bundle();
            bundle.putBoolean("share", share);
            bundle.putString("namePathology", nomePatologia);
            bundle.putBoolean("doctor", openDoctor);
            fragmentTransaction.addToBackStack(getResources().getString(R.string.healthMenuScreen));
            fragmentTransaction.replace(R.id.homeContainerView, DetailMyPathologiesFragment.class, bundle);
        }
        if(openDoctor==true)
        {
            Bundle bundle = new Bundle();
            bundle.putBoolean("share", share);
            bundle.putString("namePathology", nomePatologia);
            bundle.putBoolean("doctor", openDoctor);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.doctorFragmentView, DetailMyPathologiesFragment.class, bundle);
        }
        fragmentTransaction.commit();
    }


    /**
     * Apre il fragment "NewPathologyFragment" che consente l'aggiunta di una nuova patologia [Account dottore]
     */
    private void openNewPathologyFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(getResources().getString(R.string.healthMenuScreen));
        fragmentTransaction.replace(R.id.doctorFragmentView, NewPathologyFragment.class, null);
        fragmentTransaction.commit();

    }

    /**
     * Prende i dati delle patologie dal database. Per ogni patologia, crea dinamicamente una view.
     */
    @SuppressLint("RestrictedApi")
    private void getData()
    {
        mapViewNamePathology = new HashMap<View, String>();
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutMyPathologies.setAlpha((float)0.5);
        CompletableFuture<Map<String, Object>> future = Dao.getAllPatologies(UserLogin.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                linearLayoutMyPathologies.setAlpha((float)1.0);
                if(getActivity()==null)
                {
                    return;
                }
                if(!(result.get("esito").toString().equals(getString(R.string.patologiesGetSuccessfull))))
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.titillium_web_bold);
                    List<Patologia> patologie = (List<Patologia>)result.get("patologie");
                    if(patologie.size()==0)
                    {
                        TextView emptyMyPathologies = new TextView(getActivity());
                        emptyMyPathologies.setText(getString(R.string.emptyMyPathologies));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,50,0,0);
                        params.gravity = Gravity.CENTER_HORIZONTAL;
                        emptyMyPathologies.setLayoutParams(params);
                        emptyMyPathologies.setTypeface(typeface);
                        linearLayoutMyPathologies.addView(emptyMyPathologies);
                        return;
                    }
                    for(Patologia patologia: patologie)
                    {
                        // Creo il constraintLayout
                        ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
                        constraintLayout.setOnClickListener(v->openDetailFragment(false, patologia.getPatologia()));
                        mapViewNamePathology.put(constraintLayout, patologia.getPatologia());
                        registerForContextMenu(constraintLayout);
                        constraintLayout.setId(View.generateViewId());
                        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                getResources().getDimensionPixelSize(R.dimen.heightHealthHistory)
                        );
                        layoutParams.topMargin=getResources().getDimensionPixelSize(R.dimen.marginBetweenInputs);
                        layoutParams.leftMargin=getResources().getDimensionPixelSize(R.dimen.marginLeftMyPathologies);
                        layoutParams.rightMargin=getResources().getDimensionPixelSize(R.dimen.marginRightMyPathologies);
                        constraintLayout.setLayoutParams(layoutParams);
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.colorHealthHistory));
                        linearLayoutMyPathologies.addView(constraintLayout);
                        // Animazione al click
                        Utility.activeAnimationOnClick(getActivity(), constraintLayout);

                        // Creo la TextView relativa al nome della patologia
                        TextView namePatology = new TextView(getActivity());
                        namePatology.setText(patologia.getPatologia());
                        namePatology.setId(View.generateViewId());
                        ConstraintLayout.LayoutParams paramsNamePathology = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                        );
                        namePatology.setTypeface(typeface);
                        namePatology.setTextColor(getResources().getColor(R.color.white));
                        namePatology.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.textTitleHealthHistory));
                        namePatology.setLayoutParams(paramsNamePathology);
                        constraintLayout.addView(namePatology);
                        ConstraintSet constraintSetNamePathology = new ConstraintSet();
                        constraintSetNamePathology.clone(constraintLayout);
                        constraintSetNamePathology.connect(namePatology.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 20));
                        constraintSetNamePathology.connect(namePatology.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) dpToPx(getContext(), 10));
                        constraintSetNamePathology.applyTo(constraintLayout);


                        // Creo la TextView relativa al label della data dell'ultima visita
                        TextView lastVisitLabel = new TextView(getActivity());
                        lastVisitLabel.setText(getString(R.string.lastVisitLabel));
                        lastVisitLabel.setId(View.generateViewId());
                        ConstraintLayout.LayoutParams paramsLastVisitLabel = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                        );
                        lastVisitLabel.setTextColor(getResources().getColor(R.color.white));
                        lastVisitLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) dpToPx(getContext(), 13));
                        lastVisitLabel.setLayoutParams(paramsLastVisitLabel);
                        constraintLayout.addView(lastVisitLabel);
                        ConstraintSet constraintSetLastVisitLabel = new ConstraintSet();
                        constraintSetLastVisitLabel.clone(constraintLayout);
                        constraintSetLastVisitLabel.connect(lastVisitLabel.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 20));
                        constraintSetLastVisitLabel.connect(lastVisitLabel.getId(), ConstraintSet.TOP, namePatology.getId(), ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 2));
                        constraintSetLastVisitLabel.applyTo(constraintLayout);



                        // Creo la TextView relativa alla data dell'ultima visita
                        TextView lastVisitData = new TextView(getActivity());
                        lastVisitData.setText(patologia.getData());
                        lastVisitData.setId(View.generateViewId());
                        ConstraintLayout.LayoutParams paramsLastVisitData = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                        );
                        lastVisitData.setTextColor(getResources().getColor(R.color.white));
                        lastVisitData.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) dpToPx(getContext(), 13));
                        lastVisitData.setLayoutParams(paramsLastVisitData);
                        constraintLayout.addView(lastVisitData);
                        ConstraintSet constraintSetLastVisitData = new ConstraintSet();
                        constraintSetLastVisitData.clone(constraintLayout);
                        constraintSetLastVisitData.connect(lastVisitData.getId(), ConstraintSet.LEFT, lastVisitLabel.getId(), ConstraintSet.RIGHT, (int) dpToPx(getContext(), 3));
                        constraintSetLastVisitData.connect(lastVisitData.getId(), ConstraintSet.TOP, namePatology.getId(), ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 2));
                        constraintSetLastVisitData.applyTo(constraintLayout);


                        // Creo la TextView relativa al label della priorità.
                        TextView priorityLabel = new TextView(getActivity());
                        priorityLabel.setText(getString(R.string.priorityLastVisitMyPathologiesLabel));
                        priorityLabel.setId(View.generateViewId());
                        ConstraintLayout.LayoutParams paramsPriorityLabel = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                        );
                        priorityLabel.setTextColor(getResources().getColor(R.color.white));
                        priorityLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) dpToPx(getContext(), 13));
                        priorityLabel.setLayoutParams(paramsPriorityLabel);
                        constraintLayout.addView(priorityLabel);
                        ConstraintSet constraintSetPriorityLabel = new ConstraintSet();
                        constraintSetPriorityLabel.clone(constraintLayout);
                        constraintSetPriorityLabel.connect(priorityLabel.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 20));
                        constraintSetPriorityLabel.connect(priorityLabel.getId(), ConstraintSet.TOP, lastVisitLabel.getId(), ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 2));
                        constraintSetPriorityLabel.applyTo(constraintLayout);


                        // Creo la TextView relativa alla priorità.
                        TextView priorityResult = new TextView(getActivity());
                        priorityResult.setText(patologia.getPriorita());
                        priorityResult.setId(View.generateViewId());
                        ConstraintLayout.LayoutParams paramsPriorityResult= new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                        );
                        priorityResult.setTextColor(getResources().getColor(R.color.white));
                        priorityResult.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) dpToPx(getContext(), 13));
                        priorityResult.setLayoutParams(paramsPriorityResult);
                        constraintLayout.addView(priorityResult);
                        ConstraintSet constraintSetPriorityResult = new ConstraintSet();
                        constraintSetPriorityResult.clone(constraintLayout);
                        constraintSetPriorityResult.connect(priorityResult.getId(), ConstraintSet.LEFT, priorityLabel.getId(), ConstraintSet.RIGHT, (int) dpToPx(getContext(), 3));
                        constraintSetPriorityResult.connect(priorityResult.getId(), ConstraintSet.TOP, lastVisitLabel.getId(), ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 2));
                        constraintSetPriorityResult.applyTo(constraintLayout);
                    }
                }

            });
        });
    }


    /**
     * Menu contestuale relativo alla pressione prolungata di una View (ovvero di una patologia).
     * Il menu contestuale dell'account utente permette solo la condivisione dei dati di una patologia.
     * Il menu contestuale dell'account dottore permette sia la condivisione dei dati che la rimozione di una patologia.
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        if(openDoctor==false){
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.share_menu, menu);
        }
        if(openDoctor==true){
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.share_and_delete_menu, menu);
        }
        pathologyClicked=v;
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if(fragmentManager.findFragmentById(R.id.doctorFragmentView) instanceof DetailMyPathologiesFragment)
        {
            return false;
        }
        if(fragmentManager.findFragmentById(R.id.homeContainerView) instanceof DetailMyPathologiesFragment)
        {
            return false;
        }
        if(item.getItemId()==R.id.action_share || item.getItemId()==R.id.action_share_menu)
            openDetailFragment(true, mapViewNamePathology.get(pathologyClicked));
        if(item.getItemId()==R.id.action_delete_menu)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyleCritical);

            builder.setTitle(getString(R.string.deleteMessageTitle))
                    .setMessage(getString(R.string.deleteMessage))
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressBar.setVisibility(View.VISIBLE);
                            linearLayoutMyPathologies.setAlpha((float)0.5);
                            CompletableFuture<String> future = Dao.deletePatology(UserLogin.getUsername(), mapViewNamePathology.get(pathologyClicked), getActivity());
                            future.thenAccept(result -> {
                                getActivity().runOnUiThread(() -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    linearLayoutMyPathologies.setAlpha((float)1.0);
                                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                    if(result.equals(getString(R.string.editPatologySuccessfull)))
                                    {
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("doctor", true);
                                        fragmentTransaction.replace(R.id.userDoctorTabFragmentContainer, MyPathologiesFragment.class, bundle);
                                        fragmentTransaction.commit();
                                    }
                                });
                            });

                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onContextItemSelected(item);
    }
}