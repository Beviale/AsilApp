package uniba.roadhouse.asilapp.controller.user.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * DialogFragment che consente all'utente di inviare una valutazione di un articolo.
 */
public class EvaluateTipsDialogFragment extends DialogFragment {
    /**
     * RatingBar che consente all'utente di esprimere la valutazione in maniera user-friendly.
     */
    RatingBar ratingEvaluate;
    /**
     * Contiene la valutazione inserita dall'utente espressa sottoforma numerica.
     */
    TextView valueRatingEvaluate;
    /**
     * Button che consente il salvataggio della valutazione inserita.
     */
    Button save;
    /**
     * ProgressBar da mostrare durante l'esecuzione delle query del database.
     */
    ProgressBar progressBar;
    /**
     * Layout dell'intero DialogFragment.
     */
    ConstraintLayout layoutEvalutateFragment;
    /**
     * Id dell'articolo da valutare.
     */
    private static Integer id;

    public static EvaluateTipsDialogFragment newInstance(Integer idAdd) {
        id=idAdd;
        return new EvaluateTipsDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.evaluate_tips_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------------
        ratingEvaluate = view.findViewById(R.id.ratingEvaluate);
        progressBar = view.findViewById(R.id.progressBarEvalutateFragment);
        layoutEvalutateFragment = view.findViewById(R.id.layoutEvalutateFragment);
        valueRatingEvaluate = view.findViewById(R.id.valueRatingEvaluate);
        save = view.findViewById(R.id.buttonEvaluate);
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
        getEvalutation();
        super.onStart();
    }


    /**
     * Salva nel database la valutazione inserita.
     */
    private void saveOnDatabase()
    {
        progressBar.setVisibility(View.VISIBLE);
        layoutEvalutateFragment.setAlpha((float)0.5);
        CompletableFuture<String> future = Dao.storeArticoleValutazione(id, UserLogin.getUsername(), Float.valueOf(valueRatingEvaluate.getText().toString()), getActivity()) ;
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutEvalutateFragment.setAlpha((float)1.0);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            });
        });
    }


    /**
     * Prende dal database la valutazione inserita precedentemente dall'utente e la imposta alla ratingbar.
     * Se non è stata inserita nessuna valutazione, la ratingbar viene impostata di default a 0.
     */
    private void getEvalutation()
    {
        progressBar.setVisibility(View.VISIBLE);
        layoutEvalutateFragment.setAlpha((float)0.5);
        CompletableFuture<Map<String, ?>> future = Dao.getArticleValutazione(id, UserLogin.getUsername(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutEvalutateFragment.setAlpha((float)1.0);
                if(result.get("esito").toString().equals(getString(R.string.getArticleValuationSuccessfull)))
                {
                    ratingEvaluate.setRating(Float.valueOf(result.get("valutazione").toString()));
                }
                if(result.get("esito").toString().equals(getString(R.string.getArticlesValuationNoValuation)))
                {
                    ratingEvaluate.setRating((float)0.0);
                }
            });
        });
    }
}
