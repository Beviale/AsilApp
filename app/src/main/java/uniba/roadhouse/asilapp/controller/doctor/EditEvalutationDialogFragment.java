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
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;

public class EditEvalutationDialogFragment extends DialogFragment {
    RadioGroup evalutationHealthHistoryRadioGroup;
    Button evalutationHealthHistoryButtonSend;
    Button evalutationHealthHistoryButtonCancel;
    RadioButton healthHistoryGood;
    RadioButton healthHistoryFairlyGood;
    RadioButton healthHistoryFairlyNotGood;
    LinearLayout layoutEditEvalutation;
    ProgressBar progressBar;
    private static Integer id;
    private closeListenerEditEvalutation callbackClose;

    public interface closeListenerEditEvalutation {
        public void closeEditEvalutation();
    }


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
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart() {
        evalutationHealthHistoryButtonSend.setOnClickListener(v->saveData());
        evalutationHealthHistoryButtonCancel.setOnClickListener(v->closeDialog());
        super.onStart();
    }


    private void closeDialog()
    {
        getDialog().dismiss();
    }

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
            newEvalutation = getString(R.string.evalutationHealthHistoryValueGood);
        }
        else if(healthHistoryFairlyGood.isChecked())
        {
            newEvalutation = getString(R.string.evalutationHealthHistoryValueFairlyGood);
        }
        else
        {
            newEvalutation = getString(R.string.evalutationHealthHistoryValueNotGood);
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
