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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.other.TipoMisurazioneEnum;

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
            Set<String> set = new HashSet<>(nomiCitta);
            nomiCitta.clear();
            nomiCitta.addAll(set);
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

    public static CompletableFuture<String> getCittaResidenza(String nomeResidenza, Context context){
        return CompletableFuture.supplyAsync(()->{
            String nomeCitta="";
            Task<QuerySnapshot> query=db.collection("residenze").whereEqualTo("nome",nomeResidenza).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.cityError);
            }

            //qui la query è completa e ciclo per i risultati ottenuti
            for (QueryDocumentSnapshot document : query.getResult()) {
                nomeCitta=document.getString("citta");
                break;
            }

            return nomeCitta;
        });
    }

    public static CompletableFuture<String> registerUser(String username, String password, String nome, String cognome, String cittadinanza, String sesso, String paese, String residenza, String tipoUtente, Context context){
        return CompletableFuture.supplyAsync(()->{
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
                return context.getString(R.string.qrGenerateError);
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

    public static CompletableFuture<Map<String,String>> loginUser(String username, String password, Context context){
        return CompletableFuture.supplyAsync(()-> {
            //verifico se esiste un utente con lo username dell'utente che si vuole loggare
            Task<QuerySnapshot> query = db.collection("users").whereEqualTo("username", username).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            //quando la query è completata vedo se non esiste un utente con il nome scelto
            if (query.getResult().size() == 0) {
                return new HashMap<String,String>() {{
                    put("esito", context.getString(R.string.noUserExists));
                }};
            }

            //se l'utente esiste, ne prendo la password
            String passwHash = "";
            String nome="";

            for (QueryDocumentSnapshot document : query.getResult()) {
                passwHash = document.getString("password");
                nome=document.getString("nome");
            }

            //verifico che l'ash della password immessa dall'utente è uguale a quella del db
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean passwordIsValid = bCryptPasswordEncoder.matches(password, passwHash);

            //se l'hash della password immessa dall'utente èdiverso da quella nel db, allora il login non va a buon fine
            if (!passwordIsValid) {
                return new HashMap<String,String>() {{
                    put("esito", context.getString(R.string.wrongPassword));
                }};
            }

            //se passw e usename sono corretti, genero il token JWT da memorizzare localmente per l'autenticazione
            try {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                String token = JWT.create()
                        .withSubject(username)
                        .withExpiresAt(DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY).parse("01/01/25"))
                        .withClaim("nome",nome)
                        .withClaim("tipo","UTENTE")
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

            String finalNome = nome;
            return new HashMap<String,String>() {{
                put("esito", context.getString(R.string.loginCompleted));
                put("nome", finalNome);
            }};
        });
    }

    /**
     * Questo metodo verifica se un utente ha gia effettuato il login, verificando l'autenticità del token JWT memorizzato nelle shared preferences
     *
     * @param context contesto attuale (this)
     * @return ritorna una Map con valore "" per la key "username" se l'utente non è loggato (token non valido o non trovato) o come valore lo username dell'utente se esso è loggato.
     * inoltre vi è la chiave "nome" per il nome dell'utente
     */
    public static Map<String,String> checkIsLogged(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("loginTokenJWT", context.MODE_PRIVATE);
        String token = sharedPref.getString("token","notLogged");

        //verifico se il token esiste localmente
        if(token=="notLogged"){
            return new HashMap<String,String>() {{
                put("username", "");
            }};
        }

        //verifico che il token sia valido
        DecodedJWT decodedJWT;
        String username;
        String nome="";
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();

            decodedJWT = verifier.verify(token);

            //se la verifica è andata a buon fine, cioè se non sono andato nel catch, prendo lo username dal token jwt
            username=decodedJWT.getSubject();
            nome=decodedJWT.getClaim("nome").asString();
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            username="";
        }

        String finalUsername = username;
        String finalNome = nome;
        return new HashMap<String,String>() {{
            put("username", finalUsername);
            put("nome", finalNome);
        }};
    }

    /**
     * Metodo che dato in input lo username dell'utente, ne prende dal db i dati
     * @param username username dell'utente da prelevare dal db
     * @param context contesto attuale, cioè this
     * @return ritorna una Map<String,String> che conterrà le key: nome, cognome, sesso, cittadinanza, paeseDiProvenienza, tipoUtente, nomeResidenza. Se l'utente non esiste, ritorna null
     */
    public static CompletableFuture<Map<String,Object>> getUserData(String username,Context context){
        return CompletableFuture.supplyAsync(()->{
            Map<String,Object> userData = new HashMap<>();
            Task<QuerySnapshot> query = db.collection("users").whereEqualTo("username", username).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            //quando la query è completata vedo se esiste un utente con lo username espresso
            if (query.getResult().size()==0) {
                return null;
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
                userData.put("dottore",document.getString("dottore"));
                String qr=document.getString("qrCode");
                //memorizzo il bitmap del qrcode nella mappa da ritornare
                userData.put("qrCode",Utility.StringToBitMap(qr));
                break;
            }

            return userData;
        });
    }

    public static CompletableFuture<Bitmap> getQrCodeUser(String username, Context context){
        return CompletableFuture.supplyAsync(()->{
            String qr="";

            //prendo il qr code dall'utente nel db
            Task<QuerySnapshot> query = db.collection("users").whereEqualTo("unsername", username).get();
            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!query.isSuccessful()) {
                return null;
            }

            //se la query è andata a buon fine, prendo il qrCode
            for (QueryDocumentSnapshot document : query.getResult()) {
                qr=document.getString("qrCode");
            }

            Bitmap bm=Utility.StringToBitMap(qr);

            return bm;
        });
    }

    /**
     * Metodo per la memorizzazione dele misurazioni effettuate sull'utente
     * @param mis
     * @param context
     * @return ritorna una stringa che indica se la computazione è adata a buon fine o meno
     */

    public static CompletableFuture<String> storeMisuration(Misurazione mis, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo l'ultima misurazione effettuata
            Task<QuerySnapshot> query = db.collection("misurazioni").orderBy("dataEora", Query.Direction.DESCENDING).limit(1).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.insertMisurationFailed);
            }

            //prendo l'id dell'ultima misurazione effettuata
            Integer id=-1;
            for(QueryDocumentSnapshot document:query.getResult()){
                id=Integer.valueOf(document.getId());
                break;
            }

            if(id==-1){
                return context.getString(R.string.insertMisurationFailed);
            }

            Map<String, Object> misuration = new HashMap<>();
            misuration.put("username",mis.getUsername());
            misuration.put("dataEora",mis.getData());
            misuration.put("valutazione",mis.getValutazione());
            misuration.put("notamedico",mis.getNotaMedico());
            misuration.put("valore",mis.getValore());
            misuration.put("tipo",mis.getTipo().toString());
            misuration.put("valoreMax",(mis.getValoreMax()==null)?null:mis.getValoreMax());
            misuration.put("valoreMin",(mis.getValoreMin()==null)?null:mis.getValoreMin());

            //memorizzo la misurazione con id incermentato di 1 rispetto all'ultima misurazione effettuata
            Task addToDb = db.collection("misurazioni").document(String.valueOf(id+1)).set(misuration);
            while (!addToDb.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!addToDb.isSuccessful()) {
                return context.getString(R.string.insertMisurationFailed);
            }

            return context.getString(R.string.misurationStoredSuccessfully);
        });
    }

    /**
     * Metodo per la modifica della valutazione di una misurazione dato il suo id
     * @param id
     * @param valutazione
     * @param context
     * @return
     */
    public static CompletableFuture<String> editMisurationValutazione(Integer id, String valutazione, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo l'ultima misurazione effettuata
            Task query = db.collection("patologie").document(id.toString()).update("valutazione",valutazione);

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.misurationEditFailed);
            }

            return context.getString(R.string.misurationEditSuccessfull);
        });
    }

    /**
     * Metodo per la modifica della nota del medico di una misurazione dato il suo id
     * @param id
     * @param nota
     * @param context
     * @return
     */
    public static CompletableFuture<String> editMisurationNota(Integer id, String nota, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo l'ultima misurazione effettuata
            Task query = db.collection("patologie").document(id.toString()).update("notamedico",nota);

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.misurationEditFailed);
            }

            return context.getString(R.string.misurationEditSuccessfull);
        });
    }

    /**
     * Metodoper prendere una misurazione dato un id
     *
     * @param id
     * @param context
     * @return ritorna una Map con 2 chiavi: "esito" per l'esito della computazione e "misurazione" per ottenere l'oggetto misurazione
     */
    public static CompletableFuture<Map<String,?>> getMisuration(Integer id, Context context){
        return CompletableFuture.supplyAsync(()->{

            Task<DocumentSnapshot> query = db.collection("misurazioni").document(String.valueOf(id)).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return new HashMap<String,String>() {{
                    put("esito", context.getString(R.string.misurationGetFailed));
                }};
            }

            Misurazione misuration = new Misurazione(
                    query.getResult().getString("username"),
                    query.getResult().getString("valutazione"),
                    query.getResult().getDouble("valore"),
                    query.getResult().getDouble("valoreMax"),
                    query.getResult().getDouble("valoreMin"),
                    query.getResult().getTimestamp("dataEora"),
                    TipoMisurazioneEnum.valueOf(query.getResult().getString("tipo")),
                    query.getResult().getString("notamedico"),
                    Integer.valueOf(query.getResult().getId())
            );

            return new HashMap<String,Object>(){{
                put("esito",context.getString(R.string.misurationGetSuccessfully));
                put("misurazione",misuration);
            }};
        });
    }

    /**
     * Metodo per prendere tutte le rpecendenti misurazioni di un tipo di un certo utente
     * @param username
     * @param tipo
     * @param context
     * @return ritorna uma map con 2 chiavi: "esito" per l'esito della computaizione e "misurazioni" una List<Misurazoni> che contiene tutte le misuraioni in questione
     */
    public static CompletableFuture<Map<String,Object>> getAllPastMisurationByUsername(String username, String tipo, Context context){
        return CompletableFuture.supplyAsync(()->{
            List<Map<String,Object>> misurations = new ArrayList<>();
            Task<QuerySnapshot> query = db.collection("misurazioni").whereEqualTo("username",username).whereEqualTo("tipo",tipo).orderBy("dataEora", Query.Direction.DESCENDING).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return new HashMap<String,Object>(){{
                    put("esito",context.getString(R.string.misurationGetFailed));
                }};
            }

            Map<String,Object> result=new HashMap<String,Object>();
            List<Misurazione> misurazioni=new ArrayList<>();

            for(QueryDocumentSnapshot document:query.getResult()){
                misurazioni.add(new Misurazione(
                        document.getString("username"),
                        document.getString("valutazione"),
                        document.getDouble("valore"),
                        document.getDouble("valoreMax"),
                        document.getDouble("valoreMin"),
                        document.getTimestamp("dataEora"),
                        TipoMisurazioneEnum.valueOf(document.getString("tipo")),
                        document.getString("nota"),
                        Integer.valueOf(document.getId())
                    ));
            }

            result.put("esito",context.getString(R.string.misurationGetSuccessfully));
            result.put("misurazioni",misurazioni);

            return result;
        });
    }

    /**
     * Metodo per prendere tutte le ultime misurazioni effettuate per un utente di ognitipo di misurazione
     * @param username
     * @param context
     * @return ritorna una mappa con la chiave "esito" per conoscere l'esito della computazione e una chiave per ogni tipo di misurazione che
     * contiene l'ultima misurazione effettuata; se un tipo di misurazione non è stata fatta mai, quella chiave non comparità nella mappa
     */
    public static CompletableFuture<Map<String,?>> getAllLastMisurationsUsername(String username, Context context) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String,Object> misurations = new HashMap<>();
            List<String> typesMisuration=new ArrayList<String>(){{
               add(TipoMisurazioneEnum.TEMPERATURA.toString());add(TipoMisurazioneEnum.PRESSIONESANGUIGNA.toString());
               add(TipoMisurazioneEnum.PESO.toString());add(TipoMisurazioneEnum.BATTITOCARDIACO.toString());
               add(TipoMisurazioneEnum.GLUCOSIO.toString());add(TipoMisurazioneEnum.TREMOLIO.toString());
            }};

            for(String tipo:typesMisuration){
                Task<QuerySnapshot> query = db.collection("misurazioni").whereEqualTo("username",username).whereEqualTo("tipo",tipo).orderBy("dataEora", Query.Direction.DESCENDING).limit(1).get();

                while (!query.isComplete()) {
                    //attenendo che la funzione asincrona chaimata termini la sua computazione
                }

                if(!query.isSuccessful()){
                    return new HashMap<String,Object>(){{
                        put("esito",query.getException().getMessage());
                    }};
                }


                //se l'utente non ha mai fatto nessuna misurazione di queso tipo, allora passo al prossimo tipo
                if(query.getResult().size()==0){
                    continue;
                }


                //prendo la misurazioneeffettuata e la aggiungo alla mappa
                for(QueryDocumentSnapshot document:query.getResult()){
                    Misurazione mis=new Misurazione(
                            document.getString("username"),
                            document.getString("valutazione"),
                            document.getDouble("valore"),
                            document.getDouble("valoreMax"),
                            document.getDouble("valoreMin"),
                            document.getTimestamp("dataEora"),
                            TipoMisurazioneEnum.valueOf(document.getString("tipo")),
                            document.getString("nota"),
                            Integer.valueOf(document.getId())
                    );
                    misurations.put(tipo,mis);
                    break;
                }
            }

            misurations.put("esito",context.getString(R.string.misurationGetSuccessfully));

            return misurations;
        });
    }

    public static CompletableFuture<String> editResidenzaUtente(String username, String nuovaresidenza, Context context){
        return CompletableFuture.supplyAsync(() -> {
            Task<QuerySnapshot> query = db.collection("users").document(username).update((Map)new HashMap<String,String>(){{
                put("nomeResidenza",nuovaresidenza);
            }});

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.changeResidenceError);
            }

            return context.getString(R.string.changeResidenzaSuccessfully);
        });
    }

    public static CompletableFuture<String> editPasswordUtente(String username, String nuovaPassword, Context context){
        return CompletableFuture.supplyAsync(() -> {
            //faccio l'hash della password
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = bCryptPasswordEncoder.encode(nuovaPassword);
            Task<QuerySnapshot> query = db.collection("users").document(username).update((Map)new HashMap<String,String>(){{
                put("password",hashedPassword);
            }});

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.editPasswordFailed);
            }

            return context.getString(R.string.editPasswordSuccessfull);
        });
    }

    public static CompletableFuture<String> logOutUser(Context context){
        return CompletableFuture.supplyAsync(() -> {
            SharedPreferences preferences = context.getSharedPreferences("loginTokenJWT", context.MODE_PRIVATE);
            preferences.edit().remove("token").commit();
            return context.getString(R.string.logoutSuccessfull);
        });
    }

    /**
     * Metodo che premette di loggare un dottore nell'applicazione dato username e password. Ritorna una mappa con chiave "esito" che indica
     * l'esito della computazione
     * @param username
     * @param password
     * @param context
     * @return
     */

    public static CompletableFuture<Map<String,String>> loginDoctor(String username, String password, Context context){
        return CompletableFuture.supplyAsync(()-> {
            //verifico se esiste un utente con lo username dell'utente che si vuole loggare
            Task<QuerySnapshot> query = db.collection("dottori").whereEqualTo("username", username).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            //quando la query è completata vedo se non esiste un utente con il nome scelto
            if (query.getResult().size() == 0) {
                return new HashMap<String,String>() {{
                    put("esito", context.getString(R.string.noDoctorExists));
                }};
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
                return new HashMap<String,String>() {{
                    put("esito", context.getString(R.string.wrongPassword));
                }};
            }

            //se passw e usename sono corretti, genero il token JWT da memorizzare localmente per l'autenticazione
            try {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                String token = JWT.create()
                        .withSubject(username)
                        .withExpiresAt(DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY).parse("01/01/25"))
                        .withClaim("tipo","DOTTORE")
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

            return new HashMap<String,String>() {{
                put("esito", context.getString(R.string.loginCompleted));
            }};
        });
    }

    /**
     * Metodo che dato l'username di un dottore, ritorna un a lista contenente l'username di tutti i suoi pazienti
     * @param docUsername
     * @param context
     * @return
     */
    public static CompletableFuture<List<String>> getAllDoctorsPatients(String docUsername, Context context){
        return CompletableFuture.supplyAsync(()-> {
            //verifico se esiste un utente con lo username dell'utente che si vuole loggare
            Task<QuerySnapshot> query = db.collection("users").whereEqualTo("dottore", docUsername).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            //quando la query è completata vedo se non esiste nessun paziente del dottore ritorno una lista vuota
            if (query.getResult().size() == 0) {
                return new ArrayList<>();
            }else{
                List<String> patients=new ArrayList<>();
                for (QueryDocumentSnapshot document:query.getResult()){
                    patients.add(document.getString("username"));
                }
                return patients;
            }
        });
    }

    /**
     * Metodo per prendere tutte le patologie di un utente dato username. Ritorna una mappa conchiave "esito" che rappresenta l'esito della computazione
     * e chiave "patologie" che è la lista delle patologie prese
     * @param username
     * @param context
     * @return
     */
    public static CompletableFuture<Map<String,Object>> getAllPatologies(String username, Context context){
        return CompletableFuture.supplyAsync(()->{
            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("username",username).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return new HashMap<String,Object>(){{
                    put("esito",context.getString(R.string.patologiesGetFailed));
                }};
            }

            Map<String,Object> result=new HashMap<String,Object>();
            List<Patologia> patologie=new ArrayList<>();

            for(QueryDocumentSnapshot document:query.getResult()){
                patologie.add(new Patologia(
                        document.getString("username"),
                        document.getString("patologia"),
                        document.getString("priorita"),
                        document.getTimestamp("dataOraUltimaVisita"),
                        document.getString("notaMedico")
                ));
            }

            result.put("esito",context.getString(R.string.patologiesGetSuccessfull));
            result.put("patologie",patologie);

            return result;
        });
    }

    /**
     * Metodo per prenedere una patologia dell'utente dato username e tipo di patologia. Ritorna una mappa con chiave "patologia" con un oggetto
     * Patologia che rappresenta la patologia presa e con chiave "esito" che rappresenta l'esito della computazione
     * @param patologia
     * @param username
     * @param context
     * @return
     */
    public static CompletableFuture<Map<String,?>> getPatology(String patologia, String username, Context context){
        return CompletableFuture.supplyAsync(()->{

            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("patologia",patologia).whereEqualTo("username",username).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return new HashMap<String,String>() {{
                    put("esito", context.getString(R.string.patologiesGetFailed));
                }};
            }

            Patologia patology = null;
            for (QueryDocumentSnapshot document:query.getResult()){
                patology=new Patologia(
                        document.getString("username"),
                        document.getString("patologia"),
                        document.getString("priorita"),
                        document.getTimestamp("dataOraUltimaVisita"),
                        document.getString("notaMedico"));
                break;
            }

            Patologia finalPatology = patology;
            return new HashMap<String,Object>(){{
                put("esito",context.getString(R.string.misurationGetSuccessfully));
                put("patologia", finalPatology);
            }};
        });
    }

    /**
     * Metodo per la modifica e aggiunta di una patologia di un utente. Ha in input un oggetto patologia che conterrà i dati della patologia
     * da inserire compreso l'username dell'utente di riferimento.
     * @param patologia
     * @param context
     * @return
     */
    public static CompletableFuture<String> storePatology(Patologia patologia, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo l'ultima misurazione effettuata
            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("username",patologia.getUsername()).whereEqualTo("patologia",patologia.getPatologia()).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.insertMisurationFailed);
            }

            //creo la patologia
            Map<String, Object> patology = new HashMap<>();
            patology.put("username",patologia.getUsername());
            patology.put("dataOraUltimaVisita",patologia.getDataEora());
            patology.put("priorita",patologia.getPriorita());
            patology.put("notamedico",patologia.getNota());
            patology.put("patologia",patologia.getPatologia());

            //verifico se esiste la patologia gia nel db
            if(query.getResult().size()==0){
                //memorizzo la patologia nel db
                Task addToDb = db.collection("patologie").add(patology);
                while (!addToDb.isComplete()) {
                    //attenendo che la funzione asincrona chaimata termini la sua computazione
                }
                if (!addToDb.isSuccessful()) {
                    return context.getString(R.string.insertPatologyFailed);
                }
            }else{
                //modifico la patologia presente nel db
                Task addToDb = db.collection("patologie").document(query.getResult().getDocuments().get(0).getId()).set(patology);
                while (!addToDb.isComplete()) {
                    //attenendo che la funzione asincrona chaimata termini la sua computazione
                }
                if (!addToDb.isSuccessful()) {
                    return context.getString(R.string.insertPatologyFailed);
                }
            }

            return context.getString(R.string.insertPatologySuccessfull);
        });
    }

}
