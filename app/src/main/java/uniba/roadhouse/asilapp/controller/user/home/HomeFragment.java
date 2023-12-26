package uniba.roadhouse.asilapp.controller.user.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.CategoriaSpesaEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Articolo;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Spesa;


public class HomeFragment extends Fragment {
    ImageView arrowToOutgoingsFragment;
    TextView welcomeText;
    TextView titleFirstArticle;
    TextView titleSecondArticle;
    WebView firstWebView;
    WebView secondWebView;
    HorizontalScrollView scrollBarVideo;
    PieChart pieChart;
    ConstraintLayout firstTips;
    ConstraintLayout secondTips;
    ImageView imageFirstArticle;
    ImageView imageSecondArticle;
    SwipeRefreshLayout swipeRefreshLayoutHomeFragment;
    ImageView policeNumberImage;
    TextView policeNumberValue;
    ImageView unhcrNumberImage;
    TextView unhcrNumbervalue;
    ImageView commissionNumberImage;
    TextView commissionNumberValue;
    FrameLayout frameLayoutVideoHome;
    ProgressBar progressBarVideoHome;
    FrameLayout frameLayoutChartHome;
    ProgressBar progressBarChartHome;
    ImageView arrowToAllTipsFragment;



    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //----------RIFERIMENTI----------
        arrowToOutgoingsFragment = view.findViewById(R.id.arrowToOutgoingsFragment);
        secondWebView = view.findViewById(R.id.secondVideoView);
        policeNumberImage = view.findViewById(R.id.policeNumberImage);
        policeNumberValue = view.findViewById(R.id.policeNumberValue);
        unhcrNumberImage = view.findViewById(R.id.unhcrNumberImage);
        unhcrNumbervalue = view.findViewById(R.id.unchrNumberValue);
        imageFirstArticle = view.findViewById(R.id.imageFirstArticle);
        imageSecondArticle = view.findViewById(R.id.imageSecondArticle);
        arrowToAllTipsFragment = view.findViewById(R.id.arrowToAllTipsFragment);
        commissionNumberImage = view.findViewById(R.id.commissionNumberImage);
        titleFirstArticle = view.findViewById(R.id.titleFirstArticle);
        titleSecondArticle = view.findViewById(R.id.titleSecondArticle);
        commissionNumberValue = view.findViewById(R.id.commissionNumberValue);
        swipeRefreshLayoutHomeFragment = view.findViewById(R.id.swipereFreshLayoutHomeFragment);
        welcomeText = view.findViewById(R.id.welcomeText);
        firstWebView = view.findViewById(R.id.firstVideoView);
        scrollBarVideo = view.findViewById(R.id.scrollBarVideo);
        pieChart = view.findViewById(R.id.pieChartHome);
        firstTips = view.findViewById(R.id.firstCardTips);
        secondTips = view.findViewById(R.id.secondCardTips);
        frameLayoutVideoHome = view.findViewById(R.id.frameLayoutVideoHome);
        progressBarVideoHome = view.findViewById(R.id.progressBarVideoHome);
        frameLayoutChartHome = view.findViewById(R.id.frameLayoutChartHome);
        progressBarChartHome = view.findViewById(R.id.progressBarChartHome);
    }

    @Override
    public void onStart() {
        //-------------------------LISTENER----------------
        arrowToOutgoingsFragment.setOnClickListener(V->openOutgoingsFragment());
        policeNumberImage.setOnClickListener(v->call(policeNumberValue.getText().toString()));
        unhcrNumberImage.setOnClickListener(v->call(unhcrNumbervalue.getText().toString()));
        commissionNumberImage.setOnClickListener(v->call(commissionNumberValue.getText().toString()));
        arrowToAllTipsFragment.setOnClickListener(v->openAllTipsFragment());
        swipeRefreshLayoutHomeFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homeContainerView, HomeFragment.class, null).commit();
                swipeRefreshLayoutHomeFragment.setRefreshing(false);
            }
        });
        welcomeText.setText(getString(R.string.welcome)+"\n"+ AccessUser.getNome()+"!");
        loadVideo();
        loadTips();
        loadOutgoings();
        scrollBarVideo.setHorizontalScrollBarEnabled(true);
        super.onStart();
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);
        toolbar.inflateMenu(R.menu.settings_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.settings)
                {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(getString(R.string.homeMenuScreen));
                    fragmentTransaction.replace(R.id.homeContainerView, SettingsFragment.class, null);
                    fragmentTransaction.commit();
                }

                return true;
            }
        });
        loadVideo();
        super.onResume();
    }

    private void loadVideo()
    {

        progressBarVideoHome.setVisibility(View.VISIBLE);
        frameLayoutVideoHome.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>>  future = Dao.getAllVideoByTipo(AccessUser.getTipoAsiloProtezione(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBarVideoHome.setVisibility(View.GONE);
                frameLayoutVideoHome.setAlpha((float)1.0);
                if(result.get("esito").toString().equals(getString(R.string.getVideoSuccessfull)))
                {
                   ArrayList<String> links = (ArrayList<String>) result.get("links");
                   openVideo(links.get(0), links.get(1));
                }
                else
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }




    private void openOutgoingsFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, OutgoingsFragment.class, null);
        fragmentTransaction.addToBackStack(getString(R.string.homeMenuScreen));
        fragmentTransaction.commit();
    }


    private void openVideo(String firstLink, String secondLink)
    {
        String firstVideo = "<iframe width=\100%\" height=\100%\" src=\" https://www.youtube.com/embed/" + firstLink +"\" \" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"></iframe>";
        firstWebView.loadData(firstVideo, "text/html","utf-8");
        firstWebView.getSettings().setJavaScriptEnabled(true);
        firstWebView.setWebChromeClient(new WebChromeClient());

        String secondVideo = "<iframe width=\100%\" height=\100%\" src=\" https://www.youtube.com/embed/" + secondLink +"\" \" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"></iframe>";
        secondWebView.loadData(secondVideo, "text/html","utf-8");
        secondWebView.getSettings().setJavaScriptEnabled(true);
        secondWebView.setWebChromeClient(new WebChromeClient());
    }


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


    /**
     * Apre un intent di tipo ACTION_DIAL.
     * @param number, numero di telefono da allegare alla richiesa di intent.
     */
    private void call(String number)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        getActivity().startActivity(intent);
    }


    private void loadTips()
    {
       CompletableFuture<Map<String,?>>  future = Dao.getFirst2Articles(getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                if(result.get("esito").toString().equals(getString(R.string.getArticlesSuccessfull)))
                {
                    ArrayList<Articolo> articles = (ArrayList<Articolo>) result.get("articles");
                    Articolo firstArticle = articles.get(0);
                    titleFirstArticle.setText(firstArticle.getTitolo());
                    imageFirstArticle.setImageBitmap(firstArticle.getImmagine());
                    firstTips.setOnClickListener(v->openDetailTips(firstArticle.getId()));
                    Articolo secondArticle = articles.get(1);
                    titleSecondArticle.setText(secondArticle.getTitolo());
                    imageSecondArticle.setImageBitmap(secondArticle.getImmagine());
                    secondTips.setOnClickListener(v->openDetailTips(secondArticle.getId()));

                }
                else
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadOutgoings()
    {
        progressBarChartHome.setVisibility(View.VISIBLE);
        frameLayoutChartHome.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>> future = Dao.getAllSpese(AccessUser.getUsername(), -1, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBarChartHome.setVisibility(View.GONE);
                frameLayoutChartHome.setAlpha((float)1.0);
                if(result.get("esito").toString().equals(getString(R.string.getSpeseSuccessfull)))
                {
                    List<Spesa> speseCibo = (List<Spesa>) result.get(CategoriaSpesaEnum.CIBO.toString());
                    List<Spesa> speseFarmaci = (List<Spesa>) result.get(CategoriaSpesaEnum.FARMACI.toString());
                    List<Spesa> speseAltro = (List<Spesa>) result.get(CategoriaSpesaEnum.ALTRO.toString());
                    Double totalFood=0.0;
                    Double totalDrugs=0.0;
                    Double totalOther=0.0;
                    Double total=0.0;
                    for(Spesa spesa: speseCibo)
                    {
                        totalFood = totalFood + spesa.getCosto();
                    }
                    for(Spesa spesa: speseFarmaci)
                    {
                        totalDrugs = totalFood + spesa.getCosto();
                    }
                    for(Spesa spesa: speseAltro)
                    {
                        totalOther = totalFood + spesa.getCosto();
                    }
                    total = totalFood + totalDrugs + totalOther;
                    Double foodPercent = (totalFood/total) * 100;
                    Double drugsPercent = (totalDrugs/total) * 100;
                    Double otherPercent = (totalOther/total) * 100;
                    Utility.setPieChartOutgoings(pieChart, foodPercent.floatValue(), drugsPercent.floatValue(), otherPercent.floatValue(), getActivity());
                }
                else
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                }



            });
        });

    }

    private void openAllTipsFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, AllTipsFragment.class, null);
        fragmentTransaction.addToBackStack(getString(R.string.homeMenuScreen));
        fragmentTransaction.commit();
    }

}



