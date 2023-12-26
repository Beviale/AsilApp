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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;


public class AllTipsFragment extends Fragment {

    /**
     * Layout contenente i vari articoli.
     */
    LinearLayout layoutAllTips;



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

    /**
     * Crea dinamicamente le card degli articoli presenti nel database.
     */
    @SuppressLint("RestrictedApi")
    private void getData()
    {

        // Creo la card
        ConstraintLayout card = new ConstraintLayout(requireContext());
        Utility.activeAnimationOnClick(getActivity(), card);
        card.setOnClickListener(v->openDetailTips(1));
        card.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParamsCard = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) dpToPx(getContext(), 200)
        );
        card.setLayoutParams(layoutParamsCard);
        card.setBackground(getResources().getDrawable(R.drawable.rounded_all_tips));
        card.setElevation(100);
        layoutAllTips.addView(card);

        // Immagine dell'articolo
        ImageView imageFirstCard = new ImageView(requireContext());
        imageFirstCard.setId(View.generateViewId());
        imageFirstCard.setImageDrawable(getResources().getDrawable(R.drawable.doctor));
        ConstraintLayout.LayoutParams paramsImage = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
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
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                (int) dpToPx(getContext(), 70)
        );
        Integer padding =   (int) dpToPx(getContext(), 10);
        titleArticle.setPadding(padding, padding, padding, padding);
        card.addView(titleArticle);
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.titillium_web_bold);
        titleArticle.setTypeface(typeface);
        titleArticle.setText("SAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        titleArticle.setTextColor(getResources().getColor(R.color.black));
        titleArticle.setLayoutParams(paramsTitle);
        ConstraintSet constraintTitle = new ConstraintSet();
        constraintTitle.clone(card);
        constraintTitle.connect(titleArticle.getId(), ConstraintSet.TOP, imageFirstCard.getId(), ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 0));
        constraintTitle.connect(titleArticle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 0));
        constraintTitle.applyTo(card);
    }

    private void openDetailTips(Integer id)
    {

    }
}