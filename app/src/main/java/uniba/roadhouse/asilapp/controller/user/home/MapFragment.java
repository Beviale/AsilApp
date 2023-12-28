package uniba.roadhouse.asilapp.controller.user.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import uniba.roadhouse.asilapp.R;

public class MapFragment extends Fragment {
    private LocationManager locationManager;
    private WebView mapView;

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    // PERMISSION GRANTED
                    checkGPSEnabled();
                    Log.d("PERMISSION", "GRANTED");
                } else {
                    // PERMISSION NOT GRANTED
                    returnHome();
                    Log.d("PERMISSION", "DENIED");
                }
            }
    );

    //launcher per la richiesta di intent perl'attivazione della posizione
    private ActivityResultLauncher<Intent> positionEnableResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResultCallback<ActivityResult>) result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //se l'utente ha attivato la posizione
                    Log.d("POSITION", "ENABLED");
                } else {
                    //se l'utente non ha attivato la posizione vado alla home
                    Log.d("POSITION", "DISABLED");
                    returnHome();
                }
            }
    );

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PositionFragment.
     */
    public static PositionFragment newInstance() {
        PositionFragment fragment = new PositionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_position_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = getView().findViewById(R.id.mapView);

        getView().findViewById(R.id.hospital_map_icon).setOnClickListener(v -> mapHospitalQuery());
        getView().findViewById(R.id.police_map_icon).setOnClickListener(v -> mapPoliceQuery());
        getView().findViewById(R.id.pharmacy_map_icon).setOnClickListener(v -> mapPharmacyQuery());
        getView().findViewById(R.id.town_hall_map_icon).setOnClickListener(v -> mapTownHallQuery());

    }

    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);

        requestPositionPermissionAndEnabling();
        super.onResume();
        mapView.onResume();
    }

    private void requestPositionPermissionAndEnabling() {
        //se la posizione non è supportata torno alla home
        if (locationManager == null) { returnHome(); }

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Abbiamo i permessi
            checkGPSEnabled();
        }else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostro messaggio informativo
                showExplanation(R.string.positionPermissionTitle, R.string.positionInformativa, Manifest.permission.ACCESS_FINE_LOCATION);
                Log.d("PERMISSION", "INFORMATIVA");
            }else{
                // Richiedo i permessi
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void checkGPSEnabled() {
        //verifico che ho la posizione attiva altrimenti richiedo l'attivazione
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent enableGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            positionEnableResult.launch(enableGPS);
        } else {
            setupAndStartMapView();
            Log.d("POSITION", "ENABLED");
        }
    }

    /**
     * metodo che mostra l'informativa per la richiesta del permesso di accesso alla posizione
     * @param title
     * @param message
     * @param permission
     */
    private void showExplanation(int title, int message, final String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> requestPermissionLauncher.launch(permission))
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> returnHome());
        builder.create().show();
    }

    /**
     * metodo che ritorna alla schermata home
     */
    private void returnHome() {
        ((HomeActivity) getActivity()).changeScreen(getResources().getString(R.string.homeMenuScreen));
        Toast.makeText(getActivity(), getResources().getString(R.string.positionGivePermissionMessage),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Metodo per il setup della WebView ed impostazione della schermata inziale posizionata sulla città dell'utente
     */
    private void setupAndStartMapView() {
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.setWebViewClient(new WebViewClient());
        mapView.loadUrl("https://www.google.com/maps/place/" + PositionFragment.Instance.getResidenzaUtenteAttuale().getCittaResidenza());
    }

    private void mapHospitalQuery(){
        this.mapSearchPOIAroundCoordinatesQuery(getResources().getString(R.string.map_hospital));
    }

    private void mapPoliceQuery(){
        this.mapSearchPOIAroundCoordinatesQuery(getResources().getString(R.string.map_police));
    }

    private void mapPharmacyQuery(){
        this.mapSearchPOIAroundCoordinatesQuery(getResources().getString(R.string.map_pharmacy));
    }

    private void mapTownHallQuery(){
        this.mapSearchPOIAroundCoordinatesQuery(getResources().getString(R.string.map_townhall));
    }

    private void mapSearchPOIAroundCoordinatesQuery(String poi){

        String str = "https://www.google.com/maps/search/" + poi + String.format("/@%s,%s",
                PositionFragment.Instance.getResidenzaUtenteAttuale().getLatitudine().toString(),
                PositionFragment.Instance.getResidenzaUtenteAttuale().getLongitudine().toString() + ",11z");

        mapView.loadUrl(str);
        Log.d("MAP", "HOSPITAL LOADED");

    }

}
