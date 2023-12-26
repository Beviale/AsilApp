package uniba.roadhouse.asilapp.controller.user.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Articolo;
import uniba.roadhouse.asilapp.model.dao.Dao;


public class DetailTipsFragment extends Fragment {

    TextView titleDetailTips;
    TextView textDetailTips;
    ProgressBar progressBar;
    ConstraintLayout layoutDetailTipsFragment;
    ImageView imageDetailTips;
    private Integer id;





    public DetailTipsFragment() {

    }


    public static DetailTipsFragment newInstance(String param1, String param2) {
        DetailTipsFragment fragment = new DetailTipsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_tips, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //--------RIFERIMENTI---------------
        titleDetailTips = view.findViewById(R.id.titleDetailTips);
        imageDetailTips = view.findViewById(R.id.imageDetailTips);
        textDetailTips = view.findViewById(R.id.textDetailTips);
        progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        layoutDetailTipsFragment = view.findViewById(R.id.layoutDetailTipsFragment);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        getData();
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
        toolbar.inflateMenu(R.menu.share_and_evaluate_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.shareDetailTips)
                {
                    String share = titleDetailTips.getText().toString().concat("\n\n").concat(textDetailTips.getText().toString());
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, share);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.evaluateDetailTips)
                {
                    EvaluateTipsDialogFragment dialogFragment = EvaluateTipsDialogFragment.newInstance(id);
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "EvaluateFragment");
                }
                return true;
            }
        });
        super.onResume();
    }

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
    }

    private void getData()
    {
        progressBar.setVisibility(View.VISIBLE);
        layoutDetailTipsFragment.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>> future = Dao.getArticle(id, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutDetailTipsFragment.setAlpha((float)1.0);
                if(result.get("esito").toString().equals(getString(R.string.getArticlesSuccessfull)))
                {
                    Articolo articolo = (Articolo) result.get("article");
                    titleDetailTips.setText(articolo.getTitolo());
                    textDetailTips.setText(articolo.getTesto());
                    imageDetailTips.setImageBitmap(articolo.getImmagine());
                }
                else
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_LONG).show();
                }

            });
        });


    }
}