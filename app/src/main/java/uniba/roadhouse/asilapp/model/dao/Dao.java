package uniba.roadhouse.asilapp.model.dao;

import android.content.Context;
import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import uniba.roadhouse.asilapp.R;

public class Dao {
    private FirebaseFirestore db;
    private String email="gliasphaltatori@gamil.com";

    public Dao() {
        db= FirebaseFirestore.getInstance();
    }

    public void tryWriteData(){
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

    public List<String> getNomiCittaResidenze(){
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
    }

    public List<String> getNomiResidenze(String citta){
        List<String> nomiResidenze=new ArrayList<>();
        Task<QuerySnapshot> query=db.collection("residenze").whereEqualTo("citta",citta).get();

        while (!query.isComplete()) {
            //attenendo che la funzione asincrona chaimata termini la sua computazione
        }

        //qui la query è completa e ciclo per i risultati ottenuti
        for (QueryDocumentSnapshot document : query.getResult()) {
            nomiResidenze.add(document.getString("nome"));
        }
        return nomiResidenze;
    }

    public String registerUser(String username, String password, String nome, String cognome, String cittadinanza, String sesso, String paese, String residenza, String tipoUtente, Context context){
        //verifico se esiste un utente con lo username dell'utente che si vuole registrare
        Task<QuerySnapshot> query=db.collection("users").whereEqualTo("username",username).get();
        while (!query.isComplete()) {
            //attenendo che la funzione asincrona chaimata termini la sua computazione
        }
        //quando la query è completata vedo se esiste un utente con lo username scelto
        if(query.getResult().size()>0){
            return context.getString(R.string.userAlreadyExists);
        }
        //verifico che la password rispetta i criteri previsti
        //password deve avere almeno 8 caratteri, almeno una lettera maiuscola, un carattere speciale e un numero
        if(!Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",password)){
            return context.getString(R.string.passwordDoNotMatchRegEx);
        }
        //se va tutto bene faccio l'hash della password
        //faccio l'hash della password
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        byte[] hashedPassword;
        random.nextBytes(salt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        //se va tutto bene, creo la mappa che rappresenta i dati dell'utente
        Map<String,String> user=new HashMap<>();
        user.put("username",username);
        user.put("password",hashedPassword.toString());
        user.put("sesso",sesso);
        user.put("nome",nome);
        user.put("cognome",cognome);
        if(paese!=null){user.put("paeseDiProvenienza",paese);}
        if(cittadinanza!=null){user.put("cittadinanza",cittadinanza);}
        user.put("nomeResidenza",residenza);
        user.put("tipoUtente",tipoUtente);

        //aggiungo l'utente al db
        Task<DocumentReference> addToDb=db.collection("users").add(user);
        while (!addToDb.isComplete()) {
            //attenendo che la funzione asincrona chaimata termini la sua computazione
        }
        if(!addToDb.isSuccessful()){
            return context.getString(R.string.insertUserFailed);
        }
        //se l'inserimento è avvenuto con successo ritono il messaggio
        return context.getString(R.string.registrationComplete);
    }

    public String loginUser(String username, String password, Context context){

        //verifico se esiste un utente con lo username dell'utente che si vuole loggare
        Task<QuerySnapshot> query=db.collection("users").whereEqualTo("username",username).get();

        while (!query.isComplete()) {
            //attenendo che la funzione asincrona chaimata termini la sua computazione
        }

        //quando la query è completata vedo se non esiste un utente con il nome scelto
        if(query.getResult().size()==0){
            return context.getString(R.string.noUserExists);
        }

        //se l'utente esiste, ne prendo la password
        String passwHash = "";

        for (QueryDocumentSnapshot document : query.getResult()) {
            passwHash=document.getString("password");
        }

        //verifico che l'ash della password immessa dall'utente è uguale a quella del db
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        byte[] hashedPassword;
        random.nextBytes(salt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        //se l'hash della password immessa dall'utente èdiverso da quella nel db, allora il login non va a buon fine
        if(hashedPassword.toString()!=passwHash){
            return context.getString(R.string.wrongPassword);
        }

        //se passw e usename sono corretti, genero il token JWT da memorizzare localmente per l'autenticazione
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withIssuer("auth0")
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
        }

        return context.getString(R.string.loginCompleted);
    }
}
