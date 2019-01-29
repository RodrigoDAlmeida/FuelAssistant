package ifmg.rodrigo.fuelassistant.modelo;


/**
 * Created by Rodrigo on 15/08/2016.
 */
public class Fabricante {

    private long codigo;
    private String nome;


    public Fabricante() {

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

    public Fabricante(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fabricante)) return false;

        Fabricante that = (Fabricante) o;

        return codigo == that.codigo;

    }

    @Override
    public int hashCode() {
        return (int) (codigo ^ (codigo >>> 32));
    }

    @Override
    public String toString() {
        return  codigo +
                "  " + nome;
    }
}
