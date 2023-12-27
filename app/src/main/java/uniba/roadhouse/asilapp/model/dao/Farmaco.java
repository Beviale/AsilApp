package uniba.roadhouse.asilapp.model.dao;

/**
 * Classe che rappresenta un farmaco associato ad una patologia.
 */
public class Farmaco {
    private String nome;
    private String nota;
    private String username;
    private String patologia;

    public Farmaco(String nome, String nota, String username, String patologia)
    {
        this.nome=nome;
        this.nota=nota;
        this.username=username;
        this.patologia=patologia;
    }

    public String getNome()
    {
        return this.nome;
    }

    public String getNota()
    {
        return this.nota;
    }

    public void setNome(String nome)
    {
        this.nome=nome;
    }


    public void setNota(String nota)
    {
        this.nota=nota;
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

}
