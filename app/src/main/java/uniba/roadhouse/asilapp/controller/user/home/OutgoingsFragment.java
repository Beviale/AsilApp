package uniba.roadhouse.asilapp.controller.user.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.CategoriaSpesaEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Spesa;

public class OutgoingsFragment extends Fragment {
    Button addOutgoingButton;
    TextInputLayout categorySelectionOutgoingsLayout;
    AutoCompleteTextView categorySelectionOutgoings;
    TextInputLayout valueOutgoingsLayout;
    TextInputEditText valueOutgoings;
    ConstraintLayout layoutOutgoinsFragment;
    ProgressBar progressBar;
    private static float MAX_VALUE=5000;
    private static float MIN_VALUE=(float)0.5;






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
        categorySelectionOutgoings = view.findViewById(R.id.categorySelectionOutgoings);
        valueOutgoingsLayout = view.findViewById(R.id.valueOutgoingsLayout);
        layoutOutgoinsFragment = view.findViewById(R.id.layoutOutgoinsFragment);
        progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        valueOutgoings = view.findViewById(R.id.valueOutgoings);
        addOutgoingButton = view.findViewById(R.id.addOutgoingButton);
        // Aggiungo i TextWatcher
        categorySelectionOutgoings.addTextChangedListener(textWatcherCategory);
        valueOutgoings.addTextChangedListener(textWacherValue);
        // Disattivo il bottone di aggiunta spesa
        addOutgoingButton.setEnabled(false);
        addOutgoingButton.setAlpha((float)0.5);

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        //----------LISTENER--------------------
        addOutgoingButton.setOnClickListener(v->addOutgoing());
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

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
    }

    private void getCategory()
    {
        List<String> allCategory = new ArrayList<String>();
        allCategory.add(getString(R.string.food));
        allCategory.add(getString(R.string.drugs));
        allCategory.add(getString(R.string.other));
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allCategory);
        categorySelectionOutgoings.setAdapter(adapterCategory);
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
                if(!categorySelectionOutgoings.getText().toString().isEmpty())
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



    private void addOutgoing()
    {
        String category = categorySelectionOutgoings.getText().toString();
        Float value = Float.valueOf(valueOutgoings.getText().toString());
        if(value>MAX_VALUE)
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.valueOutgoindTooHightTitle), getString(R.string.valueOutgoindTooHight).concat(" ").concat(String.valueOf(MAX_VALUE)).concat("."));
            return;
        }
        if(value<MIN_VALUE)
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.valueOutgoindTooLowTitle), getString(R.string.valueOutgoindTooLow).concat(" ").concat(String.valueOf(MIN_VALUE)).concat("."));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutOutgoinsFragment.setAlpha((float)0.5);
        String categoryToEnum =  categorySelectionOutgoings.getText().toString();
        Spesa addSpesa = new Spesa(convertCategoryStringToEnum(categoryToEnum), Double.valueOf(valueOutgoings.getText().toString()), Timestamp.now(), AccessUser.getUsername());
        CompletableFuture<String> future = Dao.storeSpesa(addSpesa, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutOutgoinsFragment.setAlpha((float)1.0);
                Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_LONG).show();
            });
        });
    }


    private CategoriaSpesaEnum convertCategoryStringToEnum(String category)
    {
        String cibo = getString(R.string.food);
        String farmaci = getString(R.string.drugs);
        if (category.equals(cibo))
        {
            return CategoriaSpesaEnum.CIBO;
        }
        else if(category.equals(farmaci))
        {
            return CategoriaSpesaEnum.FARMACI;
        }
        else
        {
            return CategoriaSpesaEnum.ALTRO;
        }
    }
}