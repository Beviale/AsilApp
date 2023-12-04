package uniba.roadhouse.asilapp.model.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.DatabaseException;
import uniba.roadhouse.asilapp.controller.Utility;

public class Dao {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static String jwtSecret="roadhouseAsilApp";
    private String email="gliasphaltatori@gamil.com";

    public static void tryWriteData(){
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DB", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DB", "Error adding document", e);
                    }
                });
    }

    public static CompletableFuture<List<String>> getNomiCittaResidenze(){
        return CompletableFuture.supplyAsync(()->{
            List<String> nomiCitta=new ArrayList<>();
            Task<QuerySnapshot> query=db.collection("residenze").get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            //qui la query è completa e ciclo per i risultati ottenuti
            for (QueryDocumentSnapshot document : query.getResult()) {
                nomiCitta.add(document.getString("citta"));
            }
            return nomiCitta;

        });
    }

    public static CompletableFuture<List<String>> getNomiResidenze(String citta){
        return CompletableFuture.supplyAsync(()-> {
            List<String> nomiResidenze = new ArrayList<>();
            Task<QuerySnapshot> query = db.collection("residenze").whereEqualTo("citta", citta).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            //qui la query è completa e ciclo per i risultati ottenuti
            for (QueryDocumentSnapshot document : query.getResult()) {
                nomiResidenze.add(document.getString("nome"));
            }
            return nomiResidenze;
        });
    }

    public static CompletableFuture<String> registerUser(String username, String password, String nome, String cognome, String cittadinanza, String sesso, String paese, String residenza, String tipoUtente, Context context){
        return CompletableFuture.supplyAsync(()->{
            try{
                //so gia che username è disponibile e che lapassword rispeta i criteri

                //se va tutto bene faccio l'hash della password
                //faccio l'hash della password
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = bCryptPasswordEncoder.encode(password);

                //creo il qrCode associato all'username dell'utente
                String qrCode="";
                try {
                    Bitmap bm=Utility.generateQrCodeBitmap(username);
                    qrCode=Utility.BitMapToString(bm);
                } catch (WriterException e) {
                    throw new DatabaseException(context.getString(R.string.qrGenerateError));
                }

                //se va tutto bene, creo la mappa che rappresenta i dati dell'utente
                Map<String, String> user = new HashMap<>();
                user.put("username", username);
                user.put("password", hashedPassword);
                user.put("sesso", sesso);
                user.put("nome", nome);
                user.put("cognome", cognome);
                if (paese != null) {
                    user.put("paeseDiProvenienza", paese);
                }
                if (cittadinanza != null) {
                    user.put("cittadinanza", cittadinanza);
                }
                user.put("nomeResidenza", residenza);
                user.put("tipoUtente", tipoUtente);
                user.put("qrCode",qrCode);

                //aggiungo l'utente al db
                Task addToDb = db.collection("users").document(username).set(user);
                while (!addToDb.isComplete()) {
                    //attenendo che la funzione asincrona chaimata termini la sua computazione
                }
                if (!addToDb.isSuccessful()) {
                    return context.getString(R.string.insertUserFailed);
                }

                //se l'inserimento è avvenuto con successo ritono il messaggio
                return context.getString(R.string.registrationComplete);
            }catch (final DatabaseException d){
                throw new CompletionException(d.getMessage(),d);
            }
        });
    }

    public static CompletableFuture<Boolean> checkUsernameIsAvailable(String username,Context context){
        return CompletableFuture.supplyAsync(()-> {
            //verifico se esiste un utente con lo username dell'utente che si vuole registrare
            Task<QuerySnapshot> query=db.collection("users").whereEqualTo("username",username).get();
            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            //quando la query è completata vedo se esiste un utente con lo username scelto
            if (query.getResult().size() > 0) {
                return false;
            }
            return true;
        });
    }

    public static CompletableFuture<String> loginUser(String username, String password, Context context){
        return CompletableFuture.supplyAsync(()-> {
            //verifico se esiste un utente con lo username dell'utente che si vuole loggare
            Task<QuerySnapshot> query = db.collection("users").whereEqualTo("username", username).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            //quando la query è completata vedo se non esiste un utente con il nome scelto
            if (query.getResult().size() == 0) {
                return context.getString(R.string.noUserExists);
            }

            //se l'utente esiste, ne prendo la password
            String passwHash = "";

            for (QueryDocumentSnapshot document : query.getResult()) {
                passwHash = document.getString("password");
            }

            //verifico che l'ash della password immessa dall'utente è uguale a quella del db
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean passwordIsValid = bCryptPasswordEncoder.matches(password, passwHash);

            //se l'hash della password immessa dall'utente èdiverso da quella nel db, allora il login non va a buon fine
            if (!passwordIsValid) {
                return context.getString(R.string.wrongPassword);
            }

            //se passw e usename sono corretti, genero il token JWT da memorizzare localmente per l'autenticazione
            try {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                String token = JWT.create()
                        .withSubject(username)
                        .withExpiresAt(DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY).parse("01/01/25"))
                        .sign(algorithm);
                Log.d("DB", token);

                //memrizzo iltoken localmente
                SharedPreferences sharedPref = context.getSharedPreferences("loginTokenJWT", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("token", token);  // value is the string you want to save
                editor.commit();
            } catch (JWTCreationException exception) {
                // Invalid Signing configuration / Couldn't convert Claims.
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return context.getString(R.string.loginCompleted);
        });
    }

    public static boolean checkIsLogged(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("loginTokenJWT", context.MODE_PRIVATE);
        String token = sharedPref.getString("token","notLogged");

        //verifico se il token esiste localmente
        if(token=="notLogged"){
            return false;
        }

        //verifico che il token sia valido
        DecodedJWT decodedJWT;
        boolean isLogged=true;
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();

            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            isLogged=false;
        }

        return isLogged;
    }

    /**
     * Metodo che dato in input lo username dell'utente, ne prende dal db i dati
     * @param username username dell'utente da prelevare dal db
     * @param context contesto attuale, cioè this
     * @return ritorna una Map<String,String> che conterrà le key: nome, cognome, sesso, cittadinanza, paeseDiProvenienza, tipoUtente, nomeResidenza
     * @throws DatabaseException eccezione lanciata se si verificano die problemi. Ha come messaggio la stringa rappresentante il problema verificato
     */
    public static CompletableFuture<Map<String,Object>> getUserData(String username,Context context) throws DatabaseException{
        return CompletableFuture.supplyAsync(()->{
            try{
                Map<String,Object> userData = new HashMap<>();
                Task<QuerySnapshot> query = db.collection("users").whereEqualTo("unsername", username).get();

                while (!query.isComplete()) {
                    //attenendo che la funzione asincrona chaimata termini la sua computazione
                }

                //quando la query è completata vedo se esiste un utente con lo username espresso
                if (query.getResult().size() > 0) {
                    throw new DatabaseException(context.getString(R.string.noUserExists));
                }

                //se l'utente esiste, ne prendo tutti i dati
                //qui la query è completa e ciclo per i risultati ottenuti
                for (QueryDocumentSnapshot document : query.getResult()) {
                    userData.put("nome",document.getString("nome"));
                    userData.put("cognome",document.getString("cognome"));
                    userData.put("cittadinanza",document.getString("cittadinanza"));
                    userData.put("paeseDiProvenienza",document.getString("paeseDiProvenienza"));
                    userData.put("sesso",document.getString("sesso"));
                    userData.put("tipoUtente",document.getString("tipoUtente"));
                    userData.put("nomeResidenza",document.getString("nomeResidenza"));
                    String qr=document.getString("qrCode");
                    //memorizzo il bitmap del qrcode nella mappa da ritornare
                    userData.put("qrCode",Utility.StringToBitMap(qr));
                    break;
                }

                return userData;

            }catch (final DatabaseException d){
                throw new CompletionException(d.getMessage(),d);
            }
        });
    }

    public static CompletableFuture<Bitmap> getQrCodeUser(String username, Context context){
        return CompletableFuture.supplyAsync(()->{
            try{
                String qr="";

                //prendo il qr code dall'utente nel db
                Task<QuerySnapshot> query = db.collection("users").whereEqualTo("unsername", username).get();
                while (!query.isComplete()) {
                    //attenendo che la funzione asincrona chaimata termini la sua computazione
                }
                if (!query.isSuccessful()) {
                    throw new DatabaseException(context.getString(R.string.qrReadError));
                }

                //se la query è andata a buon fine, prendo il qrCode
                for (QueryDocumentSnapshot document : query.getResult()) {
                    qr=document.getString("qrCode");
                }

                Bitmap bm=Utility.StringToBitMap(qr);

                return bm;
            }catch (final DatabaseException d){
                throw new CompletionException(d.getMessage(),d);
            }finally {
                return null;
            }
        });
    }
}
