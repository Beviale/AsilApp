package uniba.roadhouse.asilapp.controller.user.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import uniba.roadhouse.asilapp.controller.doctor.EditDoctorNotesDialogFragment;
import uniba.roadhouse.asilapp.controller.doctor.EditEvalutationDialogFragment;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Misurazione;

/**
 * Fragment che consente la visualizzazione in dettaglio di una misurazione precedente.
 */
public class DetailOldHealthHistoryFragment extends Fragment {
    // Da inizializzare con in valori presenti nel database
    /**
     * id della misurazione,
     */
    TextView idRecordHealthHistoryOld;
    /**
     * data della misurazione
     */
    TextView dateRecordHealthHistoryOld;
    /**
     * ora della misurazione
     */
    TextView timeRecordHealthHistoryOld;
    /**
     * valore registrato con la misurazione
     */
    TextView valueLastRecordHealthHistoryOld;
    /**
     * valutazione della misurazione
     */
    TextView evalutationRecordHealthHistoryOld;
    /**
     * note del medico relative alla misurazione
     */
    TextView doctorNotesRecordHealthHistoryOld;
    /**
     * titolo del fragment. Si tratta essenzialmente del tipo di misurazione.
     */
    TextView detailHealthHistoryTitleOld;


    /**
     * Icona che consente la condivisione dei dati,
     */
    ImageView shareDetailHealthHistoryOld;
    /**
     * ProgressBar da mostrare durante il caricamento dei dati dal database.
     */

    ProgressBar homeActivityProgressBar;
    /**
     * layout da oscurare durante il caricamento dei dati dal database.
     */
    ConstraintLayout layoutOldHealthHistory;

    /**
     * identificativo della misurazione da mostrare.
     */
    private static Integer id;

    /**
     * Consente la modifica della valutazione da parte del medico
     */
   ImageView editButtonHealthHistoryOldEvalutation;
    /**
     * Consente la modifica della nota da parte del medico.
     */
   ImageView editButtonHealthHistoryOldDoctorNotes;

    /**
     * Indica se il fragment è stato aperto in modalità condivisione.
     */
   private static Boolean share;

    /**
     * Indica se il fragment è stato aperto con un account dottore.
     */
   private static Boolean openDoctor=false;


    public DetailOldHealthHistoryFragment() {
    }


    public static DetailOldHealthHistoryFragment newInstance(String param1, String param2) {
        DetailOldHealthHistoryFragment fragment = new DetailOldHealthHistoryFragment();
        return fragment;
    }


    /**
     * Prende dal fragmente precedente l'identificativo della misurazione da mostrare.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
            share = getArguments().getBoolean("share");
            openDoctor = getArguments().getBoolean("doctor");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detai_old_health_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //--------RIFERIMENTI----------------
        idRecordHealthHistoryOld = view.findViewById(R.id.idRecordHealthHistoryOld);
        detailHealthHistoryTitleOld = view.findViewById(R.id.detailHealthHistoryTitleOld);
        dateRecordHealthHistoryOld = view.findViewById(R.id.dateRecordHealthHistoryOld);
        timeRecordHealthHistoryOld = view.findViewById(R.id.timeRecordHealthHistoryOld);
        valueLastRecordHealthHistoryOld = view.findViewById(R.id.valueLastRecordHealthHistoryOld);
        evalutationRecordHealthHistoryOld = view.findViewById(R.id.evalutationRecordHealthHistoryOld);
        doctorNotesRecordHealthHistoryOld = view.findViewById(R.id.doctorNotesRecordHealthHistoryOld);
        Utility.enableScroll(doctorNotesRecordHealthHistoryOld);
        layoutOldHealthHistory = view.findViewById(R.id.layoutOldHealthHistory);
        if(openDoctor==false)
        {
            homeActivityProgressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        }
        if(openDoctor==true)
        {
            homeActivityProgressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        }
        shareDetailHealthHistoryOld = view.findViewById(R.id.shareDetailHealthHistoryOld);
        editButtonHealthHistoryOldEvalutation = view.findViewById(R.id.editButtonHealthHistoryOldEvalutation);
        editButtonHealthHistoryOldDoctorNotes = view.findViewById(R.id.editButtonHealthHistoryOldDoctorNotes);
        if(share==true)
        {
            showCheckboxDialogForSharePrivacy();
        }
        if(openDoctor==true)
        {
            editButtonHealthHistoryOldEvalutation.setVisibility(View.VISIBLE);
            editButtonHealthHistoryOldDoctorNotes.setVisibility(View.VISIBLE);
        }
        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
        //-----------LISTENER------------------
        shareDetailHealthHistoryOld.setOnClickListener(v->showCheckboxDialogForSharePrivacy());
        editButtonHealthHistoryOldEvalutation.setOnClickListener(v->openDialogEditEvalutation());
        editButtonHealthHistoryOldDoctorNotes.setOnClickListener(v->openDialogEditDoctorNotes());
    }

    @Override
    public void onPause() {
        homeActivityProgressBar.setVisibility(View.GONE);
        super.onPause();
    }

    @Override
    public void onResume() {
        if(openDoctor==false)
        {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        }
        if(openDoctor==true)
        {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbarDoctorActivity);
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


    /**
     * Apre il dialog dei condivisione dei dati.
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
                            share = share.concat(getString(R.string.idLastRecordHealthHistoryLabel)).concat(idRecordHealthHistoryOld.getText().toString().concat("\n"));
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        if(checkBoxDate.isChecked())
                            share = share.concat(getString(R.string.dateLastRecordHealthHistoryLabel).concat(dateRecordHealthHistoryOld.getText().toString()).concat("\n"));
                        if(checkBoxTime.isChecked())
                            share = share.concat(getString(R.string.timeLastRecordHealthHistoryLabel).concat(timeRecordHealthHistoryOld.getText().toString()).concat("\n"));
                        if(checkBoxEvalutation.isChecked())
                            share = share.concat(getString(R.string.evalutationLastRecordHealthHistoryLabel).concat(evalutationRecordHealthHistoryOld.getText().toString()).concat("\n"));
                        if(checkBoxDoctorNotes.isChecked())
                            share = share.concat(getString(R.string.doctorNotesLastRecordHealthHistoryLabel).concat("\n").concat(doctorNotesRecordHealthHistoryOld.getText().toString()).concat("\n"));
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
     * Prende dal database tutti i dati relativi alla misurazione.
     */
    private void getData()
    {
        homeActivityProgressBar.setVisibility(View.VISIBLE);
        layoutOldHealthHistory.setAlpha((float) 0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getMisuration(id, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                homeActivityProgressBar.setVisibility(View.INVISIBLE);
                layoutOldHealthHistory.setAlpha((float) 1);
                if(!result.get("esito").equals(getActivity().getString(R.string.misurationGetSuccessfully)))
                {
                    getActivity().onBackPressed();
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_LONG).show();
                }
                Misurazione misurazione = (Misurazione)result.get("misurazione");
                detailHealthHistoryTitleOld.setText(Utility.convertTipoMisurazioneEnumToString(misurazione.getTipo(), getActivity()));
               idRecordHealthHistoryOld.setText(misurazione.getId().toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                dateRecordHealthHistoryOld.setText(dateFormat.format(misurazione.getData().toDate()));
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeRecordHealthHistoryOld.setText(timeFormat.format(misurazione.getData().toDate()));
                if(!misurazione.getTipo().equals(TipoMisurazioneEnum.PRESSIONESANGUIGNA))
                    valueLastRecordHealthHistoryOld.setText(misurazione.getValore().toString().concat(getUnity(misurazione.getTipo())));
                else
                {
                   valueLastRecordHealthHistoryOld.setText(misurazione.getValoreMax().toString().concat("/").concat(misurazione.getValoreMin().toString().concat(" ").concat(getUnity(misurazione.getTipo()))));
                }
                evalutationRecordHealthHistoryOld.setText(misurazione.getValutazione());
                if(misurazione.getNotaMedico().toString().isEmpty())
                {
                    doctorNotesRecordHealthHistoryOld.setText(getString(R.string.emptyDoctorNotes));

                }
                else
                {
                    doctorNotesRecordHealthHistoryOld.setText(misurazione.getNotaMedico());
                }
            });
        });
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

    private void openDialogEditEvalutation()
    {
        EditEvalutationDialogFragment editEvalutationDialogFragment = EditEvalutationDialogFragment.newInstance();
        editEvalutationDialogFragment.show(getActivity().getSupportFragmentManager(), "EditEvalutationDialogFragment");
    }

    private void openDialogEditDoctorNotes()
    {
        EditDoctorNotesDialogFragment editDoctorNotesDialogFragment =EditDoctorNotesDialogFragment.newInstance();
        editDoctorNotesDialogFragment.show(getActivity().getSupportFragmentManager(), "EditDoctorNotesDialogFragment");

    }

}