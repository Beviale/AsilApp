package uniba.roadhouse.asilapp.controller.user.home;

import android.app.Activity;
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

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.CategoriaSpesaEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Articolo;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Spesa;

/**
 * Schermata home principale dell'account utente.
 */
public class HomeFragment extends Fragment {
    /**
     * Icona che consente di aprire il fragment di gestione spese.
     */
    ImageView arrowToOutgoingsFragment;
    /**
     * Testo di benvenuto.
     */
    TextView welcomeText;
    /**
     * Titolo del primo articolo della sezione "Tips"
     */
    TextView titleFirstArticle;
    /**
     * Titolo del secondo articolo della sezione "Tips"
     */
    TextView titleSecondArticle;
    /**
     * Prima webview relativa ai video.
     */
    WebView firstWebView;
    /**
     * Seconda webview relativa ai video.
     */
    WebView secondWebView;
    /**
     * Contiene i video.
     */
    HorizontalScrollView scrollBarVideo;
    /**
     * Grafico a torta che mostra tutte le spese associate all'utente suddivise per categoria.
     */
    PieChart pieChart;
    /**
     * Card del primo articolo.
     */
    ConstraintLayout firstTips;
    /**
     * Card del secondo articolo.
     */
    ConstraintLayout secondTips;
    /**
     * Immagine del primo articolo.
     */
    ImageView imageFirstArticle;
    /**
     * Immagine del secondo articolo.
     */
    ImageView imageSecondArticle;
    /**
     * Consente lo "swipe-to-refresh" dell'intero fragment.
     */
    SwipeRefreshLayout swipeRefreshLayoutHomeFragment;
    /**
     * Immagine del numero di telefono di emergenza nazionale.
     */
    ImageView policeNumberImage;
    /**
     * Numero di telefono di emergenza nazionale.
     */
    TextView policeNumberValue;
    /**
     * Immagine del numero di telefono dell'UNHCR
     */
    ImageView unhcrNumberImage;
    /**
     * Numero di telefono dell'UNHCR
     */
    TextView unhcrNumbervalue;
    /**
     * Immagine del numero di telefono della commissione per il diritto di asilo.
     */
    ImageView commissionNumberImage;
    /**
     * Numero di telefono della commissione per il diritto di asilo.
     */
    TextView commissionNumberValue;
    /**
     * Framelayout della sezione video.
     */
    FrameLayout frameLayoutVideoHome;
    /**
     * ProgressBar della sezione video.
     */
    ProgressBar progressBarVideoHome;
    /**
     * Framelayout della sezione di gestione spese.
     */
    FrameLayout frameLayoutChartHome;
    /**
     * ProgressBar della sezione di gestione spese.
     */
    ProgressBar progressBarChartHome;
    /**
     * Icona che consente di aprire il fragment contenente tutti gli articolo della sezione "Tips"
     */
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
        welcomeText.setText(getString(R.string.welcome)+"\n"+ UserLogin.getNome()+"!");
        scrollBarVideo.setHorizontalScrollBarEnabled(true);
        // Prendo i video dal db.
        loadVideo();
        // Prendo i primi due articolo dal db.
        loadTips();
        // Prendo i dati delle spese associate all'utente dal DB. Servono per la creazione del grafico a torta.
        loadOutgoings();
        super.onStart();
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);
        toolbar.inflateMenu(R.menu.settings_menu);
        // Attivo le impostazioni da action overflow
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
        super.onResume();
    }

    /**
     * Prende dal database i link dei due video associati alla categoria di utente (se richiedente asilo o titolare di protezione internazionale).
     * Una volta presi i link, chiama il metodo openVideo().
     */
    private void loadVideo()
    {
        progressBarVideoHome.setVisibility(View.VISIBLE);
        frameLayoutVideoHome.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>>  future = Dao.getAllVideoByTipo(UserLogin.getTipoAsiloProtezione(), getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                try{
                    progressBarVideoHome.setVisibility(View.GONE);
                    frameLayoutVideoHome.setAlpha((float)1.0);
                    if(result.get("esito").toString().equals(getString(R.string.getVideoSuccessfull)))
                    {
                        ArrayList<String> links = (ArrayList<String>) result.get("links");
                        if(links.size()==2)
                        {
                            openVideo(links.get(0), links.get(1));
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Activity activity = new HomeActivity();
                    activity.onBackPressed();
                }
            });
        });
    }


    /**
     * Apre il fragment "OutgoingsFragment", ossia quello relativo alla gestione delle spese.
     */
    private void openOutgoingsFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, OutgoingsFragment.class, null);
        fragmentTransaction.addToBackStack(getString(R.string.homeMenuScreen));
        fragmentTransaction.commit();
    }


    /**
     * Presi due link Youtube, li mostra attraverso le due webview del fragment.
     * @param firstLink, primo link Youtube.
     * @param secondLink, secondo link Youtube.
     */
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


    /**
     * Apre il fragment "DetailTipsFragment", ossia quello che consente la visualizzazione di un articolo specifico.
     * @param id
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


    /**
     * Apre un intent di tipo ACTION_DIAL.
     * @param number, numero di telefono da allegare alla richiesa di intent.
     */
    private void call(String number)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        getActivity().startActivity(intent);
    }


    /**
     * Prende dal database i primi due articoli e li mostra nelle due card del fragment.
     */
    private void loadTips()
    {
       CompletableFuture<Map<String,?>>  future = Dao.getFirst2Articles(getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                try{
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
                }catch(Exception e){
                    Activity activity = new HomeActivity();
                    activity.onBackPressed();
                }
            });
        });
    }


    /**
     * Carica dal database tutte le spese associate all'utente, li trasforma in percentuali e crea il grafico a torta.
     */
    private void loadOutgoings()
    {
        progressBarChartHome.setVisibility(View.VISIBLE);
        frameLayoutChartHome.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>> future = Dao.getAllSpese(UserLogin.getUsername(), -1, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                try{
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
                }catch (Exception e){
                    Activity activity = new HomeActivity();
                    activity.onBackPressed();
                }
            });
        });

    }


    /**
     * Apre il fragment "AllTipsFragment", ossia quello che consente la visualizzazione di tutti gli articoli.
     */
    private void openAllTipsFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, AllTipsFragment.class, null);
        fragmentTransaction.addToBackStack(getString(R.string.homeMenuScreen));
        fragmentTransaction.commit();
    }

}



