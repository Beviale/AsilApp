package uniba.roadhouse.asilapp.model.dao;

import com.google.firebase.Timestamp;

import java.sql.Time;

import uniba.roadhouse.asilapp.controller.TipoMisurazioneEnum;

public class Misurazione {
    private String username;
    private Double valore;
    private Double valoreMax;
    private Double valoreMin;
    private Timestamp data;
    private TipoMisurazioneEnum tipo;
    private String notaMedico;
    private String valutazione;
    private Integer id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getValore() {
        return valore;
    }

    public void setValore(Double valore) {
        this.valore = valore;
    }

    public Double getValoreMax() {
        return valoreMax;
    }

    public void setValoreMax(Double valoreMax) {
        this.valoreMax = valoreMax;
    }

    public Double getValoreMin() {
        return valoreMin;
    }

    public void setValoreMin(Double valoreMin) {
        this.valoreMin = valoreMin;
    }

    public Timestamp getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }

    public TipoMisurazioneEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoMisurazioneEnum tipo) {
        this.tipo = tipo;
    }

    public String getNotaMedico() {
        return notaMedico;
    }

    public void setNotaMedico(String notaMedico) {
        this.notaMedico = notaMedico;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValutazione() {
        return valutazione;
    }

    public void setValutazione(String valutazione) {
        this.valutazione = valutazione;
    }


    public Misurazione(String username, String valutazione, Double valore, Double valoreMax, Double valoreMin, Timestamp data, TipoMisurazioneEnum tipo, String notaMedico, Integer id) {
        this.username = username;
        this.valutazione=valutazione;
        this.valore = valore;
        this.valoreMax = valoreMax;
        this.valoreMin = valoreMin;
        this.data = data;
        this.tipo = tipo;
        this.notaMedico = notaMedico;
        this.id = id;
    }
}
