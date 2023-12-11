package uniba.roadhouse.asilapp.view.home;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Access;


public class HomeFragment extends Fragment {

    TextView welcomeText;


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
        welcomeText=getView().findViewById(R.id.welcomeText);
        welcomeText.setText(getString(R.string.welcome)+"\n"+Access.getNome()+"!");
        WebView firstWebView = getActivity().findViewById(R.id.firstVideoView);
        String firstVideo = "<iframe width=\100%\" height=\100%\" src=\"https://www.youtube.com/embed/qnWoT8dD1-w\" \" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"></iframe>";
        firstWebView.loadData(firstVideo, "text/html","utf-8");
        firstWebView.getSettings().setJavaScriptEnabled(true);
        firstWebView.setWebChromeClient(new WebChromeClient());

        WebView secondWebView = getActivity().findViewById(R.id.secondVideoView);
        String secondVideo = "<iframe width=\100%\" height=\100%\" src=\"https://www.youtube.com/embed/qnWoT8dD1-w\" \" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\"></iframe>";
        secondWebView.loadData(secondVideo, "text/html","utf-8");
        secondWebView.getSettings().setJavaScriptEnabled(true);
        secondWebView.setWebChromeClient(new WebChromeClient());

        HorizontalScrollView scrollBarVide = getActivity().findViewById(R.id.scrollBarVideo);
        scrollBarVide.setHorizontalScrollBarEnabled(true);
    }

    @Override
    public void onStart() {
        setPieChartOutgoings();
        super.onStart();
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
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
        super.onResume();
    }


    private void setPieChartOutgoings()
    {
        PieChart pieChart = getActivity().findViewById(R.id.pieChart);
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





}



