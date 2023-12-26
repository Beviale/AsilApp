package uniba.roadhouse.asilapp.model.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.Timestamp;
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
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.CategoriaSpesaEnum;
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

    /**
     * Metodo che permette di pendere i nomi delle città di tutte le residenze presenti sul db. Ritorna una lista di stringhe simboleggianti
     * proprio questi nomi
     * @return
     */
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

    /**
     * Metodo che prende i nomi di tutte le residenze nel db. Ritorna una lista di stringhe simpoleggiante proprio questi nomi
     * @param citta
     * @return
     */
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

    /**
     * Metodo che permette di prendere tutte la città di una residenza presente sul db. Riorna una stringa che rappresenta la città in considerazione o
     * l'esito della computazione in caso negativo (in questo caso la stringa ritornata sarà R.strings.cityError)
     * @param nomeResidenza
     * @param context
     * @return
     */
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

    /**
     * Metodo che permette di registrare un utente sul db (non dottore) dati tutti i suoi dati. Ritorna una stringa che indica l'esito della computazione
     * @param username
     * @param password
     * @param nome
     * @param cognome
     * @param cittadinanza
     * @param sesso
     * @param paese
     * @param residenza
     * @param tipoUtente
     * @param dataNascita
     * @param context
     * @return
     */
    public static CompletableFuture<String> registerUser(String username, String password, String nome, String cognome, String cittadinanza, String sesso, String paese, String residenza, String tipoUtente, String dataNascita, Context context){
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
            user.put("dataNascita",dataNascita);
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
            String tipoAsiloProtezione="";

            for (QueryDocumentSnapshot document : query.getResult()) {
                passwHash = document.getString("password");
                nome=document.getString("nome");
                tipoAsiloProtezione=document.getString("tipoUtente");
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
                        .withClaim("tipoAsiloProtezione",(tipoAsiloProtezione=="Richiedente asilo")?"asilo":"protezione")
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
     * inoltre vi è la chiave "nome" per il nome dell'utente e una chiave "tipo" che indica se l'utente è un "UTENTE" o un "DOTTORE"
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
        String tipo="";
        String tipoAsiloProtezione="";
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();

            decodedJWT = verifier.verify(token);

            //se la verifica è andata a buon fine, cioè se non sono andato nel catch, prendo lo username dal token jwt
            username=decodedJWT.getSubject();
            nome=decodedJWT.getClaim("nome").asString();
            tipo=decodedJWT.getClaim("tipo").asString();
            tipoAsiloProtezione=decodedJWT.getClaim("tipoAsiloProtezione").asString();
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            username="";
        }

        String finalUsername = username;
        String finalNome = nome;
        String finalTipo = tipo;
        String finalTipoAsiloProtezione = tipoAsiloProtezione;
        return new HashMap<String,String>() {{
            put("username", finalUsername);
            put("nome", finalNome);
            put("tipo", finalTipo);
            if(finalTipoAsiloProtezione !="") put("tipoAsiloProtezione", finalTipoAsiloProtezione);
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
                userData.put("dataNascita",document.getString("dataNascita"));
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
            Task query = db.collection("misurazioni").document(id.toString()).update("valutazione",valutazione);

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
            Task query = db.collection("misurazioni").document(id.toString()).update("notamedico",nota);

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

    /**
     * Metodo che pemette la modifica della residenza di un utente dato lo username e il nome della nuova residenza
     * @param username
     * @param nuovaresidenza
     * @param context
     * @return
     */
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

    /**
     * Metodo che permette la modifica della password dell'utente dato username e nuova password
     * @param username
     * @param nuovaPassword
     * @param context
     * @return
     */
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

    /**
     * Metodo che cancella lo shared preferences memorizzato nel dispositivo contenete il jwt al fine di effettuare il logout
     * di un utente, sia che esso sia dottore o meno
     * @param context
     * @return
     */
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
                        document.getString("data"),
                        document.getString("ora"),
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
    public static CompletableFuture<Map<String,?>> getPathology(String patologia, String username, Context context){
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
                        document.getString("data"),
                        document.getString("ora"),
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
            patology.put("data",patologia.getData());
            patology.put("ora",patologia.getOra());
            patology.put("priorita",patologia.getPriorita());
            patology.put("notaMedico",patologia.getNota());
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

    /**
     * Metodo per la modifica della priorità di una patologia dato lo suername dell'utente relativo e il nome della patologia.
     * ritorna una stringa che indica l'esito della computazione
     * @param username
     * @param patologia
     * @param priorita
     * @param context
     * @return
     */
    public static CompletableFuture<String> editPatologiaPriority(String username, String patologia, String priorita, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo la patologia dell'utente
            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("username",username).whereEqualTo("patologia",patologia).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }

            String id=null;

            for(QueryDocumentSnapshot document:query.getResult()){
                id=document.getId();
                break;
            }

            //modifico la priorità
            Task update = db.collection("patologie").document(id).update("priorita",priorita);

            while (!update.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!update.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }


            return context.getString(R.string.editPatologySuccessfull);
        });
    }

    /**
     * Metodo per la modifica della nota medico di una patologia dato lo suername dell'utente relativo e il nome della patologia.
     * ritorna una stringa che indica l'esito della computazione
     * @param username
     * @param patologia
     * @param nota
     * @param context
     * @return
     */
    public static CompletableFuture<String> editPatologiaNotaMedico(String username, String patologia, String nota, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo la patologia dell'utente
            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("username",username).whereEqualTo("patologia",patologia).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }

            String id=null;

            for(QueryDocumentSnapshot document:query.getResult()){
                id=document.getId();
                break;
            }

            //modifico la priorità
            Task update = db.collection("patologie").document(id).update("notaMedico",nota);

            while (!update.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!update.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }


            return context.getString(R.string.editPatologySuccessfull);
        });
    }

    /**
     * Metodo per la modifica dell'ora di una patologia dato lo suername dell'utente relativo e il nome della patologia.
     * ritorna una stringa che indica l'esito della computazione
     * @param username
     * @param patologia
     * @param ora
     * @param context
     * @return
     */
    public static CompletableFuture<String> editPatologyHour(String username, String patologia, String ora, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo la patologia dell'utente
            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("username",username).whereEqualTo("patologia",patologia).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }

            String id=null;

            for(QueryDocumentSnapshot document:query.getResult()){
                id=document.getId();
                break;
            }

            //modifico la priorità
            Task update = db.collection("patologie").document(id).update("ora",ora);

            while (!update.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!update.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }


            return context.getString(R.string.editPatologySuccessfull);
        });
    }

    /**
     * Metodo per la modifica della data di una patologia dato lo suername dell'utente relativo e il nome della patologia.
     * ritorna una stringa che indica l'esito della computazione
     * @param username
     * @param patologia
     * @param date
     * @param context
     * @return
     */
    public static CompletableFuture<String> editPatologyDate(String username, String patologia, String date, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo la patologia dell'utente
            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("username",username).whereEqualTo("patologia",patologia).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }

            String id=null;

            for(QueryDocumentSnapshot document:query.getResult()){
                id=document.getId();
                break;
            }

            //modifico la priorità
            Task update = db.collection("patologie").document(id).update("data",date);

            while (!update.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!update.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }


            return context.getString(R.string.editPatologySuccessfull);
        });
    }

    /**
     * Metodo per l'eliminazione della data di una patologia dato lo suername dell'utente relativo e il nome della patologia.
     * ritorna una stringa che indica l'esito della computazione
     * @param username
     * @param patologia
     * @param context
     * @return
     */
    public static CompletableFuture<String> deletePatology(String username, String patologia, Context context){
        return CompletableFuture.supplyAsync(()->{
            //prendo la patologia dell'utente
            Task<QuerySnapshot> query = db.collection("patologie").whereEqualTo("username",username).whereEqualTo("patologia",patologia).get();

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!query.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }

            String id=null;

            for(QueryDocumentSnapshot document:query.getResult()){
                id=document.getId();
                break;
            }

            //modifico la priorità
            Task update = db.collection("patologie").document(id).delete();

            while (!update.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }

            if(!update.isSuccessful()){
                return context.getString(R.string.editPatologyFailed);
            }


            return context.getString(R.string.editPatologySuccessfull);
        });
    }

    /**
     * Metodo che aggiunge un farmaco dato un oggetto Farmaco. Ritorna una stringa che indica l'esito della computazione
     * @param farmaco
     * @param context
     * @return
     */
    public static CompletableFuture<String> addFarmaco(Farmaco farmaco, Context context){
        return CompletableFuture.supplyAsync(()->{
            Map<String,Object> far=new HashMap<String,Object>(){{
               put("username",farmaco.getUsername());
               put("patologia",farmaco.getPatologia());
               put("nota",farmaco.getNota());
               put("nomeFarmaco",farmaco.getNome());
            }};

            //aggiungo l'utente al db
            Task addToDb = db.collection("farmaci").document(farmaco.getUsername()+farmaco.getPatologia()+farmaco.getNome()).set(far);
            while (!addToDb.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!addToDb.isSuccessful()) {
                return context.getString(R.string.insertFarmacoFailed);
            }

            //se l'inserimento è avvenuto con successo ritono il messaggio
            return context.getString(R.string.insertFarmacoSuccessfull);
        });
    }

    /**
     * Metodo che prende tutti i farmaci di una patologia di uno specifico utente. Ritorna una mappa con chiave "esito" indicante l'esito della
     * computaione e chiave "farmaci" che indica la lista di farmaci sottoforma di List<Farmaco>
     * @param username
     * @param nomePatologia
     * @param context
     * @return
     */
    public static CompletableFuture<Map<String,?>> getAllFarmaci(String username, String nomePatologia, Context context){
        return CompletableFuture.supplyAsync(()->{

            Task<QuerySnapshot> query = db.collection("farmaci").whereEqualTo("username",username).whereEqualTo("patologia",nomePatologia).get();
            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!query.isSuccessful()) {
                return new HashMap<String ,Object>(){{
                    put("esito",context.getString(R.string.getFarmaciFailed));
                }};
            }

            List<Farmaco> farmaci=new ArrayList<>();

            for (QueryDocumentSnapshot document:query.getResult()){
                farmaci.add(new Farmaco(document.getString("nomeFarmaco"),document.getString("nota"),username,nomePatologia));
            }

            return new HashMap<String,Object>(){{
                put("esito",context.getString(R.string.getFarmaciSuccessfull));
                put("farmaci",farmaci);
            }};
        });
    }

    /**
     * Metodo per l'eliminazione di un farmaco dato username,nome patologia e nome farmaco. Ritorna una stringa esito della computazione
     * @param username
     * @param nomePatologia
     * @param nomefarmaco
     * @param context
     * @return
     */
    public static CompletableFuture<String> deleteFarmaco(String username, String nomePatologia, String nomefarmaco, Context context){
        return CompletableFuture.supplyAsync(()->{

            Task addToDb = db.collection("farmaci").document(username+nomePatologia+nomefarmaco).delete();
            while (!addToDb.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!addToDb.isSuccessful()) {
                return context.getString(R.string.deleteFarmacoFailed);
            }

            //se l'inserimento è avvenuto con successo ritono il messaggio
            return context.getString(R.string.deleteFarmacoSuccessfull);
        });
    }

    /**
     * Metodo per prendere i 2 video relativi ad un certo tipo di utente dato in input il tipo
     * I tipi dell'utente accettabili sono "asilo" e "protezione"
     * Ritorna una Map con chiave "esito" per l'esito della computazione e "links" che è una List<String> con i link dei video
     * @param tipo
     * @param context
     * @return
     */
    public static CompletableFuture<Map<String,?>> getAllVideoByTipo(String tipo, Context context){
        return CompletableFuture.supplyAsync(()->{

            Task<QuerySnapshot> query = db.collection("video").whereEqualTo("tipo",tipo).get();
            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!query.isSuccessful()) {
                return new HashMap<String ,Object>(){{
                    put("esito",context.getString(R.string.getVideoFailed));
                }};
            }

            List<String> links=new ArrayList<>();

            for (QueryDocumentSnapshot document:query.getResult()){
                links.add(document.getString("link"));
            }

            return new HashMap<String,Object>(){{
                put("esito",context.getString(R.string.getVideoSuccessfull));
                put("links",links);
            }};
        });
    }

    /**
     * Metodo per prendere tutti gli articoli disponibili. Ritorna una Map con chiave "esito" che indica l'esito della computazione
     * e "articoles" che è un List<Articolo> che inidca la lista di articoli
     * @param context
     * @return
     */
    public static CompletableFuture<Map<String,?>> getAllArticles(Context context){
        return CompletableFuture.supplyAsync(()->{

            Task<QuerySnapshot> query = db.collection("articoli").get();
            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!query.isSuccessful()) {
                return new HashMap<String ,Object>(){{
                    put("esito",context.getString(R.string.getArticlesFailed));
                }};
            }

            List<Articolo> articles=new ArrayList<>();

            for (QueryDocumentSnapshot document:query.getResult()){
                Bitmap immagine=Utility.StringToBitMap(document.getString("immagine"));
                articles.add(new Articolo(
                        document.getString("titolo"),
                        document.getString("testo"),
                        document.getString("tipo"),
                        immagine
                ));
            }

            return new HashMap<String,Object>(){{
                put("esito",context.getString(R.string.getArticlesSuccessfull));
                put("articles",articles);
            }};
        });
    }

    /**
     * Metodo per prendere 2 articoli disponibili. Ritorna una Map con chiave "esito" che indica l'esito della computazione
     * e "articles" che è un List<Articolo> che inidca la lista di articoli
     * @param context
     * @return
     */
    public static CompletableFuture<Map<String,?>> getFirst2Articles(Context context){
        return CompletableFuture.supplyAsync(()->{

            Task<QuerySnapshot> query = db.collection("articoli").limit(2).get();
            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!query.isSuccessful()) {
                return new HashMap<String ,Object>(){{
                    put("esito",context.getString(R.string.getArticlesFailed));
                }};
            }

            List<Articolo> articles=new ArrayList<>();

            for (QueryDocumentSnapshot document:query.getResult()){
                Bitmap immagine=Utility.StringToBitMap(document.getString("immagine"));
                articles.add(new Articolo(
                        document.getString("titolo"),
                        document.getString("testo"),
                        document.getString("tipo"),
                        immagine
                ));
            }

            return new HashMap<String,Object>(){{
                put("esito",context.getString(R.string.getArticlesSuccessfull));
                put("articles",articles);
            }};
        });
    }

    /**
     * Metodo che premette di prendere tutte le spese di un utente negli ultimi <days> giorni con <days> il numero di giorni messo
     * in input al metodo. Ritorna una Map con chiave "esito" crappresentante
     * l'esito della computazione e "CIBO" che è una List<Spesa> indicante la liste delle spese sul cibo effettuate
     * l'esito della computazione e "FARMACI" che è una List<Spesa> indicante la liste delle spese sui farmaci effettuate
     * l'esito della computazione e "ALTRO" che è una List<Spesa> indicante la liste delle spese altro effettuate
     * @param username
     * @param context
     * @return
     */
    public static CompletableFuture<Map<String,?>> getAllSpese(String username, int days, Context context){
        return CompletableFuture.supplyAsync(()->{
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.DATE,-days);

            //prendole spese dell'utente specifico e di una categoria specifica negli ultimi <days> giorni
            Task<QuerySnapshot> query = db.collection("spese").whereEqualTo("username",username).whereGreaterThan("data", new Timestamp(cal.getTime())).get();
            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!query.isSuccessful()) {
                return new HashMap<String ,Object>(){{
                    put("esito",context.getString(R.string.getSpeseFailed));
                }};
            }

            List<Spesa> speseCibo=new ArrayList<>();
            List<Spesa> speseFarmaci=new ArrayList<>();
            List<Spesa> speseAltro=new ArrayList<>();

            for (QueryDocumentSnapshot document:query.getResult()){
                Spesa sp=new Spesa(
                        CategoriaSpesaEnum.valueOf(document.getString("categoria")),
                        document.getDouble("costo"),
                        document.getTimestamp("data"),
                        document.getString("username")
                );
                switch (CategoriaSpesaEnum.valueOf(document.getString("categoria"))){
                    case CIBO:
                        speseCibo.add(sp);
                        break;
                    case ALTRO:
                        speseAltro.add(sp);
                        break;
                    case FARMACI:
                        speseFarmaci.add(sp);
                }

            }


            return new HashMap<String,Object>(){{
                put("esito",context.getString(R.string.getSpeseSuccessfull));
                put(CategoriaSpesaEnum.CIBO.toString(),speseCibo);
                put(CategoriaSpesaEnum.FARMACI.toString(),speseFarmaci);
                put(CategoriaSpesaEnum.ALTRO.toString(),speseAltro);
            }};
        });
    }

    /**
     * Metodo per l memorizzazionedi una spesa. Ritorna una Stringa che rappresenta l'esito della computazione
     * @param spesa
     * @param context
     * @return
     */
    public static CompletableFuture<String> storeSpesa(Spesa spesa, Context context){
        return CompletableFuture.supplyAsync(()->{
            Map<String,Object> sp=new HashMap<String,Object>(){{
                put("categoria",spesa.getCategoria().toString());
                put("username",spesa.getUsername());
                put("costo",spesa.getCosto());
                put("data",spesa.getData());
            }};

            Task query = db.collection("spese").document(spesa.getData()+spesa.getUsername()).set(sp);

            while (!query.isComplete()) {
                //attenendo che la funzione asincrona chaimata termini la sua computazione
            }
            if (!query.isSuccessful()) {
                return context.getString(R.string.storeSpesaFailed);
            }

            return context.getString(R.string.storeSpesaSuccessfull);
        });
    }
}
