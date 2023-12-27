package uniba.roadhouse.asilapp.model.dao;

/**
 * Classe singleton che memorizza l'username, il nome e il tipo (se richiedente asilo o titolare di protezione internazionel) dell'utente che ha effettuato il login.
 */
public class UserLogin
{
    private static String username;
    private static String nome;
    private static String tipoAsiloProtezione;

    public static String getUsername()
    {
        return username;
    }
    public static String getNome(){return nome;}
    public static void setNome(String n){nome=n;}
    public static void setUsername(String usernameAdd)
    {
        username=usernameAdd;
    }

    public static String getTipoAsiloProtezione() {
        return tipoAsiloProtezione;
    }

    public static void setTipoAsiloProtezione(String tipoAsiloProtezione) {
        UserLogin.tipoAsiloProtezione = tipoAsiloProtezione;
    }
}

