package uniba.roadhouse.asilapp.model.dao;

import android.graphics.Bitmap;

public class Articolo {
    private String titolo;
    private String testo;
    private String tipo;
    private Bitmap immagine;

    public Articolo(String titolo, String testo, String tipo, Bitmap immagine) {
        this.titolo = titolo;
        this.testo = testo;
        this.tipo = tipo;
        this.immagine = immagine;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Bitmap getImmagine() {
        return immagine;
    }

    public void setImmagine(Bitmap immagine) {
        this.immagine = immagine;
    }
}
