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

import uniba.roadhouse.asilapp.R;

public class EvaluateFragment extends DialogFragment {

    RatingBar ratingEvaluate;
    TextView valueRatingEvaluate;
    private static String type;
    Button save;

    public static EvaluateFragment newInstance(String typeNewInstance) {
        type = typeNewInstance;
        return new EvaluateFragment();
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
        Float ratingSharedPref = sh.getFloat("rating".concat(type), (float)0);
        ratingEvaluate.setRating(ratingSharedPref);
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
       save.setOnClickListener(v->savePreferences());


        super.onStart();
    }

    private void savePreferences()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putFloat("rating".concat(type), ratingEvaluate.getRating());
        myEdit.commit();
        Toast.makeText(getActivity(), getString(R.string.successfullySaved), Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
