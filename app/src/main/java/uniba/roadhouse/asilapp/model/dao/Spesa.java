package uniba.roadhouse.asilapp.model.dao;

import com.google.firebase.Timestamp;

import uniba.roadhouse.asilapp.controller.other.CategoriaSpesaEnum;

public class Spesa {
    private CategoriaSpesaEnum categoria;
    private Double costo;
    private Timestamp data;
    private String username;

    public Spesa(CategoriaSpesaEnum categoria, Double costo, Timestamp data, String username) {
        this.categoria = categoria;
        this.costo = costo;
        this.data = data;
        this.username=username;
    }

    public CategoriaSpesaEnum getCategoria() {
        return categoria;
    }

    public Double getCosto() {
        return costo;
    }

    public Timestamp getData() {
        return data;
    }

    public String getUsername(){
        return username;
    }
}
