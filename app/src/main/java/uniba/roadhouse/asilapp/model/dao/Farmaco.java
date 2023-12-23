package uniba.roadhouse.asilapp.model.dao;

public class Farmaco {
    private String nome;
    private String nota;

    public Farmaco(String nome, String nota)
    {
        this.nome=nome;
        this.nota=nota;
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

}
