package uniba.roadhouse.asilapp.model.dao;

/**
 * Classe singleton che memorizza l'username del dottore che ha effettuato il login.
 */
public class DoctorLogin {
    private static String username;
    public static void setUsername(String usernameInput)
    {
        username = usernameInput;
    }
    public static String getUsername()
    {
        return username;
    }
}
