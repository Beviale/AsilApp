package uniba.roadhouse.asilapp.model.dao;

/**
 * Classe singleton che rappresenta l'utente che intende registrarsi.
 * Contiene tutti i vari dati necessari alla registrazione.
 */
public class UserSignup {
    private static String name;
    private static String surname;
    private static String gender;
    private static String birthDate;
    private static String typeUser;
    private static String citizen;
    private static String country;
    private static String cityOrganization;
    private static String nameOrganization;
    private static String username;
    private static String password;






    public static String getName() {
        return name;
    }

    public static void setName(String nameAdd) {
        name=nameAdd;
    }


    public static String getSurname() {
        return surname;
    }

    public static void setSurname(String surnameAdd) {
        surname=surnameAdd;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String genderAdd) {
        gender=genderAdd;
    }

    public static String getBirthDate() {
        return birthDate;
    }

    public static void setBirthDate(String birthDateAdd) {
        birthDate=birthDateAdd;
    }


    public static String getTypeUser() {
        return typeUser;
    }

    public static void setTypeUser(String typeUserAdd) {
        typeUser=typeUserAdd;
    }

    public static String getCitizen() {
        return citizen;
    }

    public static void setCitizen(String citizenAdd) {
        citizen=citizenAdd;
    }

    public static String getCountry() {
        return country;
    }

    public static void setCountry(String countryAdd) {
        country=countryAdd;
    }

    public static String getCityOrganization() {
        return cityOrganization;
    }

    public static void setCityOrganization(String cityOrganizationAdd) {
        cityOrganization=cityOrganizationAdd;
    }

    public static String getNameOrganization() {
        return nameOrganization;
    }

    public static void setNameOrganization(String nameOrganizationAdd) {
        nameOrganization=nameOrganizationAdd;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String usernameAdd) {
        username=usernameAdd;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String passwordAdd) {
        password=passwordAdd;
    }

}
