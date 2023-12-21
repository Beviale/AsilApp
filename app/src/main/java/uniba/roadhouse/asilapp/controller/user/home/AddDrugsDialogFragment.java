package uniba.roadhouse.asilapp.controller.user.home;

import androidx.fragment.app.DialogFragment;

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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;

public class AddDrugsDialogFragment extends DialogFragment {
    TextInputLayout nameAddDrugLayoutLayout;
    TextInputEditText nameAddDrugLayoutInput;
    TextInputLayout noteAddDrugLayoutLayout;
    TextInputEditText noteAddDrugLayoutInput;
    Button addDrugButtonCancel;
    Button addDrugButtonSend;



    public static AddDrugsDialogFragment  newInstance() {
        return new AddDrugsDialogFragment ();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_drugs_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        nameAddDrugLayoutLayout = view.findViewById(R.id.nameAddDrugLayout);
        nameAddDrugLayoutInput = view.findViewById(R.id.nameAddDrugInput);
        noteAddDrugLayoutLayout = view.findViewById(R.id.noteAddDrugLayout);
        noteAddDrugLayoutInput = view.findViewById(R.id.noteAddDrugInput);
        addDrugButtonCancel = view.findViewById(R.id.addDrugButtonCancel);
        addDrugButtonSend = view.findViewById(R.id.addDrugButtonSend);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        addDrugButtonSend.setOnClickListener(v->saveData());
        addDrugButtonCancel.setOnClickListener(v->closeDialog());

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
        if(nameAddDrugLayoutInput.getText().toString().isEmpty())
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.emptyNameAddDrugTitle), getString(R.string.emptyNameAddDrug));
        }


    }

    private void closeDialog()
    {
        getDialog().dismiss();
    }

}
