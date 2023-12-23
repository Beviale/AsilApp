package uniba.roadhouse.asilapp.controller.doctor;

import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Farmaco;

public class AddDrugsDialogFragment extends DialogFragment {
    TextInputLayout nameAddDrugLayoutLayout;
    TextInputEditText nameAddDrugLayoutInput;
    TextInputLayout noteAddDrugLayoutLayout;
    TextInputEditText noteAddDrugLayoutInput;
    Button addDrugButtonCancel;
    Button addDrugButtonSend;
    ProgressBar progressBar;
    LinearLayout linearLayoutDoctoActivity;
    private static String namePathology;



    public static AddDrugsDialogFragment  newInstance(String namePathologyInput) {
        namePathology = namePathologyInput;
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
        linearLayoutDoctoActivity = getActivity().findViewById(R.id.linearLayoutDoctoActivity);
        progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
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
            return;
        }
        String name = nameAddDrugLayoutInput.getText().toString();
        String note="";
        if(noteAddDrugLayoutInput.getText().toString().isEmpty())
        {
            note = getString(R.string.emptyNoteDrug);
        }
        else
        {
            note = noteAddDrugLayoutInput.getText().toString();
        }
        Farmaco addFarmaco = new Farmaco(name, note);
        closeDialog();
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutDoctoActivity.setAlpha((float)0.5);
        CompletableFuture<Map<String, Object>> future = Dao.addFarmaco();
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                linearLayoutDoctoActivity.setAlpha((float)1.0);

            });
        });



    }

    private void closeDialog()
    {
        getDialog().dismiss();
    }

}
