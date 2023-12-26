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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * DialogFragment che consente al dottore la modifica delle note relative o ad una misurazione oppure ad una patologia.
 */
public class EditDoctorNotesDialogFragment extends DialogFragment {
    /**
     * Campo di testo per l'inserimento delle note.
     */
    EditText editDoctorNotesEditText;
    /**
     * Button che consente il salvataggio delle nuove note.
     */
    Button editDoctorNotesButtonSend;
    /**
     * Button che consente all'utente di annullare l'intera operazione.
     */
    Button editDoctorNotesButtonCancel;
    /**
     * Layout relativo a EditDoctorNotesDialogFragment.
     */
    LinearLayout layoutEditDoctorNotes;
    /**
     * Listener che avvisa il fragment che ha aperto EditDoctorNotesDialogFragment che quest'ultimo è stato chiuso.
     */
    private closeEditDoctorNotes callbackClose;
    /**
     * PorgressBar da mostrare mentre le nuove note vengono salvate nel database.
     */
    ProgressBar progressBar;
    /**
     * Identifica se si vogliono modificare le note di una patologia o di una misurazione.
     */
    private static String typeEdit;
    /**
     * Username dell'utente.
     */
    private static String username;
    /**
     * Nome della patologia dove si richiede la modifica delle note.
     */
    private static String namePathology;
    /**
     * Id della misurazione dove si richiede la modifica delle note.
     */
    private static Integer idMisuration;


    /**
     * Interfaccia che deve essere implementata dal fragment che ha aperto EditDoctorNotesDialogFragment.
     * Viene utilizzato come listener per avvisare il fragment che l'EditDoctorNotesDialogFragment aperto precedenemente, è stato appena chiuso.
     */
    public interface closeEditDoctorNotes {
        public void closeEditDoctorNotes();
    }


    /**
     * Crea una nuova istanza di EditDoctorNotesDialgFragment.
     * Da utilizzare quando si devono modificare le note di una patologia.
     * @param usernameAdd, username dell'utente.
     * @param namePathologyAdd, nome della patologia
     * @return
     */
    public static EditDoctorNotesDialogFragment newInstancePathology(String usernameAdd, String namePathologyAdd) {
        username =usernameAdd;
        namePathology = namePathologyAdd;
        typeEdit="pathology";
        return new EditDoctorNotesDialogFragment();
    }

    /**
     * Crea una nuova istanza di EditDoctorNotesDialgFragment.
     * Da utilizzare quando si devono modificare le note di una misurazione.
     * @param idMisurationAdd, id della misurazione.
     * @return
     */
    public static EditDoctorNotesDialogFragment newInstanceHealthHistory(Integer idMisurationAdd) {
        idMisuration = idMisurationAdd;
        typeEdit="healthHistory";
        return new EditDoctorNotesDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_doctor_notes_fragment, container, false);
        try {
            callbackClose = (closeEditDoctorNotes) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement the closeEditDoctorNotes");
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        editDoctorNotesEditText = view.findViewById(R.id.editDoctorNotesEditText);
        progressBar = view.findViewById(R.id.progressBarEditDoctorNotes);
        layoutEditDoctorNotes = view.findViewById(R.id.layoutEditDoctorNotes);
        editDoctorNotesButtonSend = view.findViewById(R.id.editDoctorNotesButtonSend);
        editDoctorNotesButtonCancel = view.findViewById(R.id.editDoctorNotesButtonCancel);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        //----------------LISTENER------------------
        editDoctorNotesButtonSend.setOnClickListener(v->saveData());
        editDoctorNotesButtonCancel.setOnClickListener(v->closeDialog());
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
     * Memorizza nel database le nuove note.
     */
    private void saveData()
    {
        if(editDoctorNotesEditText.getText().toString().isEmpty())
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.emptyEditDoctorNotesTitle), getString(R.string.emptyEditDoctorNotes));
            return;
        }
        String newDoctorNotes = editDoctorNotesEditText.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        layoutEditDoctorNotes.setAlpha((float)0.5);
        if(typeEdit=="pathology")
        {
            CompletableFuture<String> future = Dao.editPatologiaNotaMedico(username, namePathology, newDoctorNotes, getActivity());
            future.thenAccept(result -> {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    layoutEditDoctorNotes.setAlpha((float)1.0);
                    closeDialog();
                    callbackClose.closeEditDoctorNotes();
                });
            });

        }
        if(typeEdit=="healthHistory")
        {
            CompletableFuture<String> future = Dao.editMisurationNota(idMisuration, newDoctorNotes, getActivity());
            future.thenAccept(result -> {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    layoutEditDoctorNotes.setAlpha((float)1.0);
                    closeDialog();
                    callbackClose.closeEditDoctorNotes();
                });
            });

        }

    }


    /**
     * Chiude la finestra di dialogo.
     */
    private void closeDialog()
    {
        getDialog().dismiss();
    }

}
