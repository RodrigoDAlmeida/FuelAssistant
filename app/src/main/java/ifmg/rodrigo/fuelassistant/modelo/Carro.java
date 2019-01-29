package ifmg.rodrigo.fuelassistant.modelo;

/**
 * Created by Rodrigo on 29/08/2016.
 */
public class Carro {

    long codigo;
    ModeloCarro modelo;
    int ano;

    public Carro(){

    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public ModeloCarro getModelo() {
        return modelo;
    }

    public void setModelo(ModeloCarro modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    @Override
    public String toString() {
        return  modelo + "  " + ano;

    }
}
