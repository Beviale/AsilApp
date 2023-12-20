package uniba.roadhouse.asilapp.controller.user.home;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;

public class EditPriorityDialogFragment extends DialogFragment {
    RadioGroup priorityMyPathologiesRadioGroup;
    Button priorityMyPathologiesButtonSend;
    Button priorityMyPathologiesButtonCancel;
    RadioButton myPathologiesLow;
    RadioButton myPathologiesMedium;
    RadioButton myPathologiesHigh;



    public static EditPriorityDialogFragment newInstance() {
        return new EditPriorityDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_priority_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        priorityMyPathologiesRadioGroup = view.findViewById(R.id.priorityMyPathologiesRadioGroup);
        priorityMyPathologiesButtonSend = view.findViewById(R.id.priorityMyPathologiesButtonSend);
        priorityMyPathologiesButtonCancel = view.findViewById(R.id.priorityMyPathologiesButtonCancel);
        myPathologiesLow = view.findViewById(R.id.myPathologiesLow);
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
        }
    }



}

