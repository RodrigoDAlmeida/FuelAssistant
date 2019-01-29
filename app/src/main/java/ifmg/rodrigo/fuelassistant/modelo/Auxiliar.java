package ifmg.rodrigo.fuelassistant.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rodrigo on 09/09/2016.
 */
public class Auxiliar {

    private long codigo;
    private Date data;
    private double nvCombustAnterior;

    private TipoCombustivel tipoCombustivel;
    private Usuario usuario;
    private Carro carro;

    private List<Corrida> corridas = new ArrayList<>();

    public Auxiliar() {

        nvCombustAnterior = 0;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public double getDistancia() {

        Double distancia = 0d;


        for (Corrida c : corridas){

            distancia += c.getDistanciaPercorrida();

        }

        return distancia;

    }


    public double getNvCombustAnterior() {
        return nvCombustAnterior;
    }

    public List<Corrida> getCorridas() {
        return corridas;
    }

    public void setCorridas(List<Corrida> corridas) {
        this.corridas = corridas;
    }

    public void setNvCombustAnterior(double nvCombustAnterior) {
        this.nvCombustAnterior = nvCombustAnterior;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }



    public TipoCombustivel getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(TipoCombustivel tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    @Override
    public String toString() {
        return "Auxiliar{" +
                "codigo=" + codigo +
                ", tipoCombustivel=" + tipoCombustivel +
                ", usuario=" + usuario +
                ", carro=" + carro +
                '}';
    }


    public Double getMediaAberturaBorboleta(){

        Double somaMediaAbertura = 0d;

        for (Corrida c : corridas){

            somaMediaAbertura += c.getMediaAceleracao();

        }

        return somaMediaAbertura / (corridas.size());

    }

    public Double getMediaConsumoTotal(){

        Double somaMediaConsumo = 0d;

        for (Corrida c : corridas){

            somaMediaConsumo += c.getMediaConsumo();

        }

        return somaMediaConsumo / (corridas.size());
    }

    public void reiniciaValores(){

        corridas = new ArrayList<>();

    }
}
