package ifmg.rodrigo.fuelassistant.modelo;


/**
 * Created by Rodrigo on 15/08/2016.
 */
public class ModeloCarro {

    private long codigo;
    private String nome;
    private CategoriaEnum categoria;
    private Fabricante fabricante;

    public ModeloCarro() {

    }

    public ModeloCarro(String nome, CategoriaEnum categoria, Fabricante fabricante) {
        this.nome = nome;
        this.categoria = categoria;
        this.fabricante = fabricante;
    }

    public CategoriaEnum getCategoria() {
        return categoria;
    }

    public Fabricante getFabricante() {
        return fabricante;
    }

    public void setFabricante(Fabricante fabricante) {
        this.fabricante = fabricante;
    }

    public void setCategoria(CategoriaEnum categoria) {
        this.categoria = categoria;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModeloCarro)) return false;

        ModeloCarro that = (ModeloCarro) o;

        return codigo == that.codigo;

    }

    @Override
    public int hashCode() {
        return (int) (codigo ^ (codigo >>> 32));
    }

    @Override
    public String toString() {
        return  fabricante.getNome() +
                "  " + nome +" "+ categoria;
    }
}
