package uniba.roadhouse.asilapp.model.dao;

/**
 * Classe singleton che memorizza l'username e il nome dell'utente che ha effettuato il login.
 */
public class Access
{
    private static String username;
    private static String nome;
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
}

