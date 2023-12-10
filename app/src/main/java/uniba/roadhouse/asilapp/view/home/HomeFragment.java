package uniba.roadhouse.asilapp.view.home;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

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
}