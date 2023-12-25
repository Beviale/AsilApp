package uniba.roadhouse.asilapp.controller.user.home;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.SystemBarStyle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Array;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.doctor.DoctorActivity;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.user.home.BluetoothConnectionThread;
import uniba.roadhouse.asilapp.controller.user.home.HomeActivity;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
import uniba.roadhouse.asilapp.model.dao.Dao;


public class HealthBoxFragment extends Fragment {
    private Handler handlerAnimation = new Handler();
    private Boolean statusAnimation = false;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket btSocket = null;
    private boolean boxFound=false;
    private boolean pendingMisuration=false;
    private BluetoothConnectionThread btConnThread;
    //launchr per il litsernere della richiesta di permesso per il bluetooth
    private ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean finalResult = true;
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    if (!entry.getValue().booleanValue()) {
                        // PERMISSION DENIED
                        finalResult = false;
                    }
                }
                if (finalResult) {
                    // PERMISSION GRANTED
                    checkBluetoothEnabled();
                    Log.d("PERMISSION", "GRANTED");
                } else {
                    // PERMISSION NOT GRANTED
                    returnHome();
                    Log.d("PERMISSION", "DENIED");
                }
            });
    //launcher per la richiesta di intent perl'attivazione del bluetooth
    private ActivityResultLauncher<Intent> bluetoothEnableResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResultCallback<ActivityResult>) result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //se l'utente ha attivato il bluetooth avvio a ricerca dei dispositivi
                    Log.d("BLUETOOTH_", "ENABLED");
                } else {
                    //se l'utente non ha attivato il bluetooth vado alla home
                    Log.d("BLUETOOTH_", "DISABLED");
                    returnHome();
                }
            });
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Log.d("BLUETOOTH_", "DISCOVER_STARTED");
                startSearchAnimation();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Log.d("BLUETOOTH_", "DISCOVER_FINISHED");
                //termino l'animazione della ricerca quando essa termina e non ho trovato la box
                if (!boxFound) stopSearchAnimation();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Log.d("BLUETOOTH_", "Address="+device.getAddress());
                if(device.getName()!=null){
                    Log.d("BLUETOOTH_","Name="+device.getName());
                    if(device.getName().equals(getResources().getString(R.string.bluetoothBoxName))){
                        startConnection(device);
                    }
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        endBluetoothConnection();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    ImageView imageAnimationButton;
    static Button buttonOpenBox;


    public HealthBoxFragment() {
        // Required empty public constructor
    }


    public static HealthBoxFragment newInstance(String param1, String param2) {
        HealthBoxFragment fragment = new HealthBoxFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_health_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //-------------RIFERIMENTI---------------
        buttonOpenBox = view.findViewById(R.id.buttonOpenBox);
        imageAnimationButton = view.findViewById(R.id.imgAnimationButton);
    }



    @Override
    public void onResume() {
        super.onResume();

        //verifyBluetoothConnection();
        Toolbar toolbar = getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();

        //richiedo il permesso di acceso al bluetooth e la sua attivazione
        requestBluetoothPermissionAndEnabling();
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        requireActivity().registerReceiver(receiver, filter);

        if (!Utility.isConnectedToInternet(getActivity())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialogStyleCritical);

            // Set the dialog title and message
            builder.setTitle(getString(R.string.pendingMisurationRequestTitle))
                    .setMessage(getString(R.string.pendingMisurationRequest))
                    .setNegativeButton(getString(R.string.negativeButtonPendingMisurationRequest), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    })
                    .setPositiveButton(getString(R.string.positiveButtonPendingMisurationRequest), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pendingMisuration = true;
                        }
                    });



            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            getActivity().onBackPressed();
                        }
                    }
            );

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        buttonOpenBox.setOnClickListener(v -> searchPairedDevices());
    }

    private void startPulse() {
        runnable.run();
    }

    private void stopPulse() {
        handlerAnimation.removeCallbacks(runnable);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            imageAnimationButton.animate()
                    .scaleX(4f)
                    .scaleY(4f)
                    .alpha(0f)
                    .setDuration(1000)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imageAnimationButton.setScaleX(1f);
                            imageAnimationButton.setScaleY(1f);
                            imageAnimationButton.setAlpha(1f);
                        }
                    });

            imageAnimationButton.animate()
                    .scaleX(4f)
                    .scaleY(4f)
                    .alpha(0f)
                    .setDuration(700)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imageAnimationButton.setScaleX(1f);
                            imageAnimationButton.setScaleY(1f);
                            imageAnimationButton.setAlpha(1f);
                        }
                    });

            handlerAnimation.postDelayed(this, 1500);
        }
    };

    /**
     * metodo che verifica se abbiamo il permeso di avere l'accesso al bluetooth e se non lo abbiamo lo richiede e richiama la funzione checkBluetoothEnabled
     */
    private void requestBluetoothPermissionAndEnabling() {
        //se il bluetooth non è supportato torno alla home
        if (btAdapter == null) {
            returnHome();
        }

        //verifico se ho il permesso per il bluetooth e faccio la richiesta se non lo ho
        //se ho il permesso eseguo la funzione checkBluetoothEnabled mentre se non ho il permesso eseguo returnHome
        //verifico se posseggo il permesso per la connessione bluetooth
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        ) {
            //se ho il permesso verifico se il bluetooth è attivo
            checkBluetoothEnabled();
            Log.d("PERMISSION", "GRANTED");
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
                //mostro l'informativa
                showExplanation(R.string.bluetoothPermissionTitle, R.string.bluetoothInformativa, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH});
                Log.d("PERMISSION", "INFORMATIVA");
            } else {
                //se non devo mostrare l'informativa, richiedo il permesso all'utente
                requestPermissionLauncher.launch(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH});
            }
        }
    }

    /**
     * metodo che mostra l'informativa per la richiesta del permesso di accesso al bluetooth
     * @param title
     * @param message
     * @param permission
     */
    private void showExplanation(int title, int message, final String[] permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> requestPermissionLauncher.launch(permission))
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> returnHome());
        builder.create().show();
    }

    /**
     * metodo che verifica se il bluetooth è attivo e se non lo è invia una richiesta di attivazione all'utente
     */
    private void checkBluetoothEnabled() {
        //verifico che ho il bluetooth attivo e se non lo ho richiedo l'attivazione
        //se lo è avvio il procesdo ri ricerca del dispositivo
        if (!btAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bluetoothEnableResult.launch(enableBT);
        } else {
            Log.d("BLUETOOTH", "ENABLED");
        }
    }

    /**
     * Metodo che prende tutti i dispositivi bluetooth già precedentemente associati
     */
    private void searchPairedDevices() {
        //parte relativa all'animazione
        if (statusAnimation) {
            //termino la scoperta dei dispositivi
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                returnHome();
            }
            btAdapter.cancelDiscovery();
        } else {
            if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                /*Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                for(BluetoothDevice device:pairedDevices){
                    if(device.getName()=="AsilApp Box"){
                        BluetoothSocket tmp=null;
                        try {
                            // Get a BluetoothSocket to connect with the given BluetoothDevice.
                            // MY_UUID is the app's UUID string, also used in the server code.
                            tmp = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                        } catch (IOException e) {
                            Log.e("BLUETOOTH", "Socket's create() method failed", e);
                        }
                        btSocket=tmp;
                    }
                }*/
                // Register for broadcasts when a device is discovered.
                btAdapter.startDiscovery();
            }
        }
    }

    /**
     * Metodo richiamato per effettuare una richiesta di connessione con la box che fungerà da server in questo scenario
     * implementiamo la connessione all'interno di un Thread a parte in quanto il metodo connect è bloccante e non necessitiamo di un
     * Service dato che la connessione è un processo che va fatto solo quanto l'applicazione è in focus
     */
    private void startConnection(BluetoothDevice device){
        boxFound=true;
        btConnThread = null;
        buttonOpenBox.setText(getString(R.string.bluetoothConnectionStarted));
        try{
            btConnThread=new BluetoothConnectionThread(device, getActivity(), this);
            btConnThread.start();
        }catch (IOException e){
            Log.d("BLUETOOTH_", "Socket's create() method failed", e);
        }
    }

    /**
     * metodo chiamato dal thread della connessione bluetooth per mandargli un risultato di ritorno in merito alla computazione effettuata
     * ritorna l'esito della connessione e della miusrazione effettuata dall'utente
     * @param connectionEsito
     */
    public void sendConnectionResultToFragment(String connectionEsito){
        //se la connessione è andata a buon fine mostro che l'app è in attesa delle misurazioni
        if(connectionEsito=="OK"){
            buttonOpenBox.setText(getActivity().getResources().getString(R.string.bluetoothDataWaiting));
        } else if (connectionEsito.equals(getResources().getString(R.string.misurationSuccessfull)) || connectionEsito.equals(getResources().getString(R.string.misurationStoredSuccessfully))) {
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), connectionEsito, Toast.LENGTH_SHORT).show());
        } else if (connectionEsito.equals("CLOSE")) {  //se il messaggio è CLOSE allora chiudo la connessione
            endBluetoothConnection();
        } else{
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), connectionEsito, Toast.LENGTH_SHORT).show());
            stopSearchAnimation();
        }
    }

    /**
     * metodo che ritorna alla schermata home
     */
    private void returnHome() {
        ((HomeActivity) getActivity()).changeScreen(getResources().getString(R.string.homeMenuScreen));
        Toast.makeText(getActivity(), getResources().getString(R.string.bluetoothGivePermissionMessage),
                Toast.LENGTH_SHORT).show();
    }

    private void startSearchAnimation(){
        startPulse();
        buttonOpenBox.setText(getString(R.string.searching));
        //mostro il toast sull'avvio della ricerca'
        Toast.makeText(getActivity(), getResources().getString(R.string.bluetoothSearchStarted),
                Toast.LENGTH_SHORT).show();
        statusAnimation = !statusAnimation;
    }

    private void stopSearchAnimation(){
        stopPulse();
        buttonOpenBox.setText(getString(R.string.open));
        //mostro il toast di fine della ricerca
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getResources().getString(R.string.bluetoothSearchFinished), Toast.LENGTH_SHORT).show());
        statusAnimation = !statusAnimation;
    }

    private void endBluetoothConnection(){
        //se la connessione con la box è terminata elimino la BluetoothSocket
        Log.d("BLUETOOTH_", "CONNECTION_ENDED");
        btConnThread.cancel();
        //e avvio l'animazione di termina della ricerca
        stopPulse();
        buttonOpenBox.setText(getResources().getString(R.string.open));
        //mostro il toast sul termine della connessione
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getResources().getString(R.string.bluetoothConnectionEnded), Toast.LENGTH_SHORT).show());
        statusAnimation = !statusAnimation;
        boxFound=false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //chiudo la connessione e la ricerca
        btAdapter.cancelDiscovery();
        if(btConnThread!=null)btConnThread.cancel();
        boxFound=false;
        btConnThread=null;
        requireActivity().unregisterReceiver(receiver);
    }
}