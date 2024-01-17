package uniba.roadhouse.asilapp.controller.user.home;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.DownloadUtil;

public class MyRecidencyFragment extends Fragment {
    private final String URL_SERVIZI_ABITATIVI = "https://unibari-my.sharepoint.com/:b:/r/personal/a_bevilacqua11_studenti_uniba_it/Documents/AsilApp/Regolamento_Servizi_Abitativi.pdf?download=1";
    private final String URL_DOCUMENTAZIONE = "https://unibari-my.sharepoint.com/:b:/r/personal/a_bevilacqua11_studenti_uniba_it/Documents/AsilApp/Regolamento_Documentazione.pdf?download=1";

    public MyRecidencyFragment() {
        // Required empty public constructor
    }

    public static MyRecidencyFragment newInstance() {
        MyRecidencyFragment fragment = new MyRecidencyFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_position_recidency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try{
            // Imposto il test a seconda della residenza attuale
            ((TextView) getView().findViewById(R.id.nome_residenza)).setText(PositionFragment.Instance.getResidenzaUtenteAttuale().getNomeResidenza().toUpperCase());
            ((EditText) getView().findViewById(R.id.residencyDescription)).setText(PositionFragment.Instance.getResidenzaUtenteAttuale().getDescrizioneResidenza());
        }catch (Exception e)
        {
            Activity activity = new HomeActivity();
            activity.onBackPressed();
        }
        // Imposto stile e listener per i download
        ((TextView)getView().findViewById(R.id.download_servizi_abitativi)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        ((TextView)getView().findViewById(R.id.download_documentazione)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        getView().findViewById(R.id.download_servizi_abitativi).setOnClickListener( v -> DownloadUtil.startDownload(getActivity(), this.URL_SERVIZI_ABITATIVI));
        getView().findViewById(R.id.download_documentazione).setOnClickListener(v -> DownloadUtil.startDownload(getActivity(), this.URL_DOCUMENTAZIONE));

    }

}