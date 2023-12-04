package uniba.roadhouse.asilapp.controller;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

import uniba.roadhouse.asilapp.R;

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


    /**
     * Scrive su una TextView un testo sottolineato.
     * @param textView TextView il cui testo deve essere sottolineato.
     * @param text testo da sottolineare.
     */
    public static void textViewUnderlineText(TextView textView, String text){
        SpannableString str=new SpannableString(text);
        str.setSpan(new UnderlineSpan(), 0, str.length(), 0);
        textView.setText(str);
    }

    public static Bitmap generateQrCodeBitmap(String data) throws WriterException {
        BitMatrix bm = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
        int width = 400;
        int height = 400;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bm.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}

