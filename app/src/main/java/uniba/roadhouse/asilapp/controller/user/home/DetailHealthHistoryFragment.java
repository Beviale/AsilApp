package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Misurazione;

/**
 * Fragment che permette la visualizzazione in dettaglio dell'ultima misurazione di un determinato parametro,
 */
public class DetailHealthHistoryFragment extends Fragment {
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
     * is dell'ultima misurazione
     */
    private static int id;
    /**
     * Se il fragment è stato aperto con l'intenzione di condividere i dati,
     */
    private static Boolean share;





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




    public DetailHealthHistoryFragment() {
    }


    // TODO: Rename and change types and number of parameters
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
        homeActivityProgressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        valueLastRecordHealthHistory = view.findViewById(R.id.valueLastRecordHealthHistory);
        idLastRecordHealthHistory = view.findViewById(R.id.idLastRecordHealthHistory);
        dateLastRecordHealthHistory = view.findViewById(R.id.dateLastRecordHealthHistory);
        oldProgressBar = view.findViewById(R.id.oldProgressBar);
        timeLastRecordHealthHistory = view.findViewById(R.id.timeLastRecordHealthHistory);
        evalutationLastRecordHealthHistory = view.findViewById(R.id.evalutationLastRecordHealthHistory);
        getData();
        getOldData();
    }

    @Override
    public void onStart() {
        super.onStart();
        shareDetailHealthHistory.setOnClickListener(v->showCheckboxDialogForSharePrivacy());
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
     * Apre il dialog dei condivisione dei dati.
     * Contiene varie checkbox che permettono all'utente di selezionare singolarmente gli elementi da condividere.
     */
    private void showCheckboxDialogForSharePrivacy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.checkbox_dialog_health_history, null);
        CheckBox checkBoxId = view.findViewById(R.id.dialogShareDetailHistoryId);
        CheckBox checkBoxDate = view.findViewById(R.id.dialogShareDetailHistoryDate);
        CheckBox checkBoxTime = view.findViewById(R.id.dialogShareDetailHistoryTime);
        CheckBox checkBoxValue = view.findViewById(R.id.dialogShareDetailHistoryValue);
        CheckBox checkBoxEvalutation = view.findViewById(R.id.dialogShareDetailHistoryEvalutation);
        CheckBox checkBoxDoctorNotes = view.findViewById(R.id.dialogShareDetailHistoryDoctorNotes);
        List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(checkBoxId);
        checkBoxes.add(checkBoxDate);
        checkBoxes.add(checkBoxTime);
        checkBoxes.add(checkBoxValue);
        checkBoxes.add(checkBoxEvalutation);
        checkBoxes.add(checkBoxDoctorNotes);
        Utility.colorAllCheckbox(checkBoxes, getActivity());
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
     * @param itemClicked, TipoMisurazioneEnum da cui ricavare l'unità di misura.
     * @return unità di misura.
     */
    private String getUnity(TipoMisurazioneEnum itemClicked)
    {
        String unity = new String();
        if(itemClicked.equals(TipoMisurazioneEnum.TEMPERATURA))
            unity=getString(R.string.unityTemperature);
        if(itemClicked.equals(TipoMisurazioneEnum.PRESSIONESANGUIGNA))
            unity=getString(R.string.unityBloodPressure);
        if(itemClicked.equals(TipoMisurazioneEnum.PESO))
            unity=getString(R.string.unityWeight);
        if(itemClicked.equals(TipoMisurazioneEnum.BATTITOCARDIACO))
            unity=getString(R.string.unityBPM);
        if(itemClicked.equals(TipoMisurazioneEnum.TREMOLIO))
            unity=getString(R.string.unityTrembling);
        if(itemClicked.equals(TipoMisurazioneEnum.GLUCOSIO))
            unity=getString(R.string.unityGlucose);
        return unity;
    }

    /**
     * Prende dal database tutti i dati relativi all'ultima misurazione e inizializza di conseguenza i widget appositi.
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
                doctorNotesLastRecordHealthHistory.setText(misurazione.getNotaMedico());
            });
        });
    }


    /**
     * Prende dal database tutti i dati reltivi alle misurazioni precedenti che sono dello stesso tipo rispetto a quella mostrata in dettaglio nel fragment.
     * Per ogni misurazioni precedente trovata, crea dinamicamente delle view.
     */
    @SuppressLint("RestrictedApi")
    private void getOldData()
    {
        oldProgressBar.setVisibility(View.VISIBLE);
        CompletableFuture<Map<String, Object>> future = Dao.getAllPastMisurationByUsername(Access.getUsername(), itemClickedString, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                oldProgressBar.setVisibility(View.GONE);
                if(!result.get("esito").equals(getActivity().getString(R.string.misurationGetSuccessfully)))
                {
                    getActivity().onBackPressed();
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_LONG).show();
                }
                 List<Misurazione> misurazioni = (List<Misurazione>)result.get("misurazioni");
                 Boolean flagFirst=true;
                 for(Misurazione misurazione: misurazioni)
                 {
                     // Se è l'ultima misurazione, la salto.
                    if(flagFirst==true)
                     {
                         flagFirst=false;
                         continue;
                     }

                     TextView textViewDate = new TextView(getActivity());
                     SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                     String date = dateFormat.format(misurazione.getData().toDate());
                     textViewDate.setText(date);
                     textViewDate.setId(View.generateViewId());
                     LinearLayout.LayoutParams paramsDate = new LinearLayout.LayoutParams(
                             LinearLayout.LayoutParams.WRAP_CONTENT,
                             LinearLayout.LayoutParams.WRAP_CONTENT
                     );
                     Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.titillium_web_bold);
                     textViewDate.setTypeface(typeface);
                     textViewDate.setBackgroundColor(getResources().getColor(R.color.appMainColorDark));
                     paramsDate.topMargin=getResources().getDimensionPixelSize(R.dimen.marginBetweenInputs);
                     textViewDate.setPadding((int) dpToPx(getContext(), 5), (int) dpToPx(getContext(), 3), (int) dpToPx(getContext(), 5), (int) dpToPx(getContext(), 3));
                     textViewDate.setTextColor(getResources().getColor(R.color.white));
                     textViewDate.setLayoutParams(paramsDate);
                     textViewDate.setVisibility(View.VISIBLE);
                     layoutOldHealthHistory.addView(textViewDate);

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


                     ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
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
                             openOldHealthHistory(misurazione.getId());
                         }
                     });



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
    private void openOldHealthHistory(Integer id)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        fragmentTransaction.addToBackStack(getString(R.string.healthMenuScreen));
        fragmentTransaction.replace(R.id.homeContainerView, DetailOldHealthHistoryFragment.class, bundle);
        fragmentTransaction.commit();
    }



}

