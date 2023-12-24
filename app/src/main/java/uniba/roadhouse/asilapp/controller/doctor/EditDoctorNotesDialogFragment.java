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
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;

public class EditDoctorNotesDialogFragment extends DialogFragment {

    EditText editDoctorNotesEditText;
    Button doctorNotesHealthHistoryButtonSend;
    Button doctorNotesHealthHistoryButtonCancel;
    LinearLayout layoutEditDoctorNotes;
    private closeEditDoctorNotes callbackClose;
    ProgressBar progressBar;
    private static String typeEdit;
    private static String username;
    private static String namePathology;
    private static Integer idMisuration;

    public interface closeEditDoctorNotes {
        public void closeEditDoctorNotes();
    }


    public static EditDoctorNotesDialogFragment newInstancePathology(String usernameAdd, String namePathologyAdd) {
        username =usernameAdd;
        namePathology = namePathologyAdd;
        typeEdit="pathology";
        return new EditDoctorNotesDialogFragment();
    }

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
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        editDoctorNotesEditText = view.findViewById(R.id.editDoctorNotesEditText);
        progressBar = view.findViewById(R.id.progressBarEditDoctorNotes);
        layoutEditDoctorNotes = view.findViewById(R.id.layoutEditDoctorNotes);
        doctorNotesHealthHistoryButtonSend = view.findViewById(R.id.doctorNotesHealthHistoryButtonSend);
        doctorNotesHealthHistoryButtonCancel = view.findViewById(R.id.doctorNotesHealthHistoryButtonCancel);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {

        doctorNotesHealthHistoryButtonSend.setOnClickListener(v->saveData());
        doctorNotesHealthHistoryButtonCancel.setOnClickListener(v->closeDialog());


        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }

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

    private void closeDialog()
    {
        getDialog().dismiss();
    }



}
