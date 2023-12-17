package uniba.roadhouse.asilapp.controller.patient.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Access;


public class HomeFragment extends Fragment {
    ImageView arrowToOutgoingsFragment;

    TextView welcomeText;
    WebView firstWebView;
    WebView secondWebView;
    HorizontalScrollView scrollBarVideo;
    PieChart pieChart;


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
        arrowToOutgoingsFragment = view.findViewById(R.id.arrowToOutgoingsFragment);
        //----------RIFERIMENTI----------
        secondWebView = view.findViewById(R.id.secondVideoView);
        welcomeText = view.findViewById(R.id.welcomeText);
        firstWebView = view.findViewById(R.id.firstVideoView);
        scrollBarVideo = view.findViewById(R.id.scrollBarVideo);
        pieChart = view.findViewById(R.id.pieChartHome);


    }

    @Override
    public void onStart() {
        //-------------------------LISTENER----------------
        arrowToOutgoingsFragment.setOnClickListener(V->openOutgoingsFragment());



        welcomeText.setText(getString(R.string.welcome)+"\n"+Access.getNome()+"!");
        scrollBarVideo.setHorizontalScrollBarEnabled(true);
        super.onStart();
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);
        toolbar.inflateMenu(R.menu.menu_home_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.settings)
                {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(getString(R.string.settingsMenuScreen));
                    fragmentTransaction.replace(R.id.homeContainerView, SettingsFragment.class, null);
                    fragmentTransaction.commit();
                }

                return true;
            }
        });
        setVideo();
        setPieChartOutgoings();
        super.onResume();
    }


    private void setPieChartOutgoings()
    {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Farmaci"));
        entries.add(new PieEntry(20f, "Cibo"));
        entries.add(new PieEntry(50f, "Altro"));
        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        pieChart.setUsePercentValues(true);
        PieDataSet dataSet = new PieDataSet(entries, "Pie Chart");
        dataSet.setValueTextSize((float)15);
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        dataSet.setColors(getResources().getColor(R.color.drugsColor), getResources().getColor(R.color.foodColor), getResources().getColor(R.color.otherColor));
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
    }


    private void openOutgoingsFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeContainerView, OutgoingsFragment.class, null);
        fragmentTransaction.addToBackStack(getString(R.string.homeMenuScreen));
        fragmentTransaction.commit();
    }


    private void setVideo()
    {
        String firstVideo = "<iframe width=\100%\" height=\100%\" src=\"https://www.youtube.com/embed/qnWoT8dD1-w\" \" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"></iframe>";
        firstWebView.loadData(firstVideo, "text/html","utf-8");
        firstWebView.getSettings().setJavaScriptEnabled(true);
        firstWebView.setWebChromeClient(new WebChromeClient());

        String secondVideo = "<iframe width=\100%\" height=\100%\" src=\"https://www.youtube.com/embed/qnWoT8dD1-w\" \" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"></iframe>";
        secondWebView.loadData(secondVideo, "text/html","utf-8");
        secondWebView.getSettings().setJavaScriptEnabled(true);
        secondWebView.setWebChromeClient(new WebChromeClient());
    }





}



