package uniba.roadhouse.asilapp.model.dao;

/**
 * Classe singleton che memorizza l'username dell'utente che ha effettuato il login.
 */
public class Access
{
    private static String username;
    public static String getUsername()
    {
        return username;
    }
    public static void setUsername(String usernameAdd)
    {
        username=usernameAdd;
    }
}

