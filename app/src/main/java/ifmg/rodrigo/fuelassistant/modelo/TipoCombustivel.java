package ifmg.rodrigo.fuelassistant.modelo;

/**
 * Created by Rodrigo on 09/09/2016.
 */
public class TipoCombustivel {

    private long codigo;
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public TipoCombustivel() {
    }

    public TipoCombustivel(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return  codigo +
                "  " + nome;
    }
}
