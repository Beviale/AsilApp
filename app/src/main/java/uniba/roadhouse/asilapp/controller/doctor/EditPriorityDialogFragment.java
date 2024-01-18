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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.checkerframework.checker.guieffect.qual.UIType;

import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * DialogFragment che consente al dottore la modifica della priorità di una patologia.
 */
public class EditPriorityDialogFragment extends DialogFragment {
    /**
     * RadioGruoup contenente le varie opzioni selezionabili per la priorità.
     */
    RadioGroup priorityMyPathologiesRadioGroup;
    /**
     * Button che consente l'invio dei dati al database.
     */
    Button priorityMyPathologiesButtonSend;
    /**
     * Button che consente all'utente di annullare l'intera operazione.
     */
    Button priorityMyPathologiesButtonCancel;
    /**
     * RadioButton che rappresenta una priorità bassa.
     */
    RadioButton myPathologiesLow;
    /**
     * RadioButton che rappresenta una priorità media.
     */
    RadioButton myPathologiesMedium;
    /**
     * RadioButton che rappresenta una priorità elevata.
     */
    RadioButton myPathologiesHigh;
    /**
     * Layout relativo all'intero DialogFragment.
     */
    LinearLayout layoutEditPriority;
    /**
     * ProgressBar da mostrare durante l'invio dei dati al database.
     */
    ProgressBar progressBar;
    /**
     * Listener che avvisa il fragment che ha aperto EditPrioritDialogFragment che quest'ultimo è stato chiuso.
     */
    private closeListenerEditPriority callbackClose;
    /**
     * Username dell'utente.
     */
    private static String username;
    /**
     * Nome della patologia a cui modificare la priorità.
     */
    private static String namePathology;

    public interface closeListenerEditPriority {
        public void closeEditPriority();
    }


    /**
     * Crea una nuova itanza di EditPriorityDialogFragment
     * @param usernameAdd, username dell'utente.
     * @param namePathologyAdd, nome della patologia a cui modificare la priorità.
     * @return
     */
    public static EditPriorityDialogFragment newInstance(String usernameAdd, String namePathologyAdd) {
        username = usernameAdd;
        namePathology = namePathologyAdd;
        return new EditPriorityDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_priority_fragment, container, false);
        try {
            callbackClose = (closeListenerEditPriority) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        priorityMyPathologiesRadioGroup = view.findViewById(R.id.priorityMyPathologiesRadioGroup);
        priorityMyPathologiesButtonSend = view.findViewById(R.id.priorityMyPathologiesButtonSend);
        priorityMyPathologiesButtonCancel = view.findViewById(R.id.priorityMyPathologiesButtonCancel);
        myPathologiesLow = view.findViewById(R.id.myPathologiesLow);
        layoutEditPriority = view.findViewById(R.id.layoutEditPriority);
        progressBar = view.findViewById(R.id.progressBarEditPriority);
        myPathologiesMedium = view.findViewById(R.id.myPathologiesMedium);
        myPathologiesHigh = view.findViewById(R.id.myPathologiesHigh);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        //-----------LISTENER-------------------
        priorityMyPathologiesButtonSend.setOnClickListener(v->saveData());
        priorityMyPathologiesButtonCancel.setOnClickListener(v->closeDialog());
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
     * Chiude la finestra di dialogo.
     */
    private void closeDialog()
    {
        getDialog().dismiss();
    }


    /**
     * Invia e memorizza nel database la nuova priorità.
     */
    private void saveData()
    {
        if(priorityMyPathologiesRadioGroup.getCheckedRadioButtonId()==-1)
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.emptyEditPriorityTitle), getString(R.string.emptyEditPriority));
            return;
        }
        String newPriority = "-";
        if(myPathologiesLow.isChecked())
        {
            newPriority = Utility.convertStringToPriorityEnum(getActivity(), getString(R.string.priorityMyPathologiesValueLow)).toString();
        }
        else if (myPathologiesMedium.isChecked())
        {
            newPriority = Utility.convertStringToPriorityEnum(getActivity(), getString(R.string.priorityMyPathologiesValueMedium)).toString();
        }
        else if (myPathologiesHigh.isChecked())
        {
            newPriority = Utility.convertStringToPriorityEnum(getActivity(), getString(R.string.priorityMyPathologiesValueHigh)).toString();
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutEditPriority.setAlpha((float)0.5);
        CompletableFuture<String> future = Dao.editPatologiaPriority(username, namePathology, newPriority, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                layoutEditPriority.setAlpha((float)1.0);
                closeDialog();
                callbackClose.closeEditPriority();
            });
        });
    }
}

