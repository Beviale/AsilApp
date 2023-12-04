package uniba.roadhouse.asilapp.controller;

//classe che rappresenta l'eccezzione lanciata quando un operazione al db non va a buon fine
public class DatabaseException extends Exception {
    public DatabaseException(String errorMessage) {
        super(errorMessage);
    }
}