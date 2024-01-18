package uniba.roadhouse.asilapp.controller.user.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.EvaluationEnum;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Misurazione;

public class BluetoothConnectionThread extends Thread {
    private BluetoothSocket mmSocket=null;
    private final BluetoothDevice mmDevice;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final BluetoothAdapter btAdapter;
    private final Context context;
    private String computation="";
    private Fragment currentFragment;
    private boolean continueReading=true;
    public BluetoothConnectionThread(BluetoothDevice device, Context activityContext, Fragment currentFragmentHealth) throws IOException {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        context=activityContext;
        currentFragment=currentFragmentHealth;

        // Get a BluetoothSocket to connect with the given BluetoothDevice.
        // MY_UUID is the app's UUID string, also used in the server code.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        tmp = device.createRfcommSocketToServiceRecord(Utility.getAppUUID(context));

        //prendo la BluetoothSocket e lo stream di input e output
        mmSocket = tmp;
        inputStream = tmp.getInputStream();
        outputStream=tmp.getOutputStream();
    }

    public void run() {

        // Cancel discovery because it otherwise slows down the connection.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        btAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
                computation=context.getResources().getString(R.string.bluetoothConnectionFailed);
                Log.d("BLUETOOTH_", "Could not connect to the server socket");
            } catch (IOException closeException) {
                Log.d("BLUETOOTH_", "Could not close the client socket");
                computation=context.getResources().getString(R.string.bluetoothConnectionFailed);
            }finally {
                //invio il risultato al fragment
                returnToClient(computation);
            }
            return;
        }
        computation="OK";
        //invio il risultato al fragment
        returnToClient(computation);

        //attendo fino a quando il server non invia dei dati
        String messageFromServer; // bytes returned from read()
        while (continueReading){
            try {
                //prendo i dati inviati dal server sottoforma di mappa
                byte[] messageByte = new byte[inputStream.available()];
                DataInputStream in = new DataInputStream(inputStream);
                int numBytes = in.read(messageByte);
                if(numBytes>0){
                    messageFromServer=new String(messageByte, StandardCharsets.UTF_8);
                    Log.d("BLUETOOTH",messageFromServer);
                    //prendo i valori passati in input
                    String parametro=messageFromServer.split("@")[0];
                    String valore=messageFromServer.split("@")[1];

                    //se ho riscontrato un errore, lo mando al client
                    if(parametro.equals("ERROR") || parametro.equals("CLOSE")){
                        returnToClient(valore);
                        break;
                    }else{  //altrimenti mando al client la buona riuscita della misurazione e la metto nel db
                        //memprizzo la misurazione nel db
                        storeMisuration(Double.parseDouble(valore),parametro);
                    }
                }
            } catch (IOException e) {
                returnToClient(context.getResources().getString(R.string.misurationFailed));
            }
        }
    }

    private void returnToClient(String str){
        ((HealthBoxFragment)currentFragment).sendConnectionResultToFragment(str);
    }

    /**
     * Metodo che memorizza la misurazione effettuata nel db, oppure nelle Shared Preferences se non c'è connessione (Misurazione pendente)
     * @param misurazione, valore delle misurazione effettuata.
     * @param parametro, parametro della misurazione.
     */
    private void storeMisuration(Double misurazione, String parametro){
        // Se non c'è connessione, creo una misurazione pendente da memorizzare temporaneamente nelle Shared Preferences
        if (!Utility.isConnectedToInternet((Activity) context)) {
            SharedPreferences sharedPref = context.getSharedPreferences("misurazione", context.MODE_PRIVATE);
            String valutazione = sharedPref.getString("valutazione","NO");
            if(valutazione.equals("NO")) {
                // Se non c'è alcune misurazione pendente
                pendingMisuration(currentFragment.getString(R.string.pendingMisurationRequestTitle), currentFragment.getString(R.string.pendingMisurationRequest), misurazione, parametro);
            }
            else{
                // Se c'è già un'altra misurazione pendente, chiedo all'utente se vuole sovrascriverla
                pendingMisuration(currentFragment.getString(R.string.pendingMisurationRequestTitleDuplicati), currentFragment.getString(R.string.pendingMisurationRequestDuplicati), misurazione, parametro);
            }
        }
        else{
            //se c'è connessione memorizzo la misuazione nel db
            Misurazione mis=new Misurazione(UserLogin.getUsername(), EvaluationEnum.NON_VALUTATO.toString(),round(misurazione,2),null,null, Timestamp.now(), TipoMisurazioneEnum.valueOf(parametro),"");
            CompletableFuture<String> future = Dao.storeMisuration(mis,context);
            future.thenAccept(result -> {
                currentFragment.getActivity().runOnUiThread(() -> {
                    //quando ho memorizzato la misurazione mando l'esito della computazione al client
                    returnToClient(result);
                });});
        }
    }


    /**
     * Chiede all'utente se salvare una misurazione pendente,
     * @param title, titolo della finestra di dialogo.
     * @param message, messaggio della finestra di dialogo.
     * @param misurazione, valore della misurazione.
     * @param parametro, parametro della misurazione.
     */
    private void pendingMisuration(String title, String message, Double misurazione, String parametro)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogStyleCritical);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(context.getString(R.string.negativeButtonPendingMisurationRequest), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //se ha premuto il pulsante scarta non accade nulla e la misurazione viene scartata e non memorizzata
                    }
                })
                .setPositiveButton(context.getString(R.string.positiveButtonPendingMisurationRequest), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TextView bannerPendingMisuration = currentFragment.getActivity().findViewById(R.id.textBannerOnePendingMisuration);
                        bannerPendingMisuration.setVisibility(View.VISIBLE);
                        // memorizzo la misurazione localmente
                        SharedPreferences sharedPref = context.getSharedPreferences("misurazione", context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("valutazione", EvaluationEnum.NON_VALUTATO.toString());
                        editor.putFloat("valore",round(misurazione,2).floatValue());
                        editor.putString("valoreMax",null);
                        editor.putString("valoreMin",null);
                        editor.putLong("data",Timestamp.now().getSeconds());
                        editor.putString("tipo",parametro);
                        editor.putString("notaMedico","");
                        editor.commit();
                        returnToClient(context.getString(R.string.misurationStoredSuccessfully));
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ((Activity) context).onBackPressed();
                    }
                }
        );
    }






    public static Double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            continueReading=false;
            sendData("CLOSE");
            mmSocket.close();
        } catch (IOException e) {
            Log.e("BLUETOOTH_", "Could not close the client socket", e);
        }
    }

    private void sendData(String msg) throws IOException {
        outputStream.write(msg.getBytes());
        Log.d("BLUETOOTH","Inviato messaggio al client");
    }
}
