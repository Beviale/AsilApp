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

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;

public class EditDoctorNotesDialogFragment extends DialogFragment {

    EditText editDoctorNotesEditText;
    Button doctorNotesHealthHistoryButtonSend;
    Button doctorNotesHealthHistoryButtonCancel;
    private closeEditDoctorNotes callbackClose;

    public interface closeEditDoctorNotes {
        public void closeEditDoctorNotes();
    }


    public static EditDoctorNotesDialogFragment newInstance() {
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
        }
        closeDialog();
        callbackClose.closeEditDoctorNotes();

    }

    private void closeDialog()
    {
        getDialog().dismiss();
    }



}
