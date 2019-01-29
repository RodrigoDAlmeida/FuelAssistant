package ifmg.rodrigo.fuelassistant.modelo;

/**
 * Created by Rodrigo on 29/08/2016.
 */
public class Usuario {

    long codigo;
    String nome;

    public Usuario(){

    }

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

    @Override
    public String toString() {
        return codigo +
                "  " + nome;
    }
}
