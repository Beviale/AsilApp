package uniba.roadhouse.asilapp.controller.user.home;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        // Creo una nuova linea, ossia una riga che contiene massimo due articoli.
        LinearLayout line = new LinearLayout(requireContext());
        line.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        line.setLayoutParams(layoutParamsLine);
        line.setOrientation(LinearLayout.VERTICAL);
        layoutAllTips.addView(line);

        // Creo la prima card
        ConstraintLayout firstCard = new ConstraintLayout(requireContext());
        Utility.activeAnimationOnClick(getActivity(), firstCard);
        firstCard.setOnClickListener(v->openDetailTips(1));
        firstCard.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParamsFirstCard = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) dpToPx(getContext(), 200)
        );
        firstCard.setLayoutParams(layoutParamsFirstCard);
        firstCard.setBackground(getResources().getDrawable(R.drawable.rounded_all_tips));
        firstCard.setElevation(100);
        line.addView(firstCard);


        ImageView imageFirstCard = new ImageView(requireContext());
        imageFirstCard.setId(View.generateViewId());
        imageFirstCard.setImageDrawable(getResources().getDrawable(R.drawable.doctor));
        ConstraintLayout.LayoutParams paramsImage = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                (int) dpToPx(getContext(), 130)
        );
        imageFirstCard.setLayoutParams(paramsImage);
        ConstraintSet constraintImage = new ConstraintSet();
        constraintImage.clone(firstCard);
        constraintImage.connect(imageFirstCard.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 0));
        constraintImage.connect(imageFirstCard.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 0));
        constraintImage.applyTo(firstCard);
        firstCard.addView(imageFirstCard);

        // Creo la prima card
        ConstraintLayout secondCard = new ConstraintLayout(requireContext());
        Utility.activeAnimationOnClick(getActivity(), secondCard);
        secondCard.setOnClickListener(v->openDetailTips(1));
        secondCard.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParamsSecondCard = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) dpToPx(getContext(), 200)
        );
        layoutParamsSecondCard.topMargin=(int) dpToPx(getContext(), 40);
        secondCard.setLayoutParams(layoutParamsSecondCard);
        secondCard.setBackground(getResources().getDrawable(R.drawable.rounded_all_tips));
        secondCard.setElevation(100);
        line.addView(secondCard);








    }

    private void openDetailTips(Integer id)
    {

    }
}