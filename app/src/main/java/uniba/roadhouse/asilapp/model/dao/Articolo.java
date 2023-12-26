package uniba.roadhouse.asilapp.model.dao;

import android.graphics.Bitmap;

public class Articolo {
    private String titolo;
    private String testo;
    private Bitmap immagine;
    private Integer id;

    public Articolo(Integer id, String titolo, String testo, Bitmap immagine) {
        this.titolo = titolo;
        this.testo = testo;
        this.immagine = immagine;
        this.id=id;
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

    public Bitmap getImmagine() {
        return immagine;
    }

    public void setImmagine(Bitmap immagine) {
        this.immagine = immagine;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
