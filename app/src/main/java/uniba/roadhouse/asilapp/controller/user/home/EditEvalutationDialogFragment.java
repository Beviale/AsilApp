package uniba.roadhouse.asilapp.controller.user.home;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;

public class EditEvalutationDialogFragment extends DialogFragment {
    RadioGroup evalutationHealthHistoryRadioGroup;
    Button evalutationHealthHistoryButtonSend;
    Button evalutationHealthHistoryButtonCancel;
    RadioButton healthHistoryGood;
    RadioButton healthHistoryFairlyGood;
    RadioButton healthHistoryFairlyNotGood;


    public static EditEvalutationDialogFragment newInstance() {
        return new EditEvalutationDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_evalutation_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        evalutationHealthHistoryRadioGroup = view.findViewById(R.id.evalutationHealthHistoryRadioGroup);
        evalutationHealthHistoryButtonSend = view.findViewById(R.id.evalutationHealthHistoryButtonSend);
        evalutationHealthHistoryButtonCancel = view.findViewById(R.id.evalutationHealthHistoryButtonCancel);
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
        }

    }


}
