package uniba.roadhouse.asilapp.controller.patient.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import uniba.roadhouse.asilapp.R;

public class OutgoingsFragment extends Fragment {
    Button addOutgoingButton;
    TextInputLayout categorySelectionOutgoingsLayout;
    AutoCompleteTextView categoriSelectionOutgoings;
    TextInputLayout valueOutgoingsLayout;
    TextInputEditText valueOutgoings;






    public OutgoingsFragment() {

    }


    public static OutgoingsFragment newInstance(String param1, String param2) {
        OutgoingsFragment fragment = new OutgoingsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outgoings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //------------RIFERIMENTI-----------------
        categorySelectionOutgoingsLayout = view.findViewById(R.id.categorySelectionOutgoingsLayout);
        categoriSelectionOutgoings = view.findViewById(R.id.categorySelectionOutgoings);
        valueOutgoingsLayout = view.findViewById(R.id.valueOutgoingsLayout);
        valueOutgoings = view.findViewById(R.id.valueOutgoings);
        addOutgoingButton = view.findViewById(R.id.addOutgoingButton);
        // Aggiungo i TextWatcher
        categoriSelectionOutgoings.addTextChangedListener(textWatcherCategory);
        valueOutgoings.addTextChangedListener(textWacherValue);
        // Disattivo il bottone di aggiunta spesa
        addOutgoingButton.setEnabled(false);
        addOutgoingButton.setAlpha((float)0.5);

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
        categorySelectionOutgoingsLayout.requestFocus();
        getCategory();
        super.onResume();
    }

    private void getCategory()
    {
        List<String> allCategory = new ArrayList<String>();
        allCategory.add(getString(R.string.food));
        allCategory.add(getString(R.string.drugs));
        allCategory.add(getString(R.string.other));
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allCategory);
        categoriSelectionOutgoings.setAdapter(adapterCategory);
    }



    TextWatcher textWatcherCategory = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            valueOutgoingsLayout.requestFocus();
            if(!valueOutgoings.getText().toString().isEmpty())
            {
                addOutgoingButton.setEnabled(true);
                addOutgoingButton.setAlpha((float)1.0);
            }
        }
    };


    TextWatcher textWacherValue = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!s.toString().isEmpty())
            {
                if(!categoriSelectionOutgoings.getText().toString().isEmpty())
                {
                    addOutgoingButton.setEnabled(true);
                    addOutgoingButton.setAlpha((float)1.0);
                }
            }
            else
            {
                addOutgoingButton.setEnabled(false);
                addOutgoingButton.setAlpha((float)0.5);
            }


        }
    };
}