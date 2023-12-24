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

import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.Dao;

public class EditPriorityDialogFragment extends DialogFragment {
    RadioGroup priorityMyPathologiesRadioGroup;
    Button priorityMyPathologiesButtonSend;
    Button priorityMyPathologiesButtonCancel;
    RadioButton myPathologiesLow;
    RadioButton myPathologiesMedium;
    RadioButton myPathologiesHigh;
    LinearLayout layoutEditPriority;
    private closeListenerEditPriority callbackClose;
    private static String username;
    private static String namePathology;
    ProgressBar progressBar;

    public interface closeListenerEditPriority {
        public void closeEditPriority();
    }



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
        priorityMyPathologiesButtonSend.setOnClickListener(v->saveData());
        priorityMyPathologiesButtonCancel.setOnClickListener(v->closeDialog());


        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }



    private void closeDialog()
    {
        getDialog().dismiss();
    }

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
            newPriority = getString(R.string.priorityMyPathologiesValueLow);
        }
        else if (myPathologiesMedium.isChecked())
        {
            newPriority = getString(R.string.priorityMyPathologiesValueMedium);
        }
        else if (myPathologiesHigh.isChecked())
        {
            newPriority = getString(R.string.priorityMyPathologiesValueHigh);
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

