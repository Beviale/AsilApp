package uniba.roadhouse.asilapp.controller.user.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;

public class EvaluateTipsDialogFragment extends DialogFragment {

    RatingBar ratingEvaluate;
    TextView valueRatingEvaluate;
    Button save;
    private static Integer id;

    public static EvaluateTipsDialogFragment newInstance(Integer idAdd) {
        id=idAdd;
        return new EvaluateTipsDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.evaluate_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        ratingEvaluate = view.findViewById(R.id.ratingEvaluate);
        valueRatingEvaluate = view.findViewById(R.id.valueRatingEvaluate);
        save = view.findViewById(R.id.buttonEvaluate);

        // Recupero la valutazione dell'app se già precedentemente memorizzata.
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        valueRatingEvaluate.setText(String.valueOf(ratingEvaluate.getRating()));
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {

        //-------------LISTENER----------------
        ratingEvaluate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valueRatingEvaluate.setText(String.valueOf(rating));
            }
        });
       save.setOnClickListener(v->saveOnDatabase());


        super.onStart();
    }

    private void saveOnDatabase()
    {
        CompletableFuture<String> future = Dao.storeArticoleValutazione(id, AccessUser.getUsername(), Float.valueOf(valueRatingEvaluate.getText().toString()), getActivity()) ;
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            });
        });

    }
}
