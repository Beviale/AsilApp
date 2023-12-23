package uniba.roadhouse.asilapp.controller.user.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.AccessUser;
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
     * Metodo che memorizza a misurazione effettuata nel db
     * @param misurazione
     * @param parametro
     */
    private void storeMisuration(Double misurazione, String parametro){
        Misurazione mis=new Misurazione(AccessUser.getUsername(),"NON VALUTATO",misurazione,null,null, Timestamp.now(), TipoMisurazioneEnum.valueOf(parametro),"");
        CompletableFuture<String> future = Dao.storeMisuration(mis,context);
        future.thenAccept(result -> {
            currentFragment.getActivity().runOnUiThread(() -> {
                //quando ho memorizzato la misurazione mando l'esito della computazione al client
                returnToClient(result);
            });});
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
