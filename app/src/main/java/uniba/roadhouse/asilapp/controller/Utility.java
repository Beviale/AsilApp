package uniba.roadhouse.asilapp.controller;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class Utility
{
    /**
     * Mostra un AlertDialog all'utente.
     * @param context contesto dal quale fa partire l'AlertDialog.
     * @param title titolo dell'AlertDialog
     * @param message messaggio dell'AlertDialog
     */
    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the dialog title and message
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle positive button click
                        dialog.dismiss();

                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }




    /**
     * Verifica se il dispositivo è connesso o meno a una rete Internet
     * @return true: il dispositivo è connesso, false altrimenti.
     */
    public static boolean isConnectedToInternet(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
        }
        return false;
    }
}

