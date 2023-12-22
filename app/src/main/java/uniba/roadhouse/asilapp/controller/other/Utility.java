package uniba.roadhouse.asilapp.controller.other;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.ParcelUuid;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.CompoundButtonCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogStyle);

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
        int width = bm.getWidth();
        int height = bm.getHeight();
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


    /**
     * Rende una TextView scrollabile
     * @param view, textview da rendere scrollabile
     */
    public static void enableScroll(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setMovementMethod(new ScrollingMovementMethod());
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }


    /**
     * Colora le checkbox con il colorAccent
     * @param checkBoxes, lista di checkbox da colorare
     * @param context, contesto
     */
    public static void colorAllCheckbox(List<CheckBox> checkBoxes, Context context)
    {
        for(CheckBox checkBox: checkBoxes)
        {
            CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
        }
    }


    /**
     * Converte un TipiMisurazioneEnum in una stringa che può essere utilizzata per i titoli dei vari widget
     * @param tipo, tipoMisurazioneEnum da convertire
     * @param context, contesto attuale
     * @return string, tipoMisurazioneEnum convertito in stringa
     */
    public static String convertTipoMisurazioneEnumToString(TipoMisurazioneEnum tipo, Context context)
    {
        switch(tipo)
        {
            case TEMPERATURA:
                return context.getString(R.string.temperatureHealthHistory);
            case PRESSIONESANGUIGNA:
                return context.getString(R.string.bloodPressureHealthHistory);
            case PESO:
                return context.getString(R.string.weightHealthHistory);
            case BATTITOCARDIACO:
                return context.getString(R.string.bpmHealthHistory);
            case TREMOLIO:
                return context.getString(R.string.tremblingHealthHistory);
            case GLUCOSIO:
                return context.getString(R.string.glucoseHealthHistory);
        }
        return null;
    }


    /**
     * Verifica se una password rispetta i criteri di sicurezza.
     * @param password, password da scansionare
     * @return true se la password rispetta i criteri di sicurezza, false altrimenti.
     */
    public static Boolean checkRegexPassword(String password)
    {
        return Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", password);
    }


    // Method to clear the time portion of a Calendar object
    public static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }


    /**
     * Abilita l'animazione al click di una View.
     * @param context, contesto
     * @param view, view su cui abilitare l'animazione
     */
    public static void activeAnimationOnClick(Context context, View view)
    {
        int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();
        view.setClickable(true);
        view.setForeground(drawableFromTheme);
    }

    public static UUID getAppUUID(Context context){
        ParcelUuid pd=new ParcelUuid(UUID.nameUUIDFromBytes(context.getResources().getString(R.string.bluetoothBoxName).getBytes()));
        return pd.getUuid();
    }

}

