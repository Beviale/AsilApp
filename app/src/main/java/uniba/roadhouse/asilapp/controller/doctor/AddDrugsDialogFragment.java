package uniba.roadhouse.asilapp.controller.doctor;

import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.user.home.DetailMyPathologiesFragment;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Farmaco;

/**
 * DialogFragment che consente l'aggiunta di un nuovo famrmaco ad una patologia.
 */
public class AddDrugsDialogFragment extends DialogFragment {
    /**
     * Layout del campo di testo per l'inserimento del nome del farmaco.
     */
    TextInputLayout nameAddDrugLayoutLayout;
    /**
     * Campo di testo per l'inserimento del normale del farmaco
     */
    TextInputEditText nameAddDrugLayoutInput;
    /**
     * Layout del campo di testo per l'inserimento della nota associata al farmaco.
     */
    TextInputLayout noteAddDrugLayoutLayout;
    /**
     * Campo di testo per l'inserimento della nota associata al farmaco.
     */
    TextInputEditText noteAddDrugLayoutInput;
    /**
     * Button che che annulla l'inserimento del farmaco.
     */
    Button addDrugButtonCancel;
    /**
     * Button che invia al database il nuovo farmaco aggiunto.
     */
    Button addDrugButtonSend;
    /**
     * ProgressBar da mostrare quando il farmasto sta per essere salvato nel database.
     */
    ProgressBar progressBar;
    /**
     * Layout di DoctorActivity.
     */
    LinearLayout linearLayoutDoctoActivity;
    /**
     * Nome della patologia da associare al farmaco. Viene passata dal fragment che ha aperto il dialogFragment,.
     */
    private static String namePathology;
    /**
     * Listener che avvisa il fragment che ha aperto AddDrugsDialogFragment che quest'ultimo è stato chiuso.
     */
    private closeListenerAddDrugs callbackClose;


    /**
     * Interfaccia che deve essere implementata dal fragment che ha aperto AddDrugsDialogFragment.
     * Viene utilizzato come listener per avvisare il fragment che l'AddDrugsDialogFragment aperto precedenemente, è stato appena chiuso.
     */
    public interface closeListenerAddDrugs {
        public void closeAddDrugs();
    }


    /**
     * Crea un'istanza di AddDrugsDialogFragment.
     * @param namePathologyInput, nome della patologia da associare al farmaco
     */
    public static AddDrugsDialogFragment  newInstance(String namePathologyInput) {
        namePathology = namePathologyInput;
        return new AddDrugsDialogFragment ();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_drugs_fragment, container, false);
        try {
            callbackClose = (closeListenerAddDrugs) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement the closeListenerAddDrugs");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        nameAddDrugLayoutLayout = view.findViewById(R.id.nameAddDrugLayout);
        linearLayoutDoctoActivity = getActivity().findViewById(R.id.linearLayoutDoctoActivity);
        progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        nameAddDrugLayoutInput = view.findViewById(R.id.nameAddDrugInput);
        noteAddDrugLayoutLayout = view.findViewById(R.id.noteAddDrugLayout);
        noteAddDrugLayoutInput = view.findViewById(R.id.noteAddDrugInput);
        addDrugButtonCancel = view.findViewById(R.id.addDrugButtonCancel);
        addDrugButtonSend = view.findViewById(R.id.addDrugButtonSend);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        //----------LISTENER-------------
        addDrugButtonSend.setOnClickListener(v->saveData());
        addDrugButtonCancel.setOnClickListener(v->closeDialog());
        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Rendo il DialogFragment non cancellabile quando si preme lo schermo del device.
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }


    /**
     * Memorizza il farmaco nel database prendendo i dati inseriti nei campi di testo.
     */
    private void saveData()
    {
        if(nameAddDrugLayoutInput.getText().toString().isEmpty())
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.emptyNameAddDrugTitle), getString(R.string.emptyNameAddDrug));
            return;
        }
        String name = nameAddDrugLayoutInput.getText().toString();
        String note="";
        if(noteAddDrugLayoutInput.getText().toString().isEmpty())
        {
            note = getString(R.string.emptyNoteDrug);
        }
        else
        {
            note = noteAddDrugLayoutInput.getText().toString();
        }
        Farmaco addFarmaco = new Farmaco(name, note, AccessUser.getUsername(), namePathology);
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutDoctoActivity.setAlpha((float)0.5);
        CompletableFuture<String> future = Dao.addFarmaco(addFarmaco, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                linearLayoutDoctoActivity.setAlpha((float)1.0);
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                closeDialog();
                callbackClose.closeAddDrugs();
            });
        });
    }


    /**
     * Chiude la finestra di dialogo.
     */
    private void closeDialog()
    {
        getDialog().dismiss();
    }

}
