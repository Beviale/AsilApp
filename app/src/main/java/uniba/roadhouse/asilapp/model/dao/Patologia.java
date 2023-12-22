package uniba.roadhouse.asilapp.model.dao;


import com.google.firebase.Timestamp;

public class Patologia {
    String username;
    String patologia;
    String priorita;
    String nota;
    Timestamp dataEora;

    public Patologia(String username, String patologia, String priorita, Timestamp dataEora, String nota){
        this.username=username;
        this.patologia=patologia;
        this.dataEora=dataEora;
        this.nota=nota;
        this.priorita=priorita;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPatologia() {
        return patologia;
    }

    public void setPatologia(String patologia) {
        this.patologia = patologia;
    }

    public String getPriorita() {
        return priorita;
    }

    public void setPriorita(String priorita) {
        this.priorita = priorita;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Timestamp getDataEora() {
        return dataEora;
    }

    public void setDataEora(Timestamp dataEora) {
        this.dataEora = dataEora;
    }
}
