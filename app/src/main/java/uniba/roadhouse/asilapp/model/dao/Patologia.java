package uniba.roadhouse.asilapp.model.dao;


import com.google.firebase.Timestamp;

public class Patologia {
    String username;
    String patologia;
    String priorita;
    String nota;
    String data;
    String ora;

    public Patologia(String username, String patologia, String priorita, String data, String ora, String nota){
        this.username=username;
        this.patologia=patologia;
        this.data=data;
        this.ora=ora;
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

    public String getData() {
        return data;
    }
    public String getOra() {
        return ora;
    }

    public void setData(String data) {
        this.data=data;
    }
    public void setOra(String ora) {
        this.ora=ora;
    }
}
