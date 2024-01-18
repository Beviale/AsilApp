package uniba.roadhouse.asilapp.controller.doctor;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * DialogFragment che consente al dottore la modifica della valutazione di una misurazione.
 */
public class EditEvalutationDialogFragment extends DialogFragment {
    /**
     * RadioGruoup contenente le varie opzioni selezionabili per la valutazione.
     */
    RadioGroup evalutationHealthHistoryRadioGroup;
    /**
     * Button che consente l'invio della valutazione.
     */
    Button evalutationHealthHistoryButtonSend;
    /**
     * Button che annulla l'intera operazione.
     */
    Button evalutationHealthHistoryButtonCancel;
    /**
     * RadioButton che rappresenta una buona valutazione.
     */
    RadioButton healthHistoryGood;
    /**
     * RadioButton che rappresenta una valutazione discreta.
     */
    RadioButton healthHistoryFairlyGood;
    /**
     * RadioButton che rappresenta una pessima valutazione.
     */
    RadioButton healthHistoryFairlyNotGood;
    /**
     * Layout relativo all'intero DialogFragment.
     */
    LinearLayout layoutEditEvalutation;
    /**
     * ProgressBar da mostreare durante l'invio e il salvataggio dei dati nel database.
     */
    ProgressBar progressBar;
    /**
     * Id della misurazione su cui effettuare la modifica della valutazione.
     */
    private static Integer id;
    /**
     * Listener che avvisa il fragment che ha aperto EditEvalutationDialogFragment che quest'ultimo è stato chiuso.
     */
    private closeListenerEditEvalutation callbackClose;


    /**
     * Interfaccia che deve essere implementata dal fragment che ha aperto EditEvalutationDialogFragment.
     * Viene utilizzato come listener per avvisare il fragment che l'EdiEvalutationDialogFragment aperto precedenemente, è stato appena chiuso.
     */
    public interface closeListenerEditEvalutation {
        public void closeEditEvalutation();
    }

    /**
     * Crea una nuova istanza di EdiEvalutationDialogFragment.
     * @param idAdd, id della misurazione.
     * @return
     */
    public static EditEvalutationDialogFragment newInstance(Integer idAdd) {
        id = idAdd;
        return new EditEvalutationDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_evalutation_fragment, container, false);
        try {
            callbackClose = (EditEvalutationDialogFragment.closeListenerEditEvalutation) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        evalutationHealthHistoryRadioGroup = view.findViewById(R.id.evalutationHealthHistoryRadioGroup);
        evalutationHealthHistoryButtonSend = view.findViewById(R.id.evalutationHealthHistoryButtonSend);
        evalutationHealthHistoryButtonCancel = view.findViewById(R.id.evalutationHealthHistoryButtonCancel);
        layoutEditEvalutation = view.findViewById(R.id.layoutEditEvalutation);
        progressBar = view.findViewById(R.id.progressBarEditEvalutation);
        healthHistoryGood = view.findViewById(R.id.healthHistoryGood);
        healthHistoryFairlyGood = view.findViewById(R.id.healthHistoryFairlyGood);
        healthHistoryFairlyNotGood = view.findViewById(R.id.healthHistoryNotGood);
        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Rendo il DialogFragment non cancellabile quando si preme lo schermo del device.
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart() {
        //-----------LISTENER----------------
        evalutationHealthHistoryButtonSend.setOnClickListener(v->saveData());
        evalutationHealthHistoryButtonCancel.setOnClickListener(v->closeDialog());
        super.onStart();
    }

    /**
     * Chiude la finestra di dialogo.
     */
    private void closeDialog()
    {
        getDialog().dismiss();
    }


    /**
     * Invia e memorizza nel database la nuova valutazione.
     */
    private void saveData()
    {
        if(evalutationHealthHistoryRadioGroup.getCheckedRadioButtonId()==-1)
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.emptyEditEvalutationTitle), getString(R.string.emptyEditEvalutation));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutEditEvalutation.setAlpha((float)0.5);
        String newEvalutation = "-";
        if(healthHistoryGood.isChecked())
        {
            newEvalutation = Utility.convertStringToEvalutationEnum(getActivity(), getString(R.string.evalutationHealthHistoryValueGood)).toString();
        }
        else if(healthHistoryFairlyGood.isChecked())
        {
            newEvalutation = Utility.convertStringToEvalutationEnum(getActivity(), getString(R.string.evalutationHealthHistoryValueFairlyGood)).toString();
        }
        else
        {
            newEvalutation = Utility.convertStringToEvalutationEnum(getActivity(), getString(R.string.evalutationHealthHistoryValueNotGood)).toString();
        }
        CompletableFuture<String> future = Dao.editMisurationValutazione(id, newEvalutation, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutEditEvalutation.setAlpha((float)1.0);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                closeDialog();
                callbackClose.closeEditEvalutation();
            });
        });
    }
}
