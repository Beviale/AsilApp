package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Articolo;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * Schermata che contiene tutti gli articoli presenti nel database.
 */
public class AllTipsFragment extends Fragment {

    /**
     * Layout contenente i vari articoli.
     */
    LinearLayout layoutAllTips;
    /**
     * Layout relativo all'intero fragment.
     */
    ConstraintLayout layoutAllTipsFragment;
    /**
     * ProgressBar da mostrare durante il caricamento degli articoli dal database.
     */
    ProgressBar progressBar;



    public AllTipsFragment() {
        // Required empty public constructor
    }


    public static AllTipsFragment newInstance(String param1, String param2) {
        AllTipsFragment fragment = new AllTipsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_tips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //-----------RIFERIMENTI--------------
        layoutAllTips = view.findViewById(R.id.linearLayoutAllTips);
        layoutAllTipsFragment = view.findViewById(R.id.layoutAllTipsFragment);
        progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        getData();
        super.onViewCreated(view, savedInstanceState);
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
        super.onResume();
    }

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
    }

    /**
     * Crea dinamicamente le card degli articoli presenti nel database.
     */
    @SuppressLint("RestrictedApi")
    private void getData()
    {
        progressBar.setVisibility(View.VISIBLE);
        layoutAllTipsFragment.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>> future = Dao.getAllArticles(getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutAllTipsFragment.setAlpha((float)1.0);
                if(result.get("esito").toString().equals(getString(R.string.getArticlesSuccessfull)))
                {
                    ArrayList<Articolo> articles = (ArrayList<Articolo>) result.get("articles");
                    for(Articolo articolo: articles)
                    {
                        // Creo la card
                        ConstraintLayout card = new ConstraintLayout(requireContext());
                        Utility.activeAnimationOnClick(getActivity(), card);
                        card.setOnClickListener(v->openDetailTips(articolo.getId()));
                        card.setId(View.generateViewId());
                        LinearLayout.LayoutParams layoutParamsCard = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) dpToPx(getContext(), 200)
                        );
                        layoutParamsCard.topMargin=getResources().getDimensionPixelSize(R.dimen.marginBetweenArticles);
                        card.setLayoutParams(layoutParamsCard);
                        card.setBackground(getResources().getDrawable(R.drawable.rounded_all_tips));
                        card.setElevation(100);
                        layoutAllTips.addView(card);

                        // Creo l'immagine dell'articolo
                        ImageView imageFirstCard = new ImageView(requireContext());
                        imageFirstCard.setId(View.generateViewId());
                        imageFirstCard.setImageBitmap(articolo.getImmagine());
                        ConstraintLayout.LayoutParams paramsImage = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                (int) dpToPx(getContext(), 130)
                        );
                        imageFirstCard.setLayoutParams(paramsImage);
                        ConstraintSet constraintImage = new ConstraintSet();
                        constraintImage.clone(card);
                        constraintImage.connect(imageFirstCard.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) dpToPx(getContext(), 0));
                        constraintImage.connect(imageFirstCard.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 0));
                        constraintImage.applyTo(card);
                        card.addView(imageFirstCard);

                        // Creo la TextView relativa al label della data di nascita del paziente.
                        TextView titleArticle = new TextView(requireContext());
                        titleArticle.setId(View.generateViewId());
                        ConstraintLayout.LayoutParams paramsTitle = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                (int) dpToPx(getContext(), 70)
                        );
                        Integer padding =   (int) dpToPx(getContext(), 10);
                        titleArticle.setBackground(getResources().getDrawable(R.drawable.rounded_all_tips));

                        titleArticle.setPadding(padding, padding, padding, padding);
                        card.addView(titleArticle);
                        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.titillium_web_bold);
                        titleArticle.setTypeface(typeface);
                        titleArticle.setText(articolo.getTitolo());
                        titleArticle.setTextColor(getResources().getColor(R.color.black));
                        titleArticle.setLayoutParams(paramsTitle);
                        ConstraintSet constraintTitle = new ConstraintSet();
                        constraintTitle.clone(card);
                        constraintTitle.connect(titleArticle.getId(), ConstraintSet.TOP, imageFirstCard.getId(), ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 0));
                        constraintTitle.connect(titleArticle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 0));
                        constraintTitle.applyTo(card);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), result.get("esito").toString() , Toast.LENGTH_LONG).show();
                }

            });
        });
    }


    /**
     * Apre il fragment "DetailTipsFragment".
     * @param id, id dell'articolo da aprire.
     */
    private void openDetailTips(Integer id)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        fragmentTransaction.replace(R.id.homeContainerView, DetailTipsFragment.class, bundle);
        fragmentTransaction.addToBackStack(getString(R.string.homeMenuScreen));
        fragmentTransaction.commit();
    }
}