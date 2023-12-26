package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.doctor.DetailUserDoctorFragment;
import uniba.roadhouse.asilapp.controller.doctor.EditDoctorNotesDialogFragment;
import uniba.roadhouse.asilapp.controller.doctor.EditEvalutationDialogFragment;
import uniba.roadhouse.asilapp.controller.doctor.EditPriorityDialogFragment;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Misurazione;

/**
 * Fragment che permette la visualizzazione in dettaglio dell'ultima misurazione di un determinato parametro,
 * Può essere aperto sia tramite account utente che dottore.
 */
public class DetailHealthHistoryFragment extends Fragment implements EditEvalutationDialogFragment.closeListenerEditEvalutation, EditDoctorNotesDialogFragment.closeEditDoctorNotes {
    // Da inizializzare con in valori presenti nel database
    /**
     * id dell'ultima misurazione
     */
    TextView idLastRecordHealthHistory;
    /**
     * data dell'ultima misurazione
     */
    TextView dateLastRecordHealthHistory;
    /**
     * ora dell'ultima misurazione
     */
    TextView timeLastRecordHealthHistory;
    /**
     * valore registrato nell'ultima misurazione
     */
    TextView valueLastRecordHealthHistory;
    /**
     * valutazione dell'ultima misurazione
     */
    TextView evalutationLastRecordHealthHistory;


    /**
     * Icona che consente la condivisione dei dati della misurazione.
     */
    ImageView shareDetailHealthHistory;
    /**
     * Titolo dell'ultima misurazione. Consiste essenzialmente nel tipo.
     */
    TextView detailHealthHistoryTitle;
    /**
     * EditText contenente le note del medico
     */
    EditText doctorNotesLastRecordHealthHistory;
    /**
     * ProgressBar da mostrare per il caricamento dei dati dal database relativi all'ultima misurazione.
     */
    ProgressBar homeActivityProgressBar;
    /**
     * Layout che è possibile oscurare durante il caricamento dei dati dal database relativi all'ultima misurazione
     */
    ConstraintLayout detailHealthHistoryLayout;
    /**
     * Consente la modifica della valutazione da parte del dottore.
     */
    ImageView editButtonHealthHistoryEvalutation;
    /**
     * Consente la modifica delle note da parte del dottore.
     */
    ImageView editButtonHealthHistoryDoctorNotes;

    /**
     * is dell'ultima misurazione
     */
    private static int id;
    /**
     * Se il fragment è stato aperto con l'intenzione di condividere i dati,
     */
    private static Boolean share;

    /**
     * Indica se il fragment è stato aperto con l'account dottore.
     */
    private static Boolean openDoctor=false;
    /**
     * Request code per la modifica delle valutazione [Account dottore]
     */
    private static final Integer REQUEST_CODE_EDIT_EVALUTATION=1;
    /**
     * Request code per la modifica delle note [Account dottore]
     */
    private static final Integer REQUEST_CODE_EDIT_DOCTOR_NOTES=2;
    /**
     * Consente lo "swipe-to-refresh" dei dati.
     */
    SwipeRefreshLayout swipereFreshLayoutDetailHealthHistory;






    //Vecchie misurazioni
    /**
     * ProgressBar da mostrare per il caricamento dei dati dal database relativi alle misurazioni precedenti.
     */
    ProgressBar oldProgressBar;
    /**
     * Layout da oscurare per il caricamento dei dati dal database relativi alle misurazioni precedenti.
     */
    LinearLayout layoutOldHealthHistory;
    /**
     * Tipo di misurazione da mostrare espresso sottoforma di TipoMisurazioneEnum.
     */
    private static TipoMisurazioneEnum itemClicked;
    /**
     * Tipo di misurazione da mostrare espresso sottoforma di stringa.
     */
    private static String itemClickedString;
    /**
     * Rappresenta la view di una vecchia misurazione cliccata dall'utente con il menu contestuale.
     */
    private static View itemOldClicked;
    /**
     * Associa a ogni constraintLayout delle vecchie misurazione il rispettivo id.
     */
    private static HashMap<View, Integer> mappaViewIdMisurazioneOld;




    public DetailHealthHistoryFragment() {
    }


    public static DetailHealthHistoryFragment newInstance() {
        DetailHealthHistoryFragment fragment = new DetailHealthHistoryFragment();
        return fragment;
    }

    /**
     * Prende dal fragment precedente l'id della misurazione da mostrare, il tipo sottoforma di stringa e il booleano di condivisione.
     * Se vero, significa che deve essere avviato il dialog di condivisione dei dati,
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemClickedString = getArguments().getString("itemClicked");
            id = getArguments().getInt("id");
            share = getArguments().getBoolean("share");
            openDoctor = getArguments().getBoolean("doctor");
        }
        if(openDoctor==true)
        {
            getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallbackDoctor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_health_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //-----------RIFERIMENTI-------------
        shareDetailHealthHistory = view.findViewById(R.id.shareDetailHealthHistory);
        doctorNotesLastRecordHealthHistory = view.findViewById(R.id.doctorNotesLastRecordHealthHistory);
        Utility.enableScroll(doctorNotesLastRecordHealthHistory);
        detailHealthHistoryTitle = view.findViewById(R.id.detailHealthHistoryTitle);
        layoutOldHealthHistory = view.findViewById(R.id.layoutOldHealthHistory);
        detailHealthHistoryLayout = view.findViewById(R.id.detailHealthHistoryLayout);
        if(openDoctor==false)
        {
            homeActivityProgressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        }
        if(openDoctor==true)
        {
            homeActivityProgressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        }
        valueLastRecordHealthHistory = view.findViewById(R.id.valueLastRecordHealthHistory);
        idLastRecordHealthHistory = view.findViewById(R.id.idLastRecordHealthHistory);
        dateLastRecordHealthHistory = view.findViewById(R.id.dateLastRecordHealthHistory);
        oldProgressBar = view.findViewById(R.id.oldProgressBar);
        swipereFreshLayoutDetailHealthHistory = view.findViewById(R.id.swipereFreshLayoutDetailHealthHistory);
        timeLastRecordHealthHistory = view.findViewById(R.id.timeLastRecordHealthHistory);
        evalutationLastRecordHealthHistory = view.findViewById(R.id.evalutationLastRecordHealthHistory);
        editButtonHealthHistoryEvalutation = view.findViewById(R.id.editButtonHealthHistoryEvalutation);
        editButtonHealthHistoryDoctorNotes = view.findViewById(R.id.editButtonHealthHistoryDoctorNotes);
        // Se il fragment è stato aperto con l'account dottore, attivo i pulsanti di modifica dei dati.
        if(openDoctor==true)
        {
            editButtonHealthHistoryEvalutation.setVisibility((View.VISIBLE));
            editButtonHealthHistoryDoctorNotes.setVisibility((View.VISIBLE));
        }
        getData();
        getOldData();
    }

    @Override
    public void onStart() {
        super.onStart();
        //----------LISTENER------------
        shareDetailHealthHistory.setOnClickListener(v->showCheckboxDialogForSharePrivacy());
        editButtonHealthHistoryEvalutation.setOnClickListener(v->openDialogEditEvalutation());
        editButtonHealthHistoryDoctorNotes.setOnClickListener(v->openDialogEditDoctorNotes());
        swipereFreshLayoutDetailHealthHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                getOldData();
                layoutOldHealthHistory.removeAllViews();
                swipereFreshLayoutDetailHealthHistory.setRefreshing(false);
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
        Toolbar toolbar = null;
        if(openDoctor==false)
        {
             toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        }

        if(openDoctor==true)
        {
            toolbar = getActivity().findViewById(R.id.toolbarDoctorActivity);
        }
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


    /**
     * Apre il dialog di condivisione dei dati.
     * Contiene varie checkbox che permettono all'utente di selezionare singolarmente gli elementi da condividere.
     */
    private void showCheckboxDialogForSharePrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyleShare);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_health_history, null);
        CheckBox checkBoxId = view.findViewById(R.id.dialogShareDetailHistoryId);
        CheckBox checkBoxDate = view.findViewById(R.id.dialogShareDetailHistoryDate);
        CheckBox checkBoxTime = view.findViewById(R.id.dialogShareDetailHistoryTime);
        CheckBox checkBoxValue = view.findViewById(R.id.dialogShareDetailHistoryValue);
        CheckBox checkBoxEvalutation = view.findViewById(R.id.dialogShareDetailHistoryEvalutation);
        CheckBox checkBoxDoctorNotes = view.findViewById(R.id.dialogShareDetailHistoryDoctorNotes);
        CheckBox checkBoxSelectAll = view.findViewById(R.id.dialogShareDetailHistorySelectAll);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(checkBoxId);
        checkBoxes.add(checkBoxDate);
        checkBoxes.add(checkBoxTime);
        checkBoxes.add(checkBoxValue);
        checkBoxes.add(checkBoxEvalutation);
        checkBoxes.add(checkBoxDoctorNotes);
        checkBoxes.add(checkBoxSelectAll);
        Utility.colorAllCheckbox(checkBoxes, getActivity());

        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                 if(checkBoxSelectAll.isChecked())
                 {
                     checkBoxId.setChecked(true);
                     checkBoxDate.setChecked(true);
                     checkBoxTime.setChecked(true);
                     checkBoxValue.setChecked(true);
                     checkBoxEvalutation.setChecked(true);
                     checkBoxDoctorNotes.setChecked(true);
                 }
                 if(!checkBoxSelectAll.isChecked())
                 {
                     checkBoxId.setChecked(false);
                     checkBoxDate.setChecked(false);
                     checkBoxTime.setChecked(false);
                     checkBoxValue.setChecked(false);
                     checkBoxEvalutation.setChecked(false);
                     checkBoxDoctorNotes.setChecked(false);
                 }
             }
         }
        );


        builder.setView(view)
                .setTitle(getString(R.string.titleShareDialogPrivacy))
                .setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String share = new String();
                        if(checkBoxId.isChecked())
                            share = share.concat(getString(R.string.idLastRecordHealthHistoryLabel)).concat(idLastRecordHealthHistory.getText().toString().concat("\n"));
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        if(checkBoxDate.isChecked())
                            share = share.concat(getString(R.string.dateLastRecordHealthHistoryLabel).concat(dateLastRecordHealthHistory.getText().toString()).concat("\n"));
                        if(checkBoxTime.isChecked())
                            share = share.concat(getString(R.string.timeLastRecordHealthHistoryLabel).concat(timeLastRecordHealthHistory.getText().toString()).concat("\n"));
                        if(checkBoxEvalutation.isChecked())
                            share = share.concat(getString(R.string.evalutationLastRecordHealthHistoryLabel).concat(evalutationLastRecordHealthHistory.getText().toString()).concat("\n"));
                        if(checkBoxDoctorNotes.isChecked())
                            share = share.concat(getString(R.string.doctorNotesLastRecordHealthHistoryLabel).concat("\n").concat(doctorNotesLastRecordHealthHistory.getText().toString()).concat("\n"));
                        intent.setType("text/plain");
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, share);
                        startActivity(intent);


                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle negative button click
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Dato un TipoMisurazioneEnunm restituisce l'unità di misura ad esso associato.
     * @param tipoMisurazioneEnum, TipoMisurazioneEnum da cui ricavare l'unità di misura.
     * @return unità di misura.
     */
    private String getUnity(TipoMisurazioneEnum tipoMisurazioneEnum)
    {
        String unity = new String();
        if(tipoMisurazioneEnum.equals(TipoMisurazioneEnum.TEMPERATURA))
            unity=getString(R.string.unityTemperature);
        if(tipoMisurazioneEnum.equals(TipoMisurazioneEnum.PRESSIONESANGUIGNA))
            unity=getString(R.string.unityBloodPressure);
        if(tipoMisurazioneEnum.equals(TipoMisurazioneEnum.PESO))
            unity=getString(R.string.unityWeight);
        if(tipoMisurazioneEnum.equals(TipoMisurazioneEnum.BATTITOCARDIACO))
            unity=getString(R.string.unityBPM);
        if(tipoMisurazioneEnum.equals(TipoMisurazioneEnum.TREMOLIO))
            unity=getString(R.string.unityTrembling);
        if(tipoMisurazioneEnum.equals(TipoMisurazioneEnum.GLUCOSIO))
            unity=getString(R.string.unityGlucose);
        return unity;
    }

    /**
     * Prende dal database tutti i dati relativi all'ultima misurazione e inizializza di conseguenza le view apposite.
     */
    private void getData() {
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        detailHealthHistoryLayout.setAlpha((float) 0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getMisuration(id, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                if(share)
                {
                    showCheckboxDialogForSharePrivacy();
                }
                homeActivityProgressBar.setVisibility(View.INVISIBLE);
                detailHealthHistoryLayout.setAlpha((float) 1);
                if(!result.get("esito").equals(getActivity().getString(R.string.misurationGetSuccessfully)))
                {
                    getActivity().onBackPressed();
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_LONG).show();
                }
                Misurazione misurazione = (Misurazione)result.get("misurazione");
                itemClicked = misurazione.getTipo();
                detailHealthHistoryTitle.setText(Utility.convertTipoMisurazioneEnumToString(itemClicked, getActivity()));
                idLastRecordHealthHistory.setText(misurazione.getId().toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                dateLastRecordHealthHistory.setText(dateFormat.format(misurazione.getData().toDate()));
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeLastRecordHealthHistory.setText(timeFormat.format(misurazione.getData().toDate()));
                if(!itemClicked.equals(TipoMisurazioneEnum.PRESSIONESANGUIGNA))
                    valueLastRecordHealthHistory.setText(misurazione.getValore().toString().concat(getUnity(itemClicked)));
                else
                {
                    valueLastRecordHealthHistory.setText(misurazione.getValoreMax().toString().concat("/").concat(misurazione.getValoreMin().toString().concat(" ").concat(getUnity(itemClicked))));
                }
                evalutationLastRecordHealthHistory.setText(misurazione.getValutazione());
                if(misurazione.getNotaMedico().toString().isEmpty())
                {
                    doctorNotesLastRecordHealthHistory.setText(getString(R.string.emptyDoctorNotes));

                }
                else
                {
                    doctorNotesLastRecordHealthHistory.setText(misurazione.getNotaMedico());
                }
            });
        });
    }



    /**
     * Prende dal database tutti i dati relativi alle misurazioni precedenti che sono della stessa categoria rispetto a quella mostrata in dettaglio nel fragment.
     * Per ogni misurazione precedente trovata, crea dinamicamente delle view.
     */
    @SuppressLint("RestrictedApi")
    private void getOldData()
    {
        oldProgressBar.setVisibility(View.VISIBLE);
        mappaViewIdMisurazioneOld = new HashMap<View, Integer>();
        CompletableFuture<Map<String, Object>> future = Dao.getAllPastMisurationByUsername(AccessUser.getUsername(), itemClickedString, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                oldProgressBar.setVisibility(View.GONE);
                if(!result.get("esito").equals(getActivity().getString(R.string.misurationGetSuccessfully)))
                {
                    getActivity().onBackPressed();
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_LONG).show();
                }
                 List<Misurazione> misurazioni = (List<Misurazione>)result.get("misurazioni");
                 Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.titillium_web_bold);
                // Se la dimensione è pari a 1, significa che non ci sono misurazioni precedenti a quella mostrata in dettaglio nel fragment.
                if(misurazioni.size()==1)
                 {
                    TextView emptyOldHealthHistory = new TextView(getActivity());
                    emptyOldHealthHistory.setText(getString(R.string.emptyHealthHistory));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,50,0,0);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    emptyOldHealthHistory.setLayoutParams(params);
                    emptyOldHealthHistory.setTypeface(typeface);
                    layoutOldHealthHistory.addView(emptyOldHealthHistory);
                    return;
                }
                Boolean flagFirst=true;
                 for(Misurazione misurazione: misurazioni)
                 {
                     // Se è la prima misurazione, la salto in quanto è quella mostrata in dettaglio nel fragment.
                    if(flagFirst==true)
                     {
                         flagFirst=false;
                         continue;
                     }

                    // Creo la textView relativa alla data di rilevazione
                     TextView textViewDate = new TextView(getActivity());
                     SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                     String date = dateFormat.format(misurazione.getData().toDate());
                     textViewDate.setText(date);
                     textViewDate.setId(View.generateViewId());
                     LinearLayout.LayoutParams paramsDate = new LinearLayout.LayoutParams(
                             LinearLayout.LayoutParams.WRAP_CONTENT,
                             LinearLayout.LayoutParams.WRAP_CONTENT
                     );
                     textViewDate.setTypeface(typeface);
                     textViewDate.setBackgroundColor(getResources().getColor(R.color.appMainColorDark));
                     paramsDate.topMargin=getResources().getDimensionPixelSize(R.dimen.marginBetweenInputs);
                     textViewDate.setPadding((int) dpToPx(getContext(), 5), (int) dpToPx(getContext(), 3), (int) dpToPx(getContext(), 5), (int) dpToPx(getContext(), 3));
                     textViewDate.setTextColor(getResources().getColor(R.color.white));
                     textViewDate.setLayoutParams(paramsDate);
                     textViewDate.setVisibility(View.VISIBLE);
                     layoutOldHealthHistory.addView(textViewDate);

                     // Creo la linea che separa la data dalla card
                     View viewline = new View(getActivity());
                     viewline.setId(View.generateViewId());
                     LinearLayout.LayoutParams paramsViewLine = new LinearLayout.LayoutParams(
                             LinearLayout.LayoutParams.WRAP_CONTENT,
                             (int) dpToPx(getContext(), 2)
                     );
                     paramsViewLine.topMargin=0;
                     viewline.setBackgroundColor(getResources().getColor(R.color.appMainColorDark));
                     viewline.setLayoutParams(paramsViewLine);
                     viewline.setVisibility(View.VISIBLE);
                     layoutOldHealthHistory.addView(viewline);

                    // Creo la card
                     ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
                     registerForContextMenu(constraintLayout);
                     mappaViewIdMisurazioneOld.put(constraintLayout, misurazione.getId());
                     constraintLayout.setId(View.generateViewId());
                     ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                             ConstraintLayout.LayoutParams.MATCH_PARENT,
                             getResources().getDimensionPixelSize(R.dimen.heightHealthHistory)
                     );
                     layoutParams.topMargin=0;
                     constraintLayout.setLayoutParams(layoutParams);
                     constraintLayout.setBackgroundColor(getResources().getColor(R.color.colodOldHealthHistory));
                     layoutOldHealthHistory.addView(constraintLayout);
                     constraintLayout.setOnClickListener(new View.OnClickListener()
                     {
                         @Override
                         public void onClick(View v) {
                             openOldHealthHistory(misurazione.getId(), false);
                         }
                     });
                     // Attivo l'animazione al click
                     Utility.activeAnimationOnClick(getActivity(), constraintLayout);

                     // Creo il titolo della misurazione
                     TextView textViewTitle = new TextView(getActivity());
                     textViewTitle.setText(Utility.convertTipoMisurazioneEnumToString(misurazione.getTipo(), getActivity()));
                     textViewTitle.setId(View.generateViewId());
                     ConstraintLayout.LayoutParams paramsTitle = new ConstraintLayout.LayoutParams(
                             ConstraintLayout.LayoutParams.WRAP_CONTENT,
                             ConstraintLayout.LayoutParams.WRAP_CONTENT
                     );
                     textViewTitle.setTypeface(typeface);
                     textViewTitle.setTextColor(getResources().getColor(R.color.white));
                     textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.textTitleHealthHistory));
                     textViewTitle.setLayoutParams(paramsTitle);
                     constraintLayout.addView(textViewTitle);
                     ConstraintSet constraintSet = new ConstraintSet();
                     constraintSet.clone(constraintLayout);
                     constraintSet.connect(textViewTitle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, getResources().getDimensionPixelSize(R.dimen.marginLeftRightDetailHealthHistory));
                     constraintSet.connect(textViewTitle.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) dpToPx(getContext(), 20));
                     constraintSet.applyTo(constraintLayout);


                     // Creo il valore registrato.
                     TextView textViewValue = new TextView(getActivity());
                     textViewValue.setId(View.generateViewId());
                     ConstraintLayout.LayoutParams paramsValue = new ConstraintLayout.LayoutParams(
                             ConstraintLayout.LayoutParams.WRAP_CONTENT,
                             ConstraintLayout.LayoutParams.WRAP_CONTENT
                     );
                     textViewValue.setTypeface(typeface);
                     textViewValue.setTextColor(getResources().getColor(R.color.white));
                     textViewValue.setLayoutParams(paramsValue);
                     constraintLayout.addView(textViewValue);
                     ConstraintSet constraintSetValue = new ConstraintSet();
                     constraintSetValue.clone(constraintLayout);
                     constraintSetValue.connect(textViewValue.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 10));
                     constraintSetValue.connect(textViewValue.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, (int) dpToPx(getContext(), 10));
                     constraintSetValue.applyTo(constraintLayout);

                     if(!misurazione.getTipo().equals(TipoMisurazioneEnum.PRESSIONESANGUIGNA))
                     {
                         textViewValue.setText(String.valueOf((int)(Math.round((misurazione.getValore())))));
                         textViewValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.resultHealthHistory));
                     }
                     else
                     {
                         textViewValue.setText(String.valueOf((int)(Math.round((misurazione.getValoreMax())))).concat("/").concat(String.valueOf((int)(Math.round((misurazione.getValoreMin()))))));
                         textViewValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.resultBloodPressureHealthHistory));
                     }

                     // Creo l'unità di misura
                     TextView textViewUnity = new TextView(getActivity());
                     textViewUnity.setText(getUnity(misurazione.getTipo()));
                     textViewUnity.setId(View.generateViewId());
                     ConstraintLayout.LayoutParams paramsUnity = new ConstraintLayout.LayoutParams(
                             ConstraintLayout.LayoutParams.WRAP_CONTENT,
                             ConstraintLayout.LayoutParams.WRAP_CONTENT
                     );
                     textViewUnity.setTypeface(typeface);
                     textViewUnity.setTextColor(getResources().getColor(R.color.white));
                     textViewUnity.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.resultUnityHealthHistory));
                     textViewUnity.setLayoutParams(paramsUnity);
                     constraintLayout.addView(textViewUnity);
                     ConstraintSet constraintSetUnity = new ConstraintSet();
                     constraintSetUnity.clone(constraintLayout);
                     constraintSetUnity.connect(textViewUnity.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 3));
                     constraintSetUnity.connect(textViewUnity.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, (int) dpToPx(getContext(), 5));
                     constraintSetUnity.applyTo(constraintLayout);



                     // Creo il label di valutazione
                     TextView evalutationLabel = new TextView(getActivity());
                     evalutationLabel.setText(getString(R.string.evalutationHealthHistoryLabel));
                     evalutationLabel.setId(View.generateViewId());
                     ConstraintLayout.LayoutParams paramsEvalutationLabel = new ConstraintLayout.LayoutParams(
                             ConstraintLayout.LayoutParams.WRAP_CONTENT,
                             ConstraintLayout.LayoutParams.WRAP_CONTENT
                     );
                     evalutationLabel.setTypeface(typeface);
                     evalutationLabel.setTextColor(getResources().getColor(R.color.white));
                     evalutationLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) dpToPx(getContext(), 13));
                     evalutationLabel.setLayoutParams(paramsEvalutationLabel);
                     constraintLayout.addView(evalutationLabel);
                     ConstraintSet constraintSetEvalutationLabel = new ConstraintSet();
                     constraintSetEvalutationLabel.clone(constraintLayout);
                     constraintSetEvalutationLabel.connect( evalutationLabel.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 25));
                     constraintSetEvalutationLabel.connect( evalutationLabel.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, getResources().getDimensionPixelSize(R.dimen.marginLeftRightDetailHealthHistory));
                     constraintSetEvalutationLabel.applyTo(constraintLayout);


                     // Creo la valutazione inserita dal medico.
                     TextView evalutation = new TextView(getActivity());
                     evalutation.setText(misurazione.getValutazione());
                     evalutation.setId(View.generateViewId());
                     ConstraintLayout.LayoutParams paramsEvalutation = new ConstraintLayout.LayoutParams(
                             ConstraintLayout.LayoutParams.WRAP_CONTENT,
                             ConstraintLayout.LayoutParams.WRAP_CONTENT
                     );
                     evalutation.setTypeface(typeface);
                     evalutation.setTextColor(getResources().getColor(R.color.white));
                     evalutation.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) dpToPx(getContext(), 13));
                     evalutation.setLayoutParams(paramsEvalutation);
                     constraintLayout.addView(evalutation);
                     ConstraintSet constraintSetEvalutation = new ConstraintSet();
                     constraintSetEvalutation.clone(constraintLayout);
                     constraintSetEvalutation.connect( evalutation.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 25));
                     constraintSetEvalutation.connect( evalutation.getId(), ConstraintSet.LEFT, evalutationLabel.getId(), ConstraintSet.RIGHT, (int) dpToPx(getContext(), 3));
                     constraintSetEvalutation.applyTo(constraintLayout);
                 }
            });
        });

    }


    /**
     * Apre il fragment che consente la visualizzazione in dettaglio di una misurazione precedente.
     * @param id, identificativo della misurazione precedente da aprire.
     */
    private void openOldHealthHistory(Integer id, Boolean share)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putBoolean("share", share);
        if(openDoctor==false)
        {
            bundle.putBoolean("doctor", false);
            fragmentTransaction.addToBackStack(getString(R.string.healthMenuScreen));
            fragmentTransaction.replace(R.id.homeContainerView, DetailOldHealthHistoryFragment.class, bundle);
        }
        if(openDoctor==true)
        {
            bundle.putBoolean("doctor", true);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.doctorFragmentView, DetailOldHealthHistoryFragment.class, bundle);
        }
        fragmentTransaction.commit();
    }


    /**
     * Apre il DialogFragment per la modifica della valutazione [Account dottore]
     */
    private void openDialogEditEvalutation()
    {
        EditEvalutationDialogFragment editEvalutationDialogFragment = EditEvalutationDialogFragment.newInstance(Integer.valueOf(idLastRecordHealthHistory.getText().toString()));
        editEvalutationDialogFragment.show(getActivity().getSupportFragmentManager(), "EditEvalutationDialogFragment");
        editEvalutationDialogFragment.setTargetFragment(this, REQUEST_CODE_EDIT_EVALUTATION);

    }

    /**
     * Apre il DialogFragment per la modifica della nota del medico [Account dottore]
     */
    private void openDialogEditDoctorNotes()
    {
        EditDoctorNotesDialogFragment editDoctorNotesDialogFragment =EditDoctorNotesDialogFragment.newInstanceHealthHistory(Integer.valueOf(idLastRecordHealthHistory.getText().toString()));
        editDoctorNotesDialogFragment.show(getActivity().getSupportFragmentManager(), "EditDoctorNotesDialogFragment");
        editDoctorNotesDialogFragment.setTargetFragment(this, REQUEST_CODE_EDIT_DOCTOR_NOTES);

    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        itemOldClicked = v;
        inflater.inflate(R.menu.share_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        openOldHealthHistory(mappaViewIdMisurazioneOld.get(itemOldClicked), true);
        return super.onContextItemSelected(item);
    }


    /**
     * Comportamento del tasto back quando il fragment viene aperto con l'account dottore.
     */
    private OnBackPressedCallback onBackPressedCallbackDoctor = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putBoolean("backHealthHistory", true);
            fragmentTransaction.replace(R.id.doctorFragmentView, DetailUserDoctorFragment.class, bundle);
            fragmentTransaction.commit();
        }
    };


    /**
     * Si attiva quando viene chiuso il DialogFragment di modifica della valutazione.
     */
    @Override
    public void closeEditEvalutation() {
        getData();
    }

    /**
     * Si attiva quando viene chiuso il DialogFragment di modifica della nota del medico.
     */
    @Override
    public void closeEditDoctorNotes() {
        getData();
    }
}

